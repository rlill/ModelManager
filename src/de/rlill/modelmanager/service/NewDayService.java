package de.rlill.modelmanager.service;

import java.util.ArrayList;
import java.util.List;

import android.graphics.AvoidXfermode;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import de.rlill.modelmanager.MainActivity;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.dialog.NewDayProgress;
import de.rlill.modelmanager.model.CompanyCar;
import de.rlill.modelmanager.model.Event;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.ModelTraining;
import de.rlill.modelmanager.model.MovieModel;
import de.rlill.modelmanager.model.Movieproduction;
import de.rlill.modelmanager.model.Team;
import de.rlill.modelmanager.model.Today;
import de.rlill.modelmanager.persistance.CompanyCarDbAdapter;
import de.rlill.modelmanager.persistance.EventDbAdapter;
import de.rlill.modelmanager.persistance.ModelTrainingDbAdapter;
import de.rlill.modelmanager.persistance.MovieModelDbAdapter;
import de.rlill.modelmanager.persistance.TodayDbAdapter;
import de.rlill.modelmanager.service.ModelService.Statistics;
import de.rlill.modelmanager.service.TodayService.TeamWork;
import de.rlill.modelmanager.struct.CarAction;
import de.rlill.modelmanager.struct.CarStatus;
import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.Game;
import de.rlill.modelmanager.struct.ModelStatus;
import de.rlill.modelmanager.struct.MovieStatus;
import de.rlill.modelmanager.struct.RejectReasons;
import de.rlill.modelmanager.struct.TodayStatus;
import de.rlill.modelmanager.struct.TrainingStatus;
import de.rlill.modelmanager.struct.ViewElements;
import de.rlill.modelmanager.struct.Weekday;

public class NewDayService extends AsyncTask<Void, Void, Void> {

	private static final String LOG_TAG = "MM*" + NewDayService.class.getSimpleName();

	private NewDayProgress progress;
	private MainActivity context;
	private ViewElements viewElements;

	private boolean notificationSalary;
	private boolean notificationSickness;
	private boolean notificationTraining;

	public NewDayService(MainActivity ctx, ViewElements ve,
			boolean noteSal, boolean noteSick, boolean noteTrain) {
		context = ctx;
		viewElements = ve;
		notificationSalary = noteSal;
		notificationSickness = noteSick;
		notificationTraining = noteTrain;
	}

	@Override
	protected void onPreExecute() {
		progress = new NewDayProgress(context);
		progress.show();
	}

	@Override
	protected Void doInBackground(Void... arg0) {

		List<Model> allModels = ModelService.getAllModels();
		progress.setMax(2 * allModels.size());
		progress.show();

		int abalance = TransactionService.getBalance(0);

		// pre-read today's trainings
		SparseArray<ModelTraining> trainings = new SparseArray<ModelTraining>();
		for (ModelTraining training : ModelTrainingDbAdapter.getTrainingsScheduled(DiaryService.today())) {
			Log.d(LOG_TAG, "Training active today: " + training);
			trainings.put(training.getModelId(), training);
		}

		// movies in progress today
		SparseArray<MovieModel> movieParticipation = new SparseArray<MovieModel>();
		Log.i(LOG_TAG, "MOVIES # # #");
		for (Movieproduction mpr : MovieService.getCurrentMovies()) {
			Log.i(LOG_TAG, "Processing " + mpr.getName());

			EventFlag flag = MovieService.flagForMovieType(mpr.getType());

			if (mpr.getStatus() == MovieStatus.PLANNED) {
				// pay, log, setStatus
				Event event = EventDbAdapter.getAllEvents(EventClass.MOVIE_START, flag).get(0);
				int startCost = Util.niceRandom(event.getAmountMin(), event.getAmountMax());
				String msg = event.getDescription().replace("%M", mpr.getName());
				TransactionService.transfer(0, -1, startCost, msg);

				DiaryService.log(msg, EventClass.MOVIE_START, flag, mpr.getId(), startCost);

				MovieService.setMovieStatus(mpr.getId(), MovieStatus.IN_PROGRESS);
			}

			if (DiaryService.todayWeekday() != Weekday.SUNDAY) {
				// daily production cost
				Event event = EventDbAdapter.getAllEvents(EventClass.MOVIE_PROGRESS, flag).get(0);
				Log.i(LOG_TAG, "MOVIE PROGRESS: " + event);
				int prodCost = Util.niceRandom(event.getAmountMin(), event.getAmountMax());
				String msg = event.getDescription().replace("%M", mpr.getName());
				TransactionService.transfer(0, -1, prodCost, msg);

				DiaryService.log(msg, EventClass.MOVIE_PROGRESS, flag, mpr.getId(), prodCost);
			}
			for (MovieModel mm : MovieService.getModelsForMovie(mpr.getId(), DiaryService.today())) {
				Log.i(LOG_TAG, "PARTICIPATE: " + mm.getModelId());
				movieParticipation.put(mm.getModelId(), mm);

				Model model = ModelService.getModelById(mm.getModelId());
				if (model.getStatus() != ModelStatus.MOVIEPROD)
					ModelService.setModelStatus(mm.getModelId(), ModelStatus.MOVIEPROD);
			}
		}

		// Movie rental income
		for (Movieproduction mpr : MovieService.getRentedMovies()) {
			Log.i(LOG_TAG, "Processing rent for " + mpr.getName());

			int mat = mpr.getEndDay() - DiaryService.today();
			Log.i(LOG_TAG, "Age: " + mat);
			if (mat % 7 != 0) continue;

			int fee = 0;
			if (mat < 70)
				fee = mpr.getPrice() / 5;
			else {
				MovieService.setMovieStatus(mpr.getId(), MovieStatus.SOLD);
				continue;
			}

			String msg = MessageService.getMessage(R.string.display_msg_movie_rented).replace("%M", mpr.getName());
			TransactionService.transfer(-1, 0, fee, msg);
		}

		int [] mix = Util.randomArray(allModels.size());
        int hiredCount = 0;
        int applicationCount = 0;
		for (int i = 0; i < allModels.size(); i++) {
			Model model = allModels.get(mix[i]);
			int avgCompareSalary = ModelService.getAverageSalary(model.getId());
			int expectedBonus = ModelService.getExpectedBonus(model.getId());
			int trainingCost = 0;

			progress.incrementProgressBy(1);

			if (model.getStatus() != ModelStatus.TRAINING) {
				ModelTraining training = trainings.get(model.getId());
				if (training != null) {
					// Training must start today
					Log.i(LOG_TAG, model + " starts training today " + training.getTraining().getDescription());
					TransactionService.transfer(0, -1, training.getPrice(),
							training.getTraining().getDescription() + " - " + model.getFullname());
					TrainingService.setTrainingStatus(training.getId(), TrainingStatus.IN_PROGRESS);
					ModelService.reportTraining(model.getId());
					trainingCost = training.getPrice();
				}
			}

			Statistics st;
			switch (model.getStatus()) {
			case HIRED:
                hiredCount++;
				// quit
				if (model.getMood() < 20 && model.getSalary() < avgCompareSalary && expectedBonus > 0) {
					ModelService.release(model.getId());
					EventService.newNotification(model.getId(), EventFlag.QUIT, 0);
				}

				// become sick
				if ((DiaryService.todayWeekday() != Weekday.SUNDAY) && (Util.rnd(model.getHealth()) < 5)) {
					ModelService.reportSick(model.getId());
					if (notificationSickness) {
						Today t = EventService.newNotification(model.getId(), EventFlag.SICK, 0);
						DiaryService.log(t);
					}
					else {
						Event event = EventDbAdapter.getAllEvents(EventClass.NOTIFICATION, EventFlag.SICK).get(0);
						Today today = new Today();
						today.setModelId(model.getId());
						today.setEventId(event.getId());
						DiaryService.log(today);
					}
				}

				break;
			case SICK:
				MovieModel mm = movieParticipation.get(model.getId());

				int prevPrice = 0;
				if (mm != null) {
					for (MovieModel tmm : MovieModelDbAdapter.getMovieModels(mm.getMovieId(), model.getId(), 0)) {
						if (tmm.getPrice() > prevPrice) prevPrice = tmm.getPrice();
					}
				}
				if (Util.rnd(model.getHealth()) > 5) {

					// healthy again
					if (mm != null) {
						ModelService.setModelStatus(model.getId(), ModelStatus.MOVIEPROD);

						// restore prevPrice
						mm.setPrice(prevPrice);
						MovieModelDbAdapter.updateMovieModel(mm);

						Movieproduction mpr = MovieService.getMovie(mm.getMovieId());
						Log.i(LOG_TAG, "MOVIE # # # " + model.getFullname() + " in movie " + mpr.getName());

						if (DiaryService.todayWeekday() != Weekday.SUNDAY) {
							// pay, log
							Event event = EventDbAdapter.getAllEvents(EventClass.MOVIE_CAST, EventFlag.MOVIE).get(0);
							String msg = event.getDescription().replace("%M", mpr.getName());
							TransactionService.transfer(0, model.getId(), prevPrice, msg);

							DiaryService.log(msg, EventClass.MOVIE_CAST, EventFlag.MOVIE, model.getId(), prevPrice);
						}
					}
					else
						ModelService.setModelStatus(model.getId(), ModelStatus.HIRED);

				} else {
					ModelService.reportSick(model.getId());
					DiaryService.log(MessageService.getMessage(R.string.logmessage_sick_today, model),
							EventClass.NOTIFICATION, EventFlag.SICK, model.getId(), 0);

					if (mm != null) {
						// plan model for same movie tomorrow
						MovieService.addModelForMovie(mm.getMovieId(), model.getId(), DiaryService.today() + 1, prevPrice);
					}
				}
				break;
			case TRAINING:
				ModelTraining mt = TrainingService.getCurrentTraining(model.getId());
				if (mt != null) {
					if (mt.getStartDay() <= DiaryService.today() && mt.getEndDay() >= DiaryService.today()) {
						// training continues
						if (DiaryService.todayWeekday() != Weekday.SATURDAY && DiaryService.todayWeekday() != Weekday.SUNDAY) {
							ModelService.reportTraining(model.getId());
							DiaryService.log(MessageService.getMessage(R.string.logmessage_training_today, model, mt.getTraining()),
								EventClass.ACCEPT, EventFlag.TRAINING, model.getId(), trainingCost);
						}
					}
					else {
						// training finished
						TrainingService.setTrainingStatus(mt.getId(), TrainingStatus.FINISHED);
						String msg = MessageService.trainingFinishedReport(context, model, mt.getTraining());
						ModelService.returnFromTraining(model.getId(), mt);
						int logId = DiaryService.log(msg, EventClass.NOTIFICATION, EventFlag.TRAINING_FIN, model.getId(), mt.getPrice());
						if (notificationTraining) {
							EventService.newNotification(model.getId(), EventFlag.TRAINING_FIN, mt.getPrice(), logId);
						}
					}
				}
				break;
			case VACATION:
				if (model.getVacrest() > 0) {
					// TODO: return from vacation earlier?
					ModelService.grantVacation(model.getId());
				}
				else {
					ModelService.reportHealthy(model.getId());
				}
				break;
			case FREE:
				st = ModelService.getStatistics(model.getId());
				if (Util.rnd(4) == 0 && (st.latestAvailable == 0 || st.latestAvailable < DiaryService.today() - 14)) {
					// become unavailable
					ModelService.setModelStatus(model.getId(), ModelStatus.UNAVAILABLE);
				}
				break;
			case UNAVAILABLE:
				st = ModelService.getStatistics(model.getId());
				if (Util.rnd(4) == 0 && (st.latestUnavailable == 0 || st.latestUnavailable < DiaryService.today() - 14)) {
					// become available
					ModelService.setModelStatus(model.getId(), ModelStatus.FREE);
				}
				break;
			case MOVIEPROD:

				mm = movieParticipation.get(model.getId());
				if (mm == null) {
					Log.e(LOG_TAG, "Unknown movie-participation " + model);
					break;
				}

				if (Util.rnd(model.getHealth()) < 4) {
					// become sick
					ModelService.reportSick(model.getId());
					if (notificationSickness) {
						Today t = EventService.newNotification(model.getId(), EventFlag.SICK, 0);
						DiaryService.log(t);
					}
					else {
						Event event = EventDbAdapter.getAllEvents(EventClass.NOTIFICATION, EventFlag.SICK).get(0);
						Today today = new Today();
						today.setModelId(model.getId());
						today.setEventId(event.getId());
						DiaryService.log(today);
					}

					// store salary=0 for today
					int oldPrice = mm.getPrice();
					mm.setPrice(0);
					MovieModelDbAdapter.updateMovieModel(mm);

					// plan model for same movie tomorrow
					MovieService.addModelForMovie(mm.getMovieId(), model.getId(), DiaryService.today() + 1, oldPrice);

				}
				else {
					Movieproduction mpr = MovieService.getMovie(mm.getMovieId());
					Log.i(LOG_TAG, "MOVIE # # # " + model.getFullname() + " in movie " + mpr.getName());

					if (DiaryService.todayWeekday() == Weekday.SUNDAY) {
						MovieModelDbAdapter.deleteMovieModel(mm.getId());
					}
					else {
						// pay, log
						Event event = EventDbAdapter.getAllEvents(EventClass.MOVIE_CAST, EventFlag.MOVIE).get(0);
						String msg = event.getDescription().replace("%M", mpr.getName());
						TransactionService.transfer(0, model.getId(), mm.getPrice(), msg);

						DiaryService.log(msg, EventClass.MOVIE_CAST, EventFlag.MOVIE, model.getId(), mm.getPrice());
					}
					// plan model for same movie tomorrow
					MovieService.addModelForMovie(mm.getMovieId(), model.getId(), DiaryService.today() + 1, mm.getPrice());
				}
			}

			if (model.getStatus() == ModelStatus.FREE || model.getStatus() == ModelStatus.UNAVAILABLE) continue;

			// paycheck - spendings
			if (model.getPayday() == DiaryService.todayWeekday()) {
				if (notificationSalary) {
					Today t = EventService.newNotification(model.getId(), EventFlag.PAYCHECK, model.getSalary());
					TransactionService.transfer(0, model.getId(), model.getSalary(), t.getEvent().getNoteAcct());
					DiaryService.log(t);
				}
				else {
					Event event = EventDbAdapter.getAllEvents(EventClass.NOTIFICATION, EventFlag.PAYCHECK).get(0);
					TransactionService.transfer(0, model.getId(), model.getSalary(), event.getNoteAcct());
					Today today = new Today();
					today.setModelId(model.getId());
					today.setEventId(event.getId());
					today.setAmount1(model.getSalary());
					DiaryService.log(today);
				}

				// spendings
				int spendings = Util.rnd(model.getSalary() / 2) + model.getSalary() / 2;
				String msg = context.getResources().getString(R.string.labelSpendings);
				TransactionService.transfer(model.getId(), -1, spendings, msg);
			}

			// fill up vacation account
			if ((DiaryService.today() - model.getHireday() % 28) == 0) {
				ModelService.setVacationRest(model.getId(), model.getVacation());
			}

		} // for each model

        // set of models that could apply if they wanted.
        // in case of new game, force some to apply while the number of hired
        // models is very low
        List<Model> hireables = new ArrayList<Model>();


		if (DiaryService.todayWeekday() != Weekday.SUNDAY) {

			SparseArray<TeamWork> teamWork = new SparseArray<TeamWork>();

			mix = Util.randomArray(allModels.size());
			for (int i = 0; i < allModels.size(); i++) {
				Model model = allModels.get(mix[i]);

				progress.incrementProgressBy(1);

				if (model.getStatus() == ModelStatus.FREE) {
					// maybe apply
					if (Util.rnd(20) == 1) {
                        ModelService.makeApplication(model.getId());
                        applicationCount++;
                    }
                    else hireables.add(model);
				}

				// win/lose events
				if ((model.getStatus() == ModelStatus.HIRED || model.getStatus() == ModelStatus.MOVIEPROD
						|| model.getStatus() == ModelStatus.VACATION) && Util.rnd(5) == 2) {
					Event e = EventService.getRandomExtraEvent();
					if (e == null) continue;

					Today t = new Today();
					t.setStatus(TodayStatus.NEW);
					t.setEventId(e.getId());
					int amount = Util.niceRandom(e.getAmountMin(), e.getAmountMax());
					if (e.getMaxpercent() > 0 && amount > abalance * e.getMaxpercent() / 100)
						amount = Util.niceRound(abalance * e.getMaxpercent() / 100);
					t.setAmount1(amount);
					t.setAmount2(Util.interpolation(e.getAmountMin(), amount));
					if (e.getFlag() == EventFlag.WIN_PERSON || e.getFlag() == EventFlag.LOSE_PERSON
							|| e.getFlag() == EventFlag.PAYFIX_PERSON
							|| e.getFlag() == EventFlag.PAYVAR_PERSON
							|| e.getFlag() == EventFlag.PAYOPT_PERSON
							) {
						Model m = ModelService.findModelForEvent(e);
						if (m == null) continue;
						t.setModelId(m.getId());

						if (e.getEclass() == EventClass.EXTRA_LOSS && e.getFlag() == EventFlag.PAYFIX_PERSON) {
							// transfer money to model now - can be reclaimed later
							TransactionService.transfer(0, m.getId(), amount, t.getNoteAcct());
							DiaryService.log(e.getNoteFile(), e.getEclass(), e.getFlag(), model.getId(), amount);
						}

						if (e.getEclass() == EventClass.EXTRA_LOSS && e.getFlag() == EventFlag.LOSE_PERSON) {
							// pay now - can be reclaimed later
							TransactionService.transfer(0, -1, amount, t.getNoteAcct());
							DiaryService.log(e.getNoteFile(), e.getEclass(), e.getFlag(), model.getId(), amount);
						}
					}
					TodayDbAdapter.addToday(t);
				}

				// request raise
				int avgCompareSalary = (int)(ModelService.getAverageSalary(model.getId())
						* ((double)1 + model.getAmbition() / 400));
				if (ModelService.isModelHired(model) && model.getSalary() <= avgCompareSalary && Util.rnd(model.getAmbition()) > 10) {
					int expectMin = (int)(model.getSalary() * 1.05);
					EventService.newRaiseSalaryRequest(model.getId(),
							Util.niceRandom(expectMin, (int)(avgCompareSalary * 1.2)), expectMin);
				}

				if (model.getStatus() != ModelStatus.HIRED) continue;

				// car broken/stolen
				if (model.getCarId() > 0) {
					Today t;
					switch (Util.rnd(70)) {
					case 1:
					case 2:
						CarService.log(model.getCarId(), CarAction.BREAKDOWN,
								MessageService.getMessage(R.string.logmessage_car_breakdown),
								CarService.getCarValue(model.getCarId()));
						CarService.setCarStatus(model.getCarId(), CarStatus.DEFECT);
						t = EventService.newNotification(model.getId(), EventFlag.CAR_BROKEN,
								CarService.getCarRepairCost(model.getCarId()));
						DiaryService.log(t);
						break;
					case 3:
						CarService.log(model.getCarId(), CarAction.ACCIDENT,
								MessageService.getMessage(R.string.logmessage_car_wrecked),
								CarService.getCarValue(model.getCarId()));
						CarService.setCarStatus(model.getCarId(), CarStatus.WRECKED);
						t = EventService.newNotification(model.getId(), EventFlag.CAR_WRECKED, 0);
						DiaryService.log(t);
						break;
					case 4:
						CarService.log(model.getCarId(), CarAction.THEFT,
								MessageService.getMessage(R.string.logmessage_car_stolen),
								CarService.getCarValue(model.getCarId()));
						CarService.setCarStatus(model.getCarId(), CarStatus.STOLEN);
						t = EventService.newNotification(model.getId(), EventFlag.CAR_STOLEN, 0);
						int logId = DiaryService.log(t);
						t.setAmount2(logId);
						TodayDbAdapter.updateToday(t);
						ModelService.setCar(model.getId(), 0);
						break;
					}
				}

				// request bonus
				Statistics st = ModelService.getStatistics(model.getId());
				int expectedBonus = ModelService.getExpectedBonus(model.getId());
				if (expectedBonus > 0) {
					Log.d(LOG_TAG, "Expecting bonus " + expectedBonus + " - " + model);
					EventService.newBonusRequest(model.getId(), Util.niceRandom(expectedBonus, 2 * expectedBonus), expectedBonus);
				}

				// request quit
				if (model.getHireday() < DiaryService.today() - 14
						&& (model.getSalary() < avgCompareSalary || expectedBonus > 0)
						&& Util.rnd(model.getMood()) < 10) {
					EventService.newQuitRequest(model.getId(), expectedBonus, avgCompareSalary);
				}

				// request vacation
				if (model.getVacrest() > 0
						&& st.lastVacation < DiaryService.today() - 5
						&& Util.rnd(model.getAmbition()) < 5) {
					EventService.newVacationRequest(model.getId());
				}

				// request training
				if (st.lastTraining < DiaryService.today() - 28
						&& Util.rnd(model.getAmbition()) > 20
						&& ModelService.plannedTrainingForModel(model.getId()) != null) {
					EventService.newTrainingRequest(model.getId());
				}

				// request car-update
				boolean needCar = false;
				if (model.getCarId() == 0) {
					needCar = true;
				}
				else {
					CompanyCar cc = CompanyCarDbAdapter.getCompanyCar(model.getCarId());
					if (cc.getStatus() == CarStatus.STOLEN
							|| cc.getStatus() == CarStatus.WRECKED
							|| (cc.getStatus() == CarStatus.IN_USE
								&& cc.getBuyday() < DiaryService.today() - 84
								&& Util.rnd(100) < 10)) {
						needCar = true;
					}
				}
				if (needCar) {
					EventService.newCarRequest(model.getId());
				}

				if (!ModelService.isActiveTeamLeader(model.getId())) {
					// Booking
					Today today = null;
					int price = 0;
					int pmax = 0;
					switch (Util.rnd(3)) {
					case 0:
						today = EventService.bookingEvent(EventFlag.PHOTO);
						price = Util.niceRandom(today.getEvent().getAmountMin(), today.getEvent().getAmountMax())
								* model.getQuality_photo() / 10;
						pmax = today.getEvent().getAmountMax() * (model.getQuality_photo() / 10) * ((Util.rnd(10) + 11) / 10);
						break;
					case 1:
						today = EventService.bookingEvent(EventFlag.MOVIE);
						price = Util.niceRandom(today.getEvent().getAmountMin(), today.getEvent().getAmountMax())
								* model.getQuality_movie() / 10;
						pmax = today.getEvent().getAmountMax() * (model.getQuality_movie() / 10) * ((Util.rnd(10) + 11) / 10);
						break;
					}
					if (today != null) {
						today.setModelId(model.getId());

						TeamWork tw = teamWork.get(model.getTeamId());
						if (tw == null) {
							tw = new TeamWork();
							teamWork.put(model.getTeamId(), tw);
						}

						if (ModelService.isManagedTeamMember(model.getId())
								&& ModelService.isModelBookableToday(model.getId(), today.getEvent().getFlag())) {
							RejectReasons rr = ModelService.bookingRejectReasons(model.getId());
							if (rr.willReject()) {
								EventService.rejectBooking(today);
								today.setAmount1(price);
								today.setAmount2(pmax);
								TodayDbAdapter.addToday(today);
								Log.i(LOG_TAG, "Team " + model.getTeamId() + " booking rejected: " + model);
							}
							else {
								// do the booking right away
								today.setAmount1(pmax);
								ModelService.reportBooking(today);
								TransactionService.transfer(-1, 0, pmax, today.getNoteAcct());
								DiaryService.fileLog(today);

								tw.bookings++;
								tw.earnings += pmax;
								Log.i(LOG_TAG, "Team " + model.getTeamId() + " event " + tw.bookings + " " + Util.amount(pmax) + " - " + model);
							}
						}
						else {
							today.setModelId(-1);
							today.setAmount1(price);
							today.setAmount2(pmax);
							TodayDbAdapter.addToday(today);
							Log.i(LOG_TAG, "Team " + model.getTeamId() + " not managed " + model
									+ " mg: " + ModelService.isManagedTeamMember(model.getId())
									+ " bk: " + ModelService.isModelBookableToday(model.getId(), today.getEvent().getFlag()));
						}
					}
				}


				// additional Booking
				Today today = null;
				switch (Util.rnd(3)) {
				case 0:
					today = EventService.bookingEvent(EventFlag.PHOTO);
					break;
				case 1:
					today = EventService.bookingEvent(EventFlag.MOVIE);
					break;
				}
				if (today != null) {
					Model m = ModelService.findModelForEvent(today.getEvent());
					if (m != null) {
						int price = 0;
						int pmax = 0;
						if (today.getEvent().getFlag() == EventFlag.PHOTO) {
							price = Util.niceRandom(today.getEvent().getAmountMin(), today.getEvent().getAmountMax())
									* m.getQuality_photo() / 10;
							pmax = today.getEvent().getAmountMax() * (m.getQuality_photo() / 10) * ((Util.rnd(10) + 11) / 10);
						} else {
							price = Util.niceRandom(today.getEvent().getAmountMin(), today.getEvent().getAmountMax())
									* m.getQuality_movie() / 10;
							pmax = today.getEvent().getAmountMax() * (m.getQuality_movie() / 10) * ((Util.rnd(10) + 11) / 10);
						}
						today.setModelId(m.getId());

						TeamWork tw = teamWork.get(model.getTeamId());
						if (tw == null) {
							tw = new TeamWork();
							teamWork.put(model.getTeamId(), tw);
						}

						if (ModelService.isManagedTeamMember(model.getId())
								&& ModelService.isModelBookableToday(model.getId(), today.getEvent().getFlag())) {
							// do the booking right away
							today.setAmount1(pmax);
							ModelService.reportBooking(today);
							TransactionService.transfer(-1, 0, pmax, today.getNoteAcct());
							DiaryService.fileLog(today);

							tw.bookings++;
							tw.earnings += pmax;
							Log.i(LOG_TAG, "Team " + model.getTeamId() + " event " + tw.bookings + " " + Util.amount(pmax) + " - " + model);
						}
						else {
							today.setModelId(-1);
							today.setAmount1(price);
							today.setAmount2(pmax);
							TodayDbAdapter.addToday(today);
							Log.i(LOG_TAG, "Team " + model.getTeamId() + " not managed " + model
									+ " mg: " + ModelService.isManagedTeamMember(model.getId())
									+ " bk: " + ModelService.isModelBookableToday(model.getId(), today.getEvent().getFlag()));
						}
					}
				}

				if (allModels.size() >= Game.MIN_GAME_MODELS && Util.rnd(allModels.size()) < 3)
					EventService.newGambleEvent();

			} // for each model

			// pay the team leaders
			for (int i = 0; i < teamWork.size(); i++) {
				int teamId = teamWork.keyAt(i);
                if (teamId == 0) continue;
				Team team = ModelService.getTeam(teamId);
				TeamWork tw = teamWork.valueAt(i);

				Model leader = null;
				if (tw.bookings > 0) {
					// check if first leader is active
					leader = ModelService.getModelById(team.getLeader1());
					if (leader.getStatus() != ModelStatus.HIRED) {
						Log.i(LOG_TAG, "Team #" + teamId
								+ " leader 1 #" + team.getLeader1() + " is " + leader.getStatus());
						leader = ModelService.getModelById(team.getLeader2());
					}
					if (leader.getStatus() != ModelStatus.HIRED) {
						Log.i(LOG_TAG, "Team #" + teamId
								+ " leader 2 #" + team.getLeader2() + " is " + leader.getStatus());
						leader = ModelService.getModelById(team.getLeader2());
					}
				}
				if (teamId > 0 &&
						(leader == null || leader.getStatus() != ModelStatus.HIRED || tw.bookings == 0)) {
					Log.i(LOG_TAG, "Team #" + teamId + " is unmanaged");
					EventService.newNotification(0, EventFlag.NO_GROUPWORK, 0, teamId);
					continue;
				}

				int bonus = tw.earnings * team.getBonus() / 100;
				Today t = EventService.newNotification(leader.getId(), EventFlag.GROUPWORK, tw.earnings, bonus);
				Log.i(LOG_TAG, "Team " + teamId + " has performed " + tw.bookings + " bookings and earned *" + tw.earnings + ".- Leader " + leader + " gets *" + bonus + ".-");
				TransactionService.transfer(0, leader.getId(), bonus, t.getNoteAcct());
				DiaryService.log(t);
			}
		} // not on Sunday

        while (DiaryService.today() < 7 && hiredCount < 10 && applicationCount < 10
                && hireables.size() > 0) {
            int mi = Util.rnd(hireables.size());
            Model m = hireables.get(mi);
            ModelService.makeApplication(m.getId());
            applicationCount++;
            hireables.remove(mi);
        }

		// Costs for company cars
		if (DiaryService.todayWeekday() == Weekday.MONDAY) {
			int carExpenses = 0;
			for (CompanyCar cc : CarService.getCompanyPoolCars()) {
				carExpenses += cc.getCarType().getPrice() / 1000;
			}
			TransactionService.transfer(0, -1, carExpenses, MessageService.getMessage(R.string.accountmessage_company_car_costs));
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (progress!=null) {
			progress.dismiss();
		}
		viewElements.updateParentView();
		context.refreshModelList();
	}


}
