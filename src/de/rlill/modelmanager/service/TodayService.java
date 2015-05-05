package de.rlill.modelmanager.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;
import de.rlill.modelmanager.MainActivity;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.dialog.GameFacedetectDialog;
import de.rlill.modelmanager.dialog.GameNamedetectDialog;
import de.rlill.modelmanager.model.Diary;
import de.rlill.modelmanager.model.Event;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.Team;
import de.rlill.modelmanager.model.Today;
import de.rlill.modelmanager.persistance.DiaryDbAdapter;
import de.rlill.modelmanager.persistance.EventDbAdapter;
import de.rlill.modelmanager.persistance.TodayDbAdapter;
import de.rlill.modelmanager.struct.CarAction;
import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.ModelStatus;
import de.rlill.modelmanager.struct.RejectReasons;
import de.rlill.modelmanager.struct.ViewElements;


public class TodayService {

	private static final String LOG_TAG = "MM*" + TodayService.class.getSimpleName();

	public static void accept(MainActivity ctx, ViewElements ve) {
		Today today = ve.getContextToday();
		SparseArray<String> formularData = ve.getFormularData();
		Log.i(LOG_TAG, "accepting today's event " + today.getId()
				+ "(" + today.getEvent().getEclass() + "/" + today.getEvent().getFlag() + ")"
				+ ": " + today.getDescription());

		switch (today.getEvent().getEclass()) {
		case NOTIFICATION:
			if (today.getEvent().getFlag() == EventFlag.NEWDAY) {
				newDay(ctx, ve);

				// calculate worthincrease factor
				double currWI = PropertiesService.getWorthincrease();
				double newWI = calculateWorthincrease();

				if (newWI > currWI) {
					Log.i(LOG_TAG, String.format("Worthincrease changed from %5.2f to %5.2f", currWI, newWI));

					// write to Database
					PropertiesService.setWorthincrease(newWI);

					// also write to preferences
					SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putString(ctx.getResources().getString(R.string.worthincrease), String.format("%5.2f", newWI));
					editor.commit();
				}
			}
			if (today.getEvent().getFlag() == EventFlag.CAR_BROKEN) {
				CarService.repairCar(today.getModel().getCarId());
			}
			TodayDbAdapter.removeToday(today.getId());
			break;
		case BOOKING:
			TransactionService.transfer(-1, 0, today.getAmount1(), today.getNoteAcct());
			ModelService.reportBooking(today);
			DiaryService.fileLog(today);
			TodayDbAdapter.removeToday(today.getId());
			triggerTeamwork();
			break;
		case BOOKREJECT:
			ModelService.grantVacation(today.getModelId());
			ModelService.clearTodaysBookings(today.getModelId(), EventFlag.PHOTO);
			TodayService.dropEvents(today.getModelId(), EventClass.REQUEST, EventFlag.VACATION, EventFlag.TRAINING);
			triggerTeamwork();
			break;
		case REQUEST:
			switch (today.getEvent().getFlag()) {
			case BONUS:
				ModelService.grantBonus(today.getModelId(), today.getAmount1(), today.getAmount2());
				DiaryService.logAccept(today);
				TodayDbAdapter.removeToday(today.getId());
				triggerTeamwork();
				break;
			case VACATION:
				ModelService.grantVacation(today.getModelId());
				TodayDbAdapter.removeToday(today.getId());
				TodayService.dropEvents(today.getModelId(), EventClass.REQUEST, EventFlag.TRAINING);
				ModelService.clearTodaysBookings(today.getModelId(), EventFlag.PHOTO);
				break;
			case TRAINING:
				int tid = Util.atoi(formularData.get(R.string.labelTraining));
				if (tid > 0) {
					ModelService.sendToTraining(today.getModelId(), tid);
					TodayDbAdapter.removeToday(today.getId());
				}
				break;
			case RAISE:
				int newSalary = today.getAmount1();
				DiaryService.logAccept(today);
				ModelService.grantRaise(today.getModelId(), newSalary);
				TodayDbAdapter.removeToday(today.getId());
				triggerTeamwork();
				break;
			case CAR_UPDATE:
				int carId = Util.atoi(formularData.get(R.string.labelCar));
				if (carId == 0) {
					// must buy first
					int carTypeId = Util.atoi(formularData.get(R.string.labelCarTypes));
					if (carTypeId > 0) carId = CarService.buyCar(carTypeId);
				}
				if (carId <= 0)  break;
				ModelService.setCar(today.getModelId(), carId);
				CarService.log(carId, CarAction.ASSIGN,
						MessageService.getMessage(R.string.logmessage_car_assign, today.getModel()),
						CarService.getCarValue(carId));

				today.setAmount1(Util.atoi(formularData.get(R.string.labelPrice)));
				DiaryService.logAccept(today);
				TodayDbAdapter.removeToday(today.getId());
				triggerTeamwork();
				break;
			case QUIT:
				TodayService.dropRequests(today.getModelId());
				ModelService.release(today.getModelId());
				DiaryService.logAccept(today);
				TodayDbAdapter.removeToday(today.getId());
				break;
			default:
				Log.w(LOG_TAG, "Accept for unexpected REQUEST with flag " + today.getEvent().getFlag());
				TodayDbAdapter.removeToday(today.getId());
			}
			break;
		case EXTRA_IN:
			switch (today.getEvent().getFlag()) {
			case WIN:
			case WIN_PERSON:
				TransactionService.transfer(-1, 0, today.getAmount1(), today.getNoteAcct());
				DiaryService.log(today);
				TodayDbAdapter.removeToday(today.getId());
				break;
			default:
				Log.w(LOG_TAG, "Accept for unexpected EXTRA_IN with flag " + today.getEvent().getFlag());
				TodayDbAdapter.removeToday(today.getId());
			}
			break;
		case EXTRA_OUT:
			switch (today.getEvent().getFlag()) {
			case LOSE:
			case LOSE_PERSON:
				TransactionService.transfer(0, -1, today.getAmount1(), today.getNoteAcct());
				DiaryService.log(today);
				TodayDbAdapter.removeToday(today.getId());
				break;
			case PAYOPT_PERSON:
			case PAYFIX_PERSON:
			case PAYVAR_PERSON:
				TransactionService.transfer(0, today.getModelId(), today.getAmount1(), today.getNoteAcct());
				DiaryService.log(today);
				TodayDbAdapter.removeToday(today.getId());
				break;
			default:
				Log.w(LOG_TAG, "Accept for unexpected EXTRA_OUT with flag " + today.getEvent().getFlag());
				TodayDbAdapter.removeToday(today.getId());
			}
			break;
		case EXTRA_LOSS:
			switch (today.getEvent().getFlag()) {
			case PAYOPT_PERSON:
			case PAYVAR_PERSON:
				TransactionService.transfer(0, today.getModelId(), today.getAmount1(), today.getNoteAcct());
				ModelService.improveCriminality(today.getModelId(), 10);
				DiaryService.log(today);
				TodayDbAdapter.removeToday(today.getId());
				break;
			case PAYFIX_PERSON:
				ModelService.improveCriminality(today.getModelId(), 10);
				DiaryService.log(today);
				TodayDbAdapter.removeToday(today.getId());
				break;
			case LOSE_PERSON:
				DiaryService.log(today);
				TodayDbAdapter.removeToday(today.getId());
				break;
			default:
				Log.w(LOG_TAG, "Accept for unexpected EXTRA_LOSS with flag " + today.getEvent().getFlag());
				TodayDbAdapter.removeToday(today.getId());
			}
			break;
		case GAMBLE:
			int game = ve.getContextInt();
			Log.d(LOG_TAG, "Game " + game + ", event: " + today.getEvent());
			SparseArray<String> fd = ve.getFormularData();
			int bet = 0;
			Intent intent;
			switch (game) {
			case 1:
				bet = Util.atoi(fd.get(R.string.labelBet1));
				if (bet < today.getEvent().getAmountMin()) break;
				intent = new Intent(ctx, GameNamedetectDialog.class);
				intent.putExtra(GameNamedetectDialog.EXTRA_BET, bet);
				intent.putExtra(GameNamedetectDialog.RESET, 1);
				ctx.startActivity(intent);
				break;
			case 2:
				bet = Util.atoi(fd.get(R.string.labelBet2));
				if (bet < today.getEvent().getAmountMin()) break;
				intent = new Intent(ctx, GameFacedetectDialog.class);
				intent.putExtra(GameFacedetectDialog.EXTRA_BET, bet);
				intent.putExtra(GameFacedetectDialog.RESET, 1);
				ctx.startActivity(intent);
				break;
			}
			TodayDbAdapter.removeToday(today.getId());
			break;
		default:
			TodayDbAdapter.removeToday(today.getId());
		}
	}

	public static void reject(Context ctx, Today today) {
		Log.i(LOG_TAG, "rejecting today's event " + today.getEvent().getEclass() + ":" + today.getEvent().getFlag() + ":" + today.getId() + ": " + today.getDescription());

		switch (today.getEvent().getEclass()) {
		case REQUEST:
			switch (today.getEvent().getFlag()) {
			case RAISE:
			case PAYOPT_PERSON:
			case BONUS:
				ModelService.improveMood(today.getModelId(), -10);
				TodayDbAdapter.removeToday(today.getId());
				break;
			case VACATION:
			case TRAINING:
			case CAR_UPDATE:
				ModelService.improveMood(today.getModelId(), -5);
				TodayDbAdapter.removeToday(today.getId());
				break;
			default:
			}
			break;
		case EXTRA_OUT:
		case EXTRA_LOSS:
			switch (today.getEvent().getFlag()) {
			case PAYOPT_PERSON:
				ModelService.improveMood(today.getModelId(), -10);

				// Chance of model quitting
				if (Util.rnd(5) == 1) {
					TodayService.dropRequests(today.getModelId());
					ModelService.release(today.getModelId());
					DiaryService.log(
							MessageService.getMessage(R.string.logmessage_model_quit, today.getModel()),
							EventClass.NOTIFICATION, EventFlag.QUIT, today.getModelId(), 0);

					// convert to quit notification
					EventService.convertToQuit(today);
					TodayDbAdapter.updateToday(today);
				}
				else
					TodayDbAdapter.removeToday(today.getId());

				break;
			default:
			}
			break;
		case APPLICATION:
		case BOOKING:
		case GAMBLE:
			TodayDbAdapter.removeToday(today.getId());
			break;
		case BOOKREJECT:
			ModelService.clearBooking(today);
			break;
		default:
			Log.e(LOG_TAG, "Can not reject " + today.getEvent().getEclass());
		}
		// anything else is not rejectable!
	}

	public static void offer(Context ctx, Today today, SparseArray<String> formularData) {
		Log.i(LOG_TAG, "offer for today's event " + today.getId()
				+ "(" + today.getEvent().getEclass() + "/" + today.getEvent().getFlag() + ")"
				+ ": " + today.getDescription());

		int offer;
		switch (today.getEvent().getEclass()) {
		case APPLICATION:
			// Salary
			int salary = Util.atoi(formularData.get(R.string.labelSalary));
			if (salary < today.getAmount2()) break;

			// Vacation
			int vacation = Util.atoi(formularData.get(R.string.labelVacation));
			if (vacation < 4) break; // FIXME: use value from some kind of settings

			// Bonus
			int bonus = Util.atoi(formularData.get(R.string.labelBonus));
			if (bonus > 0) {
				ModelService.grantBonus(today.getModelId(), bonus);
			}

			// Team
			int teamId = Util.atoi(formularData.get(R.string.labelTeam));
			if (teamId == ModelService.TEAM_NEW_TEAM) {
				// create new team and make model 1st leader
				teamId = ModelService.createTeam(today.getModelId());
			}

			ModelService.hire(today.getModelId(), salary, vacation, teamId);
			DiaryService.logAccept(today);
			TodayDbAdapter.removeToday(today.getId());

			// Car
			int companyCarId = Util.atoi(formularData.get(R.string.labelCar));
			int carTypeId = Util.atoi(formularData.get(R.string.labelCarTypes));
			int carPrice = Util.atoi(formularData.get(R.string.labelPrice));
			if (companyCarId > 0 || carTypeId > 0) {
				if (companyCarId == 0) {
					// must buy first
					companyCarId = CarService.buyCar(carTypeId);
				}
				ModelService.setCar(today.getModelId(), companyCarId);
				CarService.log(companyCarId, CarAction.ASSIGN,
						MessageService.getMessage(R.string.logmessage_car_assign, today.getModel()),
						CarService.getCarValue(companyCarId));

				String msg = ctx.getString(R.string.logmessage_car_assign, CarService.getCarInfo(companyCarId));
				DiaryService.log(msg, EventClass.ACCEPT, EventFlag.CAR_UPDATE, today.getModelId(), carPrice);
			}
			triggerTeamwork();
			break;

		case REQUEST:

			switch (today.getEvent().getFlag()) {
			case RAISE:
				int newSalary = Util.atoi(formularData.get(R.string.labelTheOffer));
				if (newSalary >= today.getAmount2()) {
					// accept
					DiaryService.logAccept(today);
					ModelService.grantRaise(today.getModelId(), newSalary);
					TodayDbAdapter.removeToday(today.getId());
					triggerTeamwork();
				}
				else {
					// toast
					Toast.makeText(ctx, ctx.getResources().getString(R.string.messageOfferRejected),
							Toast.LENGTH_LONG).show();
				}
				break;

			case QUIT:
				// check salary raise
				offer = Util.atoi(formularData.get(R.string.labelSalary));
				int expect = today.getModel().getSalary()
						+ (int)((double)today.getModel().getSalary() * today.getModel().getAmbition() / 1000);
				Log.d(LOG_TAG, "model #" + today.getModelId() + " has ambition " + today.getModel().getAmbition());
				Log.d(LOG_TAG, "checking salary raise offer " + offer + ", expect " + expect);
				if (offer >= expect) {
					Log.d(LOG_TAG, "accept salary raise offer " + offer);
					ModelService.grantRaise(today.getModelId(), offer);
					TodayDbAdapter.removeToday(today.getId());
					TodayService.dropEvents(today.getModelId(), EventClass.REQUEST, EventFlag.RAISE);
					triggerTeamwork();
				}
				// check bonus offer
				offer = Util.atoi(formularData.get(R.string.labelBonus));
				expect = ModelService.getExpectedBonus(today.getModelId());
				if (expect < today.getModel().getSalary())
					expect = (int)((double)today.getModel().getSalary() * today.getModel().getAmbition() / 10);
				Log.d(LOG_TAG, "checking bonus offer " + offer + ", expect " + expect);
				if (offer >= expect) {
					Log.d(LOG_TAG, "accept bonus offer " + offer);
					ModelService.grantBonus(today.getModelId(), offer, -1);
					TodayDbAdapter.removeToday(today.getId());
					TodayService.dropEvents(today.getModelId(), EventClass.REQUEST, EventFlag.BONUS);
					triggerTeamwork();
				}
				break;

			case BONUS:
				offer = Util.atoi(formularData.get(R.string.labelTheOffer));
				if (offer == 0) offer = Util.atoi(formularData.get(R.string.labelBonus));
				if (offer >= today.getAmount2()) {
					ModelService.grantBonus(today.getModelId(), offer, today.getAmount1());
					TodayDbAdapter.removeToday(today.getId());
					triggerTeamwork();
				}
				break;

			default:
			}

			break;

		case EXTRA_OUT:
			switch (today.getEvent().getFlag()) {
			case PAYOPT_PERSON:
				offer = Util.atoi(formularData.get(R.string.labelTheOffer));
				if (offer == 0) offer = Util.atoi(formularData.get(R.string.labelBonus));
				if (offer >= 0) {
					TransactionService.transfer(0, today.getModelId(), offer, today.getEvent().getNoteAcct());
					DiaryService.log(today.getNoteFile(),
							EventClass.ACCEPT, EventFlag.PAYOPT_PERSON, today.getModelId(), offer);
					ModelService.bonusMoodImpact(today.getModelId(), offer, today.getAmount1());
					TodayDbAdapter.removeToday(today.getId());
				}
				break;

			case PAYVAR_PERSON:
				offer = Util.atoi(formularData.get(R.string.labelTheOffer));
				if (offer == 0) offer = Util.atoi(formularData.get(R.string.labelBonus));
				if (offer >= today.getAmount2()) {
					TransactionService.transfer(0, today.getModelId(), offer, today.getEvent().getNoteAcct());
					DiaryService.log(today.getNoteFile(),
							EventClass.ACCEPT, EventFlag.PAYVAR_PERSON, today.getModelId(), offer);
					ModelService.bonusMoodImpact(today.getModelId(), offer, today.getAmount1());
					TodayDbAdapter.removeToday(today.getId());
				}
				break;

			default:
			}
			break;

		case EXTRA_LOSS:
			switch (today.getEvent().getFlag()) {
			case PAYFIX_PERSON:
				int reclaim = Util.atoi(formularData.get(R.string.labelTheReclaim));
				if (reclaim > 0 && reclaim <= today.getAmount1()) {
					TransactionService.transfer(today.getModelId(), 0, reclaim,
							MessageService.getMessage(R.string.accountmessage_reclaim) + " " + today.getNoteAcct());
					ModelService.improveCriminality(today.getModelId(), -20);

					String optFire = formularData.get(R.string.labelFireImmmediately);
					if ("1".equals(optFire)) {
						TodayService.dropRequests(today.getModelId());
						ModelService.release(today.getModelId());
						DiaryService.log(MessageService.getMessage(R.string.logmessage_dismissal_imm),
								EventClass.ACCEPT, EventFlag.QUIT, today.getModelId(), 0);
					}

					TodayDbAdapter.removeToday(today.getId());
				}
				else if (reclaim == 0) {
					ModelService.improveCriminality(today.getModelId(), 10);
					TodayDbAdapter.removeToday(today.getId());
				}
				break;

			case PAYOPT_PERSON:
				offer = Util.atoi(formularData.get(R.string.labelTheOffer));
				if (offer >= 0) {
					if (offer > today.getAmount1()) ModelService.improveCriminality(today.getModelId(), 30);
					else if (offer == today.getAmount1()) ModelService.improveCriminality(today.getModelId(), 10);
					else if (offer < today.getAmount1()) ModelService.improveCriminality(today.getModelId(), -20);
					TransactionService.transfer(0, today.getModelId(), offer, today.getEvent().getNoteAcct());
					DiaryService.log(today.getNoteFile(),
							EventClass.ACCEPT, EventFlag.PAYOPT_PERSON, today.getModelId(), offer);
					ModelService.bonusMoodImpact(today.getModelId(), offer, today.getAmount1());

					String optFire = formularData.get(R.string.labelFireImmmediately);
					if ("1".equals(optFire)) {
						TodayService.dropRequests(today.getModelId());
						ModelService.release(today.getModelId());
						DiaryService.log(MessageService.getMessage(R.string.logmessage_dismissal_imm),
								EventClass.ACCEPT, EventFlag.QUIT, today.getModelId(), 0);
					}

					// quit-chance
					int qc = 50 - (int)(50 * (double)offer / today.getAmount2());

					// model is likely to quit
					if (Util.rnd(10) < qc) {
						TodayService.dropRequests(today.getModelId());
						ModelService.release(today.getModelId());
						DiaryService.log(
								MessageService.getMessage(R.string.logmessage_model_quit, today.getModel()),
								EventClass.NOTIFICATION, EventFlag.QUIT, today.getModelId(), 0);

						// convert to quit notification
						EventService.convertToQuit(today);
						TodayDbAdapter.updateToday(today);
					}
					else
						TodayDbAdapter.removeToday(today.getId());

				}
				break;

			case PAYVAR_PERSON:
				offer = Util.atoi(formularData.get(R.string.labelTheOffer));
				if (offer >= today.getAmount2()) {
					if (offer > today.getAmount1()) ModelService.improveCriminality(today.getModelId(), 30);
					else if (offer == today.getAmount1()) ModelService.improveCriminality(today.getModelId(), 10);
					else if (offer < today.getAmount1()) ModelService.improveCriminality(today.getModelId(), -20);
					TransactionService.transfer(0, today.getModelId(), offer, today.getEvent().getNoteAcct());
					DiaryService.log(today.getNoteFile(),
							EventClass.ACCEPT, EventFlag.PAYVAR_PERSON, today.getModelId(), offer);
					ModelService.bonusMoodImpact(today.getModelId(), offer, today.getAmount1());

					String optFire = formularData.get(R.string.labelFireImmmediately);
					if ("1".equals(optFire)) {
						TodayService.dropRequests(today.getModelId());
						ModelService.release(today.getModelId());
						DiaryService.log(MessageService.getMessage(R.string.logmessage_dismissal_imm),
								EventClass.ACCEPT, EventFlag.QUIT, today.getModelId(), 0);
					}

					TodayDbAdapter.removeToday(today.getId());
				}
				break;

			case LOSE_PERSON:
				reclaim = Util.atoi(formularData.get(R.string.labelTheReclaim));
				if (today.getAmount1() > 0 && reclaim > 0 && reclaim <= today.getAmount1()) {
					TransactionService.transfer(today.getModelId(), 0, reclaim,
							MessageService.getMessage(R.string.accountmessage_reclaim) + " " + today.getNoteAcct());

					// quit-chance 0% - 70%
					int qc = 70 - (int)(70 * (double)reclaim / today.getAmount1());

					// model is likely to quit
					if (Util.rnd(100) < qc) {
						TodayService.dropRequests(today.getModelId());
						ModelService.release(today.getModelId());
						DiaryService.log(
								MessageService.getMessage(R.string.logmessage_model_quit, today.getModel()),
								EventClass.NOTIFICATION, EventFlag.QUIT, today.getModelId(), 0);

						// convert to quit notification
						EventService.convertToQuit(today);
						TodayDbAdapter.updateToday(today);
					}
					else
						TodayDbAdapter.removeToday(today.getId());
				}
				else if (reclaim == 0) {
					TodayDbAdapter.removeToday(today.getId());
				}
				break;

			default:
			}
			break;

		case BOOKING:
			Log.d(LOG_TAG, "BOOK-" + today.getEvent().getFlag() + " for " + Util.amount(today.getAmount1())
					+ "/" + Util.amount(today.getAmount2()));
			int priceOffer = Util.atoi(formularData.get(R.string.labelTheOffer));
			int substitute = Util.atoi(formularData.get(R.string.labelSubstitute));

			Model model = today.getModel();
			int mqual = (today.getEvent().getFlag() == EventFlag.PHOTO)
					? model.getQuality_photo() : model.getQuality_movie();
			if (mqual < 1) mqual = 1;
			Log.d(LOG_TAG, "Request " + model + " (" + mqual + ")");

			if (substitute != model.getId()) {
				Model smod = ModelService.getModelById(substitute);
				int squal = (today.getEvent().getFlag() == EventFlag.PHOTO)
						? smod.getQuality_photo() : smod.getQuality_movie();
				if (squal < 1) squal = 1;
				Log.d(LOG_TAG, "Offer " + smod + " (" + squal + ") for " + Util.amount(priceOffer));
				int newOffer = today.getAmount1() / mqual * squal;
				int newMax = today.getAmount2() / mqual * squal;

				Log.d(LOG_TAG, "Expect " + Util.amount(newOffer) + "/" + Util.amount(newMax));

				if (priceOffer <= newMax) {
					// customer accepts substitute and price offer
					today.setModelId(substitute);
					today.setAmount1(priceOffer);
					today.setAmount2(newMax);
				}
				else {
					// customer accepts substitute but not the price offer
					today.setModelId(substitute);
					today.setAmount1(newOffer);
					today.setAmount2(newMax);
				}

				RejectReasons rr = ModelService.bookingRejectReasons(substitute);
				if (rr.willReject()) EventService.rejectBooking(today);

				TodayDbAdapter.updateToday(today);
				break;
			}

			if (priceOffer > today.getAmount2()) {
				// too expensive - continue to bargain
				today.setAmount1(Util.interpolation(today.getAmount1(), today.getAmount2()));
				TodayDbAdapter.updateToday(today);
				break;
			}

			// offer accepted
			today.setAmount1(priceOffer);
			TodayDbAdapter.updateToday(today);

			break;

		case BOOKREJECT:
			offer = Util.atoi(formularData.get(R.string.labelTheOffer));
			if (offer == 0) offer = Util.atoi(formularData.get(R.string.labelBonus));
			if (offer > 0) {
				ModelService.grantBonus(today.getModelId(), offer, today.getAmount1());
				// remove further bonus requests
				for (Today td : TodayDbAdapter.getEventsForModel(today.getModelId())) {
					if (td.getEvent().getEclass() == EventClass.REQUEST
						&& td.getEvent().getFlag() == EventFlag.BONUS) {
						if (offer >= td.getAmount2())
							TodayDbAdapter.removeToday(td.getId());
						else {
							int expectedBonus = ModelService.getExpectedBonus(today.getModelId());
							td.setAmount1(Util.niceRandom(expectedBonus, 2 * expectedBonus));
							td.setAmount2(expectedBonus);
							TodayDbAdapter.updateToday(td);
						}
					}
				}
			}

			int carId = Util.atoi(formularData.get(R.string.labelCar));
			carPrice = Util.atoi(formularData.get(R.string.labelPrice));
			if (carId == 0) {
				// must buy first
				carTypeId = Util.atoi(formularData.get(R.string.labelCarTypes));
				if (carTypeId > 0) carId = CarService.buyCar(carTypeId);
			}
			if (carId > 0) {
				ModelService.setCar(today.getModelId(), carId);
				CarService.log(carId, CarAction.ASSIGN,
						MessageService.getMessage(R.string.logmessage_car_assign),
						CarService.getCarValue(carId));

				String msg = ctx.getString(R.string.logmessage_car_assign, CarService.getCarInfo(carId));
				DiaryService.log(msg, EventClass.ACCEPT, EventFlag.CAR_UPDATE, today.getModelId(), carPrice);
			}

			// check whether request may now be acceptable
			RejectReasons rr = ModelService.bookingRejectReasons(today.getModelId());
			if (!rr.willReject()) {
				EventService.unrejectBooking(today);
				TodayDbAdapter.updateToday(today);
			}

			break;

		default:
			TodayDbAdapter.removeToday(today.getId());
		}
	}

	private static void newDay(MainActivity ctx, ViewElements ve) {

		ModelService.newDay();
		DiaryService.newDay();
		CreditService.newDay();
		EventService.resetEventHistory();

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		boolean noteSal = sharedPref.getBoolean("notifyPaycheck", true);
		boolean noteSick = sharedPref.getBoolean("notifySick", true);
		boolean noteTrain = sharedPref.getBoolean("notifyTrain", true);

		NewDayService nd = new NewDayService(ctx, ve, noteSal, noteSick, noteTrain);
		nd.execute(null, null, null);
	}

	private static void triggerTeamwork() {
		// arbitrary order
		List<Team> teams = ModelService.getAllTeams();
		if (teams.size() < 1) return;
		int [] mix = Util.randomArray(teams.size());
		for (int i = 0; i < mix.length; i++) {
			Team team = teams.get(mix[i]);
			ModelService.teamwork(team);
		}
	}

	public static List<Today> getTodayEventsForModel(int modelId) {
		return TodayDbAdapter.getEventsForModel(modelId);
	}

	public static List<String> getAllEventsForModelToday(int modelId) {
		List<String> result = new ArrayList<String>();
		for (Today t : TodayDbAdapter.getEventsForModel(modelId)) {
			result.add(" \u274F " + t.getDescription());
		}
		for (Diary d : DiaryDbAdapter.listEvents(modelId, DiaryService.today())) {
			result.add("\u2714 " + d.getDescription());
		}
		return result;
	}

	public static void dropEvents(int modelId, EventClass eclass, EventFlag ... flag) {
		String fs = ""; for (EventFlag f : flag)  fs += " " + f.name();
		Log.d(LOG_TAG, "dropping Events for model #" + modelId + ", class " + eclass.name() + ", flags " + fs);
		for (Today t : TodayDbAdapter.getEventsForModel(modelId)) {
			if (t.getEvent().getEclass() == eclass) {
				for (EventFlag f : flag) {
					if (t.getEvent().getFlag() == f) {
						Log.d(LOG_TAG, "Cancelling " + t);
						TodayDbAdapter.removeToday(t.getId());
					}
				}
			}
		}
	}

	public static void dropRequests(int modelId) {
		ModelService.clearTodaysBookings(modelId, EventFlag.PHOTO);
		for (Today today : TodayDbAdapter.getEventsForModel(modelId)) {
			if (today.getEvent().getEclass() == EventClass.REQUEST) {
				Log.d(LOG_TAG, "Cancelling " + today);
				TodayDbAdapter.removeToday(today.getId());
			}
		}
	}

	public static void updateBookingRequestAcceptance(int modelId) {
		RejectReasons rr = ModelService.bookingRejectReasons(modelId);
		for (Today today : TodayDbAdapter.getEventsForModel(modelId)) {
			if (today.getEvent().getEclass() == EventClass.BOOKING && rr.willReject()) {
				EventService.rejectBooking(today);
				TodayDbAdapter.updateToday(today);
			}
			else if (today.getEvent().getEclass() == EventClass.BOOKREJECT && !rr.willReject()) {
				EventService.unrejectBooking(today);
				TodayDbAdapter.updateToday(today);
			}
		}
	}

	private static double calculateWorthincrease() {
		Event e = EventDbAdapter.getAllEvents(EventClass.APPLICATION, EventFlag.HIRE).get(0);
		Log.i(LOG_TAG, "Hire min salary: " + e.getAmountMin());

		List<ModelStatus> statusFilters = new ArrayList<ModelStatus>();
		statusFilters.add(ModelStatus.HIRED);
		statusFilters.add(ModelStatus.MOVIEPROD);
		statusFilters.add(ModelStatus.TRAINING);
		statusFilters.add(ModelStatus.VACATION);
		statusFilters.add(ModelStatus.SICK);

		int ssum = 0;
		int scnt = 0;
		int smin = -1;
		int smax = 0;
		for (Model model : ModelService.getAllModels(statusFilters)) {
			ssum += model.getSalary();
			smin = (smin == -1 || smin > model.getSalary()) ? model.getSalary() : smin;
			smax = (smax < model.getSalary()) ? model.getSalary() : smax;
			scnt++;
		}
		if (scnt == 0) return 1.0;

		double savg = (double)ssum / scnt;
		Log.i(LOG_TAG, String.format("Current min: %d.- avg: %d.- max: %d.-", smin, (int)savg, smax));

		Log.i(LOG_TAG, String.format("would result in worthincrease factor %5.2f", (savg / e.getAmountMin())));

//		return 1.0;
		return savg / e.getAmountMin();
	}

	public static class TeamWork {
		public int bookings = 0;
		public int earnings = 0;
	}

}
