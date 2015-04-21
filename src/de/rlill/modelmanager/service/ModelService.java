package de.rlill.modelmanager.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.CompanyCar;
import de.rlill.modelmanager.model.Event;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.ModelTraining;
import de.rlill.modelmanager.model.Team;
import de.rlill.modelmanager.model.Today;
import de.rlill.modelmanager.persistance.DiaryDbAdapter;
import de.rlill.modelmanager.persistance.ModelDbAdapter;
import de.rlill.modelmanager.persistance.ModelTrainingDbAdapter;
import de.rlill.modelmanager.persistance.TeamDbAdapter;
import de.rlill.modelmanager.persistance.TodayDbAdapter;
import de.rlill.modelmanager.service.TodayService.TeamWork;
import de.rlill.modelmanager.struct.CarAction;
import de.rlill.modelmanager.struct.CarClass;
import de.rlill.modelmanager.struct.CarStatus;
import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.ModelStatus;
import de.rlill.modelmanager.struct.Quality;
import de.rlill.modelmanager.struct.RejectReasons;
import de.rlill.modelmanager.struct.RomanNumeral;
import de.rlill.modelmanager.struct.TeamOption;
import de.rlill.modelmanager.struct.TrainingStatus;
import de.rlill.modelmanager.struct.Weekday;

public class ModelService {

	private static final String LOG_TAG = "MM*" + ModelService.class.getSimpleName();

	public static final int TEAM_NO_TEAM = 0;
	public static final int TEAM_NEW_TEAM = -1;
	public static final int UNDEFINED_MODEL = -1;

	private static SparseArray<Model> modelsArray;
	private static SparseArray<Statistics> modelStatistics = new SparseArray<Statistics>();
	private static SparseArray<Team> teamsArray;

	public static Model getModelById(int modelId) {
		if (modelsArray == null) modelsArray = ModelDbAdapter.listAllModels();
		return modelsArray.get(modelId);
	}

	public static void setUndefinedModel(String name, String image) {
		if (modelsArray == null) modelsArray = ModelDbAdapter.listAllModels();
		Model model = new Model(UNDEFINED_MODEL);
		model.setFirstname("");
		model.setLastname(name);
		model.setImage(image);
		model.setQuality_photo(20);
		model.setQuality_movie(20);
		model.setQuality_tlead(0);
		model.setStatus(ModelStatus.UNAVAILABLE);
		modelsArray.put(-1, model);
	}

	/**
	 * List all models.
	 */
	public static List<Model> getAllModels() {
		List<Model> result = new ArrayList<Model>();
		if (modelsArray == null) modelsArray = ModelDbAdapter.listAllModels();
		for (int i = 0; i < modelsArray.size(); i++) {
			Model m = modelsArray.valueAt(i);
			if (m.getId() != -1) result.add(m);
		}
		return result;
	}

    /**
     * List all models, filtered by statusFilter.
     */
    public static List<Model> getAllModels(ModelStatus statusFilter) {
    	List<ModelStatus> filterList = new ArrayList<ModelStatus>();
    	filterList.add(statusFilter);
    	return getAllModels(filterList);
    }

    /**
     * List all models, filtered by statusFilters.
     */
    public static List<Model> getAllModels(List<ModelStatus> statusFilters) {
    	List<Model> result = new ArrayList<Model>();
		if (modelsArray == null) modelsArray = ModelDbAdapter.listAllModels();
		for (int i = 0; i < modelsArray.size(); i++) {
			Model model = modelsArray.valueAt(i);
			if (!statusFilters.contains(model.getStatus())) continue;
			if (model.getId() != -1) result.add(model);
		}
		return result;
    }

    /**
     * Get models available for booking.
     * @param flag What to book for: EventFlag.PHOTO or EventFlag.MOVIE
     */
    public static List<Model> getModelsForBooking(EventFlag flag) {
    	List<Model> result = new ArrayList<Model>();
    	if (modelsArray == null) modelsArray = ModelDbAdapter.listAllModels();
    	for (int i = 0; i < modelsArray.size(); i++) {
    		Model model = modelsArray.valueAt(i);
    		if (!(model.getStatus() == ModelStatus.HIRED)) continue;
    		if (!isModelBookableToday(model.getId(), flag)) continue;
    		result.add(model);
    	}

    	Collections.sort(result, new ModelNameComparator());

    	return result;
    }

    public static List<Model> getHiredModels() {
		List<ModelStatus> statusFilters = new ArrayList<ModelStatus>();
		statusFilters.add(ModelStatus.HIRED);
		statusFilters.add(ModelStatus.SICK);
		statusFilters.add(ModelStatus.VACATION);
		statusFilters.add(ModelStatus.TRAINING);
		statusFilters.add(ModelStatus.MOVIEPROD);

		return getAllModels(statusFilters);
	}

    public static void initTeamSpinner(Context ctx, Spinner sp, int selectedTeamId) {

		// list Teams
    	if (teamsArray == null) teamsArray = TeamDbAdapter.getAllTeams();
		List<TeamOption> teamList = new ArrayList<TeamOption>();

		int selPos = 0;
		TeamOption to = new TeamOption(ctx.getResources().getString(R.string.labelNoTeam), TEAM_NO_TEAM);
		teamList.add(to);
		to = new TeamOption(ctx.getResources().getString(R.string.labelNewTeam), TEAM_NEW_TEAM);
		teamList.add(to);
		if (selectedTeamId == TEAM_NEW_TEAM) selPos = teamList.indexOf(to);
		for (int i = 0; i < teamsArray.size(); i++) {
			Team t = teamsArray.valueAt(i);
			String leaders = getTeamName(t.getId());
			to = new TeamOption(leaders, t.getId());
			teamList.add(to);
			if (t.getId() == selectedTeamId) selPos = teamList.indexOf(to);
		}

		ArrayAdapter<TeamOption> teamAdapter = new ArrayAdapter<TeamOption>(ctx,
				android.R.layout.simple_spinner_item, teamList);
		teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		sp.setAdapter(teamAdapter);
		sp.setSelection(selPos);
	}

	public static String getTeamName(int teamId) {
		if (teamId == TEAM_NO_TEAM) return "-";
		if (teamsArray == null) teamsArray = TeamDbAdapter.getAllTeams();
		Team t = teamsArray.get(teamId);
		if (t == null) return "-invalid team id " + teamId + "-";
		RomanNumeral rn = new RomanNumeral(teamId);
		if (t.getLeader1() > 0) {
			Model m = getModelById(t.getLeader1());
			if (m != null)
				return rn.toString() + " (" + m.getFullname() + ")";
		}
		if (t.getLeader2() > 0) {
			Model m = getModelById(t.getLeader2());
			if (m != null)
				return rn.toString() + " (" + m.getFullname() + ")";
		}
		return "Team " + rn.toString();
	}

	public static int createTeam(int modelId) {
		Model model = getModelById(modelId);
		if (model == null) throw new IllegalArgumentException("invalid modelId " + modelId);
		Team team = new Team();
		team.setLeader1(modelId);
		team.setBonus(10);
		int teamId = TeamDbAdapter.addTeam(team);
		// re-read with id initialized
		teamsArray = TeamDbAdapter.getAllTeams();
		return teamId;
	}

	public static void setModelStatus(int modelId, ModelStatus status) {
		Model model = getModelById(modelId);
		model.setStatus(status);
		switch (status) {
		case UNAVAILABLE:
			DiaryService.log(MessageService.getMessage(R.string.logmessage_become_unavailable, model),
					EventClass.NOTIFICATION, EventFlag.UNAVAILABLE, modelId, 0);
			break;
		case FREE:
			DiaryService.log(MessageService.getMessage(R.string.logmessage_become_available, model),
					EventClass.NOTIFICATION, EventFlag.AVAILABLE, modelId, 0);
			break;
		default:
		}
		ModelDbAdapter.updateModel(model);
	}

	public static void setTeam(int modelId, int teamId) {
		Model model = getModelById(modelId);
		if (model.getTeamId() != teamId) {
			int ot = model.getTeamId();
			model.setTeamId(teamId);
			ModelDbAdapter.updateModel(model);
			String msg = MessageService.getMessage(R.string.logmessage_change_team, model);
			msg = msg.replace("%T1", getTeamName(ot));
			msg = msg.replace("%T2", getTeamName(teamId));
			DiaryService.log(msg, EventClass.ACCEPT, EventFlag.CHANGETEAM, modelId, 0);
		}
	}

	public static List<Team> getAllTeams() {
		List<Team> result = new ArrayList<Team>();
		if (teamsArray == null) teamsArray = TeamDbAdapter.getAllTeams();
		for (int i = 0; i < teamsArray.size(); i++) {
			result.add(teamsArray.valueAt(i));
		}
		return result;
	}

	public static List<Model> getTeamMembers(int teamId) {
		List<Model> result = new ArrayList<Model>();
		if (modelsArray == null) modelsArray = ModelDbAdapter.listAllModels();
		for (int i = 0; i < modelsArray.size(); i++) {
			Model m = modelsArray.valueAt(i);
			if (m.getTeamId() == teamId && isModelHired(m)) result.add(m);
		}
		return result;
	}

	public static Team getTeam(int teamId) {
		if (teamsArray == null) teamsArray = TeamDbAdapter.getAllTeams();
		return teamsArray.get(teamId);
	}

	public static boolean isActiveTeamLeader(int modelId) {
		Model model = getModelById(modelId);
		if (model.getStatus() != ModelStatus.HIRED) return false;
		if (model.getTeamId() == 0) return false;

		// check if fist leader
		Team team = getTeam(model.getTeamId());
		if (team.getLeader1() == modelId) return true;

		// check if active as second leader
		if (team.getLeader2() != modelId) return false;
		Model first = getModelById(team.getLeader1());
		if (first.getStatus() == ModelStatus.HIRED) return false;

		// first leader is busy, take over
		return true;
	}

	public static boolean isManagedTeamMember(int modelId) {
		Model model = getModelById(modelId);
		if (model.getTeamId() == 0) return false;
		Team team = getTeam(model.getTeamId());

		if (team.getLeader1() == modelId) return false;
		// check if fist leader is active
		Model leader = getModelById(team.getLeader1());
		if (leader != null && leader.getStatus() == ModelStatus.HIRED) return true;

		if (team.getLeader2() == modelId) return false;
		// check if second leader is active
		leader = getModelById(team.getLeader2());
		if (leader != null && leader.getStatus() == ModelStatus.HIRED) return true;

		// no active leader, act unmanaged
		return false;
	}


	public static void teamwork(Team team) {

		// check if first leader is active
		Model leader = getModelById(team.getLeader1());
		if (leader == null || leader.getStatus() != ModelStatus.HIRED) {
			// check if second leader is active
			leader = getModelById(team.getLeader2());
		}
		if (leader == null || leader.getStatus() != ModelStatus.HIRED) {
			Log.w(LOG_TAG, "No Teamwork in team " + team.getId() + " because no leader is available");
			return;
		}

		// collect requests
		List<Model> substList = getTeamMembers(team.getId());
		List<Today> photoRequests = new ArrayList<Today>();
		List<Today> movieRequests = new ArrayList<Today>();
		for (Today today : TodayDbAdapter.getAllEvents()) {
			// process booking requests only
			if (today.getEvent().getEclass() != EventClass.BOOKING
					&& today.getEvent().getEclass() != EventClass.BOOKREJECT) continue;

			// accept requests with no model assigned
			boolean thisTeam = today.getModelId() == ModelService.UNDEFINED_MODEL;

			// and accept requests for model from this team
			if (!thisTeam) {
				for (Model m : substList) if (m.getId() == today.getModelId()) thisTeam = true;
			}

			// skip everything else
			if (!thisTeam) continue;

			switch (today.getEvent().getFlag()) {
			case PHOTO:
				photoRequests.add(today);
				break;
			case MOVIE:
				movieRequests.add(today);
				break;
			default:
				Log.w(LOG_TAG, "Today BOOKING event " + today.getEvent().getFlag() + ": #" + today.getId());
			}
		}

		// assign to best models

		// movies
		TeamWork tw = new TeamWork();
		Collections.sort(substList, new ModelQualityComparator(Quality.MOVIE));
		for (Today t : movieRequests) {
			Model match = null;
			for (Model m : substList) {
				if (m.getStatus() != ModelStatus.HIRED) continue;
				if (isActiveTeamLeader(m.getId())) continue;
				RejectReasons rr = bookingRejectReasons(m.getId());
				if (rr.willReject()) continue;
				if (isModelBookableToday(m.getId(), EventFlag.MOVIE)) {
					match = m;
					break;
				}
			}

			if (match != null) {
				// book
				int mqual = t.getModel().getQuality_movie();
				if (mqual < 0) mqual = 1;

				int squal = match.getQuality_movie();
				if (squal < 1) squal = 1;

				int newPrice = t.getAmount2() / mqual * squal;
				Log.i(LOG_TAG, "PRICE " + t.getAmount2() + ".- / " + mqual + " * " + squal + " = " + newPrice);

				t.setAmount1(newPrice);
				t.setModelId(match.getId());
				reportBooking(t);
				TransactionService.transfer(-1, 0, newPrice, t.getNoteAcct());
				DiaryService.fileLog(t);
				reportTeamleadWork(leader.getId());
				TodayDbAdapter.removeToday(t.getId());

				tw.bookings++;
				tw.earnings += newPrice;
				Log.i(LOG_TAG, "Team " + match.getTeamId() + " event " + tw.bookings + " " + Util.amount(newPrice) + " - " + match.getFullname());
			}
			else
				Log.d(LOG_TAG, "No bookable model for MOVIE request #" + t.getId());
		}

		// photosessions
		Collections.sort(substList, new ModelQualityComparator(Quality.PHOTO));
		for (Today t : photoRequests) {
			Model match = null;
			for (Model m : substList) {
				if (m.getStatus() != ModelStatus.HIRED) continue;
				if (isActiveTeamLeader(m.getId())) continue;
				RejectReasons rr = bookingRejectReasons(m.getId());
				if (rr.willReject()) continue;
				if (isModelBookableToday(m.getId(), EventFlag.PHOTO)) {
					match = m;
					break;
				}
			}

			if (match != null) {
				// book
				int mqual = t.getModel().getQuality_photo();
				if (mqual < 0) mqual = 1;

				int squal = match.getQuality_photo();
				if (squal < 1) squal = 1;

				int newPrice = t.getAmount2() / mqual * squal;
				Log.i(LOG_TAG, "PRICE " + t.getAmount2() + ".- / " + mqual + " * " + squal + " = " + newPrice);

				t.setAmount1(newPrice);
				t.setModelId(match.getId());
				reportBooking(t);
				TransactionService.transfer(-1, 0, newPrice, t.getNoteAcct());
				DiaryService.fileLog(t);
				reportTeamleadWork(leader.getId());
				TodayDbAdapter.removeToday(t.getId());

				tw.bookings++;
				tw.earnings += newPrice;
				Log.i(LOG_TAG, "Team " + match.getTeamId() + " event " + tw.bookings + " " + Util.amount(newPrice) + " - " + match.getFullname());
			}
			else
				Log.d(LOG_TAG, "No bookable model for PHOTO request #" + t.getId());
		}

		if (tw.earnings > 0) {
			int bonus = tw.earnings * team.getBonus() / 100;

			// look for active notification to re-use
			Today t = EventService.currentNotification(leader.getId(), EventFlag.GROUPWORK);
			if (t != null) {
				// update
				t.setAmount1(t.getAmount1() + tw.earnings);
				t.setAmount2(t.getAmount2() + bonus);
				TodayDbAdapter.updateToday(t);
			}
			else {
				t = EventService.newNotification(leader.getId(), EventFlag.GROUPWORK, tw.earnings, bonus);
			}
			Log.i(LOG_TAG, "Team " + team.getId() + " has performed " + tw.bookings + " bookings and earned *" + tw.earnings + ".- Leader " + leader.getFullname() + " gets *" + bonus + ".-");
			TransactionService.transfer(0, leader.getId(), bonus, t.getNoteAcct());
			DiaryService.fileLog(t);
		}
	}


	public static Statistics getStatistics(int modelId) {
		Statistics st = modelStatistics.get(modelId);
		if (st == null) {
			st = DiaryDbAdapter.getModelStatistics(modelId);
			st.updateDay = DiaryService.today();
			modelStatistics.put(modelId, st);
		}
		if (st.updateDay != DiaryService.today()) {
			st.updateDay = DiaryService.today();
			st.movieToday = 0;
			st.photoToday = 0;
			st.teamleadToday = 0;
		}
		return st;
	}

	public static int getExpectedBonus(int modelId) {
		Statistics st = getStatistics(modelId);
		Model model = getModelById(modelId);
		int expectBonus = (st.w4photoEarnings + st.w4movieEarnings) / 100
				* (model.getAmbition() / 5 + 10);
		Log.i(LOG_TAG, "In 4 weeks model #" + modelId
				+ " has earned " + st.w4photoEarnings + ".- + " + st.w4movieEarnings + ".- "
				+ " and expects a refund of " + expectBonus + ".-");
		expectBonus -= st.w4bonus;
		if (expectBonus < 0) expectBonus = 0;
		Log.i(LOG_TAG, "She already has received " + st.w4bonus + ".- in payments and still wants "
				+ expectBonus + ".-");
		return expectBonus;
	}

	public static int getQuitCompensation(int modelId) {
		Model model = getModelById(modelId);
		int bonus = model.getSalary() * 2;
		int active = (DiaryService.today() - model.getHireday());
		if (active > 14) bonus = (int)(model.getSalary() * ((double)active / 7));
		return bonus;
	}

	public static Model findModelForEvent(Event event) {
		if (modelsArray == null) modelsArray = ModelDbAdapter.listAllModels();
		List<Model> candidates = new ArrayList<Model>();
		int [] mix = Util.randomArray(modelsArray.size());
		for (int i = 0; i < modelsArray.size(); i++) {
			Model m = modelsArray.valueAt(mix[i]);
//			Log.i(LOG_TAG, "INVESTIGATING model " + m.getFullname());

			// exclusions
			switch (event.getEclass()) {
			case APPLICATION:
				if (m.getStatus() != ModelStatus.FREE) continue;
				// TODO: remove from list who quit less than a few days ago
				break;

			case BOOKING:
				if (m.getStatus() != ModelStatus.HIRED) continue;
				if (!isModelBookableToday(m.getId(), event.getFlag())) continue;
				if (isActiveTeamLeader(m.getId())) continue;
				break;

			case REQUEST:
				if (m.getStatus() != ModelStatus.HIRED && m.getStatus() != ModelStatus.MOVIEPROD && m.getStatus() != ModelStatus.VACATION) continue;
				// TBC...
				switch(event.getFlag()) {
				case QUIT:
					if (isModelBookedToday(m.getId())) continue;

					// TODO: exclude if already quitting today

					// exclude who started less than a few days ago
					if (m.getHireday() > DiaryService.today() - 7) continue;
					break;
				case CAR_UPDATE:
				case BONUS: // test scenario
					break;
				default:
					Log.e(LOG_TAG, "REQUEST not yet supported with flag " + event.getFlag());
				}
				break;

			case EXTRA_IN:
			case EXTRA_OUT:
				if (m.getStatus() != ModelStatus.HIRED && m.getStatus() != ModelStatus.MOVIEPROD && m.getStatus() != ModelStatus.VACATION) continue;
				break;

			case EXTRA_LOSS:
				if (m.getStatus() != ModelStatus.HIRED && m.getStatus() != ModelStatus.MOVIEPROD && m.getStatus() != ModelStatus.VACATION) continue;
				if (m.getCriminal() < 75) continue;
				break;

			default:
				Log.e(LOG_TAG, "No support yet for " + event.getEclass());
				return null;
			}

			candidates.add(m);
		}
		if (candidates.size() == 0) {
			Log.w(LOG_TAG, "No candidates available for " + event.getEclass() + ":" + event.getFlag());
			return null;
		}

		if (event.getFlag() == EventFlag.QUIT) {
			// order by quit-tendency and chose a weighted random
			Collections.sort(candidates, new ModelAmbitionComparator());
			int idx = Util.rnd(Util.rnd(candidates.size()) + 1);
			return candidates.get(idx);
		}

		int idx = Util.rnd(candidates.size());
		return candidates.get(idx);
	}

	public static boolean isModelBookedToday(int modelId) {
		Statistics st = getStatistics(modelId);
		if (st.movieToday > 0) return true;
		if (st.photoToday > 0) return true;
		return false;
	}

	public static boolean isModelBookableToday(int modelId, EventFlag flag) {
		Statistics st = getStatistics(modelId);
		if (st.movieToday > 0) return false;
		if (st.photoToday > 0 && flag == EventFlag.MOVIE) return false;
		if (st.photoToday > 1) return false;
		return true;
	}

	public static boolean isModelHired(Model model) {
		return (model.getStatus() == ModelStatus.HIRED
				|| model.getStatus() == ModelStatus.TRAINING
				|| model.getStatus() == ModelStatus.VACATION
				|| model.getStatus() == ModelStatus.SICK
				|| model.getStatus() == ModelStatus.MOVIEPROD);
	}

	public static RejectReasons bookingRejectReasons(int modelId) {
		RejectReasons rr = new RejectReasons();
		Model model = getModelById(modelId);
		Statistics st = getStatistics(modelId);

		// car
		if (model.getCarId() == 0) {
			rr.carMissing = true;
		}
		else {
			CompanyCar cc = CarService.getCompanyCar(model.getCarId());
			if (cc.getStatus() != CarStatus.IN_USE) rr.carMissing = true;
		}

		// bonus
		rr.bonusMissing = getExpectedBonus(modelId);

		// vacation
		int vm = st.w4photoSessions / 6 + st.w4movieSessions / 3 - 2 - st.w4daysVacation;
		if (vm > 0 && model.getVacrest() > 0) rr.vacationMissing = vm;

		// bad mood
		if (model.getMood() < 20) rr.badMood = true;

		return rr;
	}

	public static ModelTraining plannedTrainingForModel(int modelId) {
		ModelTraining mt = ModelTrainingDbAdapter.getNextPlannedTrainingForModel(modelId);

		// eliminate not-attended past trainings
		while (mt != null && mt.getEndDay() < DiaryService.today()
				&& mt.getTrainingStatus() == TrainingStatus.PLANNED) {
			ModelTrainingDbAdapter.deleteTraining(mt.getId());
			mt = ModelTrainingDbAdapter.getNextPlannedTrainingForModel(modelId);
		}

		return mt;
	}

	public static int getAverageSalary(int modelId) {
		Model model = getModelById(modelId);
		int qualitySum = model.getQuality_movie() + model.getQuality_photo() + model.getQuality_tlead();
//		Log.d(LOG_TAG, "QM:" + model.getQuality_movie() + " QP:" + model.getQuality_photo() + " QT:" + model.getQuality_tlead() + " S:" + qualitySum + "  " + model);
		int salarySum = 0;
		int salaryCnt = 0;
		int range = 10;
		int hiredModels = 0;
		do {
			salarySum = 0;
			salaryCnt = 0;
			hiredModels = 0;
			for (int i = 0; i < modelsArray.size(); i++) {
				Model m = modelsArray.valueAt(i);
				if (!isModelHired(m)) continue;

				hiredModels++;
				int qsum = m.getQuality_movie() + m.getQuality_photo() + m.getQuality_tlead();
//				Log.d(LOG_TAG, "QM:" + m.getQuality_movie() + " QP:" + m.getQuality_photo() + " QT:" + m.getQuality_tlead() + " S:" + qsum + "  " + m);
				if (qualitySum >= qsum - range && qualitySum <= qsum + range) {
					salarySum += m.getSalary();
					salaryCnt++;
				}
			}
//			Log.i(LOG_TAG, "Found " + salaryCnt + "/" + hiredModels + " models with qualitySum " + qualitySum + " +- " + range);
			range *= 3;
		} while (range < 270 && (salaryCnt < 3 || salaryCnt < (hiredModels + 9) / 10));
		if (salaryCnt > 0) return salarySum / salaryCnt;
		return 0;
	}

	public static void makeApplication(int modelId) {
		Today t = EventService.applicationEvent(modelId);
		t.setModelId(modelId);
		TodayDbAdapter.addToday(t);
	}

	public static void hire(int modelId, int salary, int vacation, int teamId) {
		Model model = getModelById(modelId);
		model.setStatus(ModelStatus.HIRED);
		model.setHireday(DiaryService.today());
		model.setSalary(salary);
		model.setPayday(DiaryService.todayWeekday());
		model.setVacation(vacation);
		model.setVacrest(vacation);
		model.setTeamId(teamId);
		ModelDbAdapter.updateModel(model);
	}

	public static void release(int modelId) {
		Model model = getModelById(modelId);
		model.setStatus(ModelStatus.FREE);
		int companyCarId = model.getCarId();
		model.setCarId(0);
		ModelDbAdapter.updateModel(model);
		if (companyCarId > 0) {
			CarService.assignCar(companyCarId, 0);
			CarService.log(model.getCarId(), CarAction.TAKEAWAY,
					MessageService.getMessage(R.string.logmessage_car_takeaway),
					CarService.getCarValue(companyCarId));
		}
		TrainingService.cancelTrainingPlans(modelId);
	}

	public static void grantRaise(int modelId, int salary) {
		Model model = getModelById(modelId);

		int increase = salary - model.getSalary();
		if (increase <= 0)
			Log.w(LOG_TAG, "invalid raise of " + increase + ".-");

		if (model.getSalary() > 0) {
			int imporovePercent = increase * 100 / model.getSalary();
			int mood = model.getMood() + imporovePercent;
			if (mood > 100) mood = 100;
			model.setMood(mood);
		}

		model.setSalary(salary);
		ModelDbAdapter.updateModel(model);

		RejectReasons rr = bookingRejectReasons(modelId);

		for (Today today : TodayService.getTodayEventsForModel(modelId)) {
			// drop raise + quit requests if current raise was enough
			if (today.getEvent().getEclass() == EventClass.REQUEST
				&& (today.getEvent().getFlag() == EventFlag.RAISE
					|| today.getEvent().getFlag() == EventFlag.QUIT)) {
				// amount2 is minimum raise in both cases:
				if (salary >= today.getAmount2()) {
					TodayDbAdapter.removeToday(today.getId());
				}
			}
			// unreject booking if mood has improved
			if (today.getEvent().getEclass() == EventClass.BOOKREJECT && !rr.willReject()) {
				EventService.unrejectBooking(today);
			}
		}
	}

	public static void grantBonus(int modelId, int bonus) {
		grantBonus(modelId, bonus, 0);
	}

	public static void grantBonus(int modelId, int bonus, int expect) {
		TransactionService.transfer(0, modelId, bonus, MessageService.getMessage(R.string.accountmessage_bonus));
		Statistics st = getStatistics(modelId);
		st.w4bonus += bonus;
		DiaryService.log(MessageService.getMessage(R.string.logmessage_bonus) + " " + Util.amount(bonus),
				EventClass.ACCEPT, EventFlag.BONUS, modelId, bonus);
		bonusMoodImpact(modelId, bonus, expect);
		TodayService.updateBookingRequestAcceptance(modelId);
	}

	public static void bonusMoodImpact(int modelId, int bonus, int expect) {
		if (expect == 0) {
			Model model = getModelById(modelId);
			int salary = (model.getSalary() > 0) ? model.getSalary() : 1;
			int satisfaction = (int)((double)bonus * 10 / salary);
			Log.d(LOG_TAG, "Salary: " + model.getSalary() + ".- bonus " + bonus + " -> mood + " + satisfaction);
			improveMood(modelId, satisfaction);
		}
		else if (expect > 0) {
			int fairExpect = getExpectedBonus(modelId);
			int expectrange = expect - fairExpect;
			if (expectrange < 1) expectrange = 1;

			/* bonus > expect -> ..+60
			 * bonus = expect -> + 30
			 * bonus = fairexpect -> +- 0
			 * bonus < fairexpect -> ..-10 */
			int satisfaction = (int)(30 * ((double)bonus - fairExpect) / expectrange);
			Log.d(LOG_TAG, "Expected min: " + fairExpect + ".- opt: " + expect + ", got " + bonus + " -> mood + " + satisfaction);
			if (satisfaction < -10) satisfaction = -10;

			improveMood(modelId, satisfaction);
		}
	}

	public static void sendToTraining(int modelId, int trainingId) {
		ModelTrainingService.bookTraining(modelId, trainingId);
		TodayService.dropEvents(modelId, EventClass.REQUEST, EventFlag.VACATION);
	}

	public static void grantVacation(int modelId) {
		Model model = getModelById(modelId);
		if (model.getVacrest() < 1) {
			Log.w(LOG_TAG, model.getFullname() + " has no vacation days left.");
		}
		if (isModelBookedToday(modelId)) {
			 // start vacation tomorrow
			model.setStatus(ModelStatus.VACATION);
			ModelDbAdapter.updateModel(model);

			Statistics st = getStatistics(modelId);
			st.lastVacation = DiaryService.today() + 1;
		}
		else {
			// start vacation today
			model.setStatus(ModelStatus.VACATION);
			if (DiaryService.todayWeekday() != Weekday.SUNDAY)
				model.setVacrest(model.getVacrest() - 1);
			ModelDbAdapter.updateModel(model);

			if (DiaryService.todayWeekday() != Weekday.SUNDAY) {
				DiaryService.log(MessageService.getMessage(R.string.logmessage_vacation_today, model),
						EventClass.ACCEPT, EventFlag.VACATION, model.getId(), 0);

				Statistics st = getStatistics(modelId);
				st.lastVacation = DiaryService.today();
				st.w4daysVacation++;
				clearTodaysBookings(modelId, EventFlag.PHOTO);
			}
		}
	}

	public static void reportSick(int modelId) {
		Model model = getModelById(modelId);
		if (model.getStatus() != ModelStatus.SICK) {
			model.setStatus(ModelStatus.SICK);
			ModelDbAdapter.updateModel(model);
		}
		Statistics st = getStatistics(modelId);
		st.w4daysSick++;
		clearTodaysBookings(modelId, EventFlag.PHOTO);
	}

	public static void reportTraining(int modelId) {
		Model model = getModelById(modelId);
		if (model.getStatus() != ModelStatus.TRAINING) {
			model.setStatus(ModelStatus.TRAINING);
			ModelDbAdapter.updateModel(model);
		}
		Statistics st = getStatistics(modelId);
		st.w4daysTraining++;
	}

	public static void reportHealthy(int modelId) {
		Model model = getModelById(modelId);
		model.setStatus(ModelStatus.HIRED);
		ModelDbAdapter.updateModel(model);
	}

	public static void returnFromTraining(int modelId, ModelTraining training) {
		Model model = getModelById(modelId);
		model.setStatus(ModelStatus.HIRED);
		model.setQuality_photo(model.getQuality_photo() + training.getTraining().getInc_qphoto());
		model.setQuality_movie(model.getQuality_movie() + training.getTraining().getInc_qmovie());
		model.setQuality_tlead(model.getQuality_tlead() + training.getTraining().getInc_qtlead());
		model.setErotic(       model.getErotic()        + training.getTraining().getInc_erotic());
		model.setHealth(       model.getHealth()        + training.getTraining().getInc_health());
		model.setAmbition(     model.getAmbition()      + training.getTraining().getInc_ambition());
		model.setMood(         model.getMood()          + training.getTraining().getInc_mood());
		model.setCriminal(     model.getCriminal()      + training.getTraining().getInc_criminal());
		ModelDbAdapter.updateModel(model);
	}

	public static void reportBooking(Today today) {
		Statistics st = getStatistics(today.getModelId());
		EventFlag flag = EventFlag.MOVIE; // clear only movie-bookings
		if (today.getEvent().getFlag() == EventFlag.PHOTO) {
			st.latestPhoto = DiaryService.today();
			st.w4photoEarnings += today.getAmount1();
			st.w4photoSessions++;
			st.photoToday++;
			if (st.photoToday > 1) flag = EventFlag.PHOTO; // clear all bookings
		}
		else {
			st.latestMovie = DiaryService.today();
			st.w4movieEarnings += today.getAmount1();
			st.w4movieSessions++;
			st.movieToday++;
			flag = EventFlag.PHOTO; // clear all bookings
		}
		clearTodaysBookings(today.getModelId(), flag);
		updateBonusRequests(today.getModelId());

		// bad mood if model has no car or small car
		if (today.getModel().getCarId() <= 0) {
			improveMood(today.getModelId(), -20);
		}
		else {
			CompanyCar cc = CarService.getCompanyCar(today.getModel().getCarId());
			if (cc.getStatus() != CarStatus.IN_USE)
				improveMood(today.getModelId(), -20);
			else if (cc.getCarType().getCclass() == CarClass.SMALL)
				improveMood(today.getModelId(), -10);
		}
	}

	public static void reportTeamleadWork(int teamleaderModelId) {
		// FIXME: implement
		Statistics st = getStatistics(teamleaderModelId);
		st.teamleadToday++;
	}

	/**
	 * Set open booking requests for modelId to unspecified model.
	 * @param modelId
	 * @param flag
	 * 			MOVIE: substitute only MOVIE bookings
	 * 			PHOTO: substitute all bookings
	 */
	public static void clearTodaysBookings(int modelId, EventFlag flag) {
		for (Today today : TodayService.getTodayEventsForModel(modelId)) {
			if ((today.getEvent().getEclass() == EventClass.BOOKING
				|| today.getEvent().getEclass() == EventClass.BOOKREJECT)
					&& (today.getEvent().getFlag() == EventFlag.MOVIE || flag == EventFlag.PHOTO)) {
				// must substitute
				clearBooking(today);
			}
		}
	}

	/**
	 * Update bonus-requests if they demand a too low amount
	 */
	public static void updateBonusRequests(int modelId) {
		int expectedBonus = getExpectedBonus(modelId);
		for (Today today : TodayService.getTodayEventsForModel(modelId)) {
			if (today.getEvent().getEclass() == EventClass.REQUEST && today.getEvent().getFlag() == EventFlag.BONUS) {
				if (today.getAmount1() < expectedBonus || today.getAmount2() < expectedBonus) {
					today.setAmount1(Util.niceRandom(expectedBonus, 2 * expectedBonus));
					today.setAmount2(expectedBonus);
					TodayDbAdapter.updateToday(today);
				}
			}
		}
	}

	/**
	 * Set open booking request to unspecified model.
	 */
	public static void clearBooking(Today today) {

		Model model = today.getModel();
		Model subst = getModelById(-1);

		if (today.getEvent().getEclass() != EventClass.BOOKING && today.getEvent().getEclass() != EventClass.BOOKREJECT)
			throw new IllegalArgumentException("clearBookings called with " + today.getEvent().getEclass());

		if (today.getEvent().getEclass() == EventClass.BOOKREJECT)
			EventService.unrejectBooking(today);

		int newOffer = today.getAmount1();
		int newMax = today.getAmount2();
		if (today.getEvent().getFlag() == EventFlag.PHOTO) {
			int mq = model.getQuality_photo() > 0 ? model.getQuality_photo() : 1;
			int sq = subst.getQuality_photo() > 0 ? subst.getQuality_photo() : 1;
			newOffer = newOffer / mq * sq;
			newMax = newMax / mq * sq;
		}
		else {
			int mq = model.getQuality_movie() > 0 ? model.getQuality_movie() : 1;
			int sq = subst.getQuality_movie() > 0 ? subst.getQuality_movie() : 1;
			newOffer = newOffer / mq * sq;
			newMax = newMax / mq * sq;
		}
		today.setModelId(-1);
		today.setAmount1(newOffer);
		today.setAmount2(newMax);

		TodayDbAdapter.updateToday(today);
	}

	public static void clearApplications(int modelId) {
		for (Today today : TodayService.getTodayEventsForModel(modelId)) {
			if (today.getEvent().getEclass() == EventClass.APPLICATION) {
				TodayDbAdapter.removeToday(today.getId());
			}
		}
	}

	/* ------------- */

	public static void setVacation(int modelId, int vacation) {
		Model model = getModelById(modelId);
		model.setVacation(vacation);
		ModelDbAdapter.updateModel(model);
	}

	public static void setVacationRest(int modelId, int vacation) {
		Model model = getModelById(modelId);
		model.setVacrest(vacation);
		ModelDbAdapter.updateModel(model);
	}

	public static void setCar(int modelId, int companyCarId) {
		Model model = getModelById(modelId);
		if (model.getCarId() > 0) {

			// register no-driver for previous car
			CarService.assignCar(model.getCarId(), -1);

			if (companyCarId == 0) {
				// car de-assigned and no new car to assign - bad mood:
				int mood = model.getMood();
				CompanyCar cc = CarService.getCompanyCar(model.getCarId());
				switch(cc.getCarType().getCclass()) {
				case SMALL:
					mood -= 5;
					break;
				case MEDIUM:
					mood -= 10;
					break;
				case LARGE:
					mood -= 15;
					break;
				case SPORTS:
				case LUXURY:
					mood -= 20;
					break;
				}
				if (mood < 0) mood = 0;
				model.setMood(mood);
				Log.i(LOG_TAG, "Setting car #" + companyCarId + " for model #" + modelId + " decreasing mood to " + mood);
			}
		}
		model.setCarId(companyCarId);

		if (companyCarId > 0) {

			// register new driver for new car
			CarService.assignCar(companyCarId, modelId);

			int mood = model.getMood();
			CompanyCar cc = CarService.getCompanyCar(companyCarId);
			switch(cc.getCarType().getCclass()) {
			case SMALL:
				mood += 10;
				break;
			case MEDIUM:
				mood += 20;
				break;
			case LARGE:
				mood += 30;
				break;
			case SPORTS:
			case LUXURY:
				mood += 40;
				break;
			}

			if (cc.getBuyday() == DiaryService.today()) mood += 10;
			if (mood > 100) mood = 100;
			model.setMood(mood);
			Log.i(LOG_TAG, "Setting car #" + companyCarId + " for model #" + modelId + " increasing mood to " + mood);
			TodayService.dropEvents(modelId, EventClass.NOTIFICATION,
					EventFlag.CAR_BROKEN, EventFlag.CAR_STOLEN, EventFlag.CAR_WRECKED);
		}
		ModelDbAdapter.updateModel(model);
		TodayService.updateBookingRequestAcceptance(modelId);

	}

	public static void improveMood(int modelId, int points) {
		Model model = getModelById(modelId);
		int mood = model.getMood() + points;
		if (mood < 0) mood = 0;
		if (mood > 100) mood = 100;
		model.setMood(mood);
		ModelDbAdapter.updateModel(model);
	}

	public static void improveCriminality(int modelId, int points) {
		Model model = getModelById(modelId);
		int crimLevel = model.getCriminal() + points;
		if (crimLevel < 0) crimLevel = 0;
		if (crimLevel > 100) crimLevel = 100;
		model.setCriminal(crimLevel);
		ModelDbAdapter.updateModel(model);
	}

	public static void newDay() {
		for (int i = 0; i < modelStatistics.size(); i++) {
			Statistics st = modelStatistics.valueAt(i);
			st.photoToday = 0;
			st.movieToday = 0;
			st.teamleadToday = 0;
		}
	}

	/* ------------- */

	public static class ModelAmbitionComparator implements Comparator<Model> {
		@Override
		public int compare(Model lhs, Model rhs) {
			if (lhs.getAmbition() < rhs.getAmbition()) return 1;
			if (lhs.getAmbition() > rhs.getAmbition()) return -1;
			return 0;
		}
	}

    public static class ModelNameComparator implements Comparator<Model> {
    	@Override
    	public int compare(Model lhs, Model rhs) {
    		return lhs.getFullname().compareTo(rhs.getFullname());
    	}
    }

    public static class ModelQualityComparator implements Comparator<Model> {
    	private Quality quality;
    	public ModelQualityComparator(Quality q) { quality = q; }
    	@Override
    	public int compare(Model lhs, Model rhs) {
    		switch (quality) {
    		case PHOTO:
    			if (lhs.getQuality_photo() < rhs.getQuality_photo()) return 1;
    			if (lhs.getQuality_photo() > rhs.getQuality_photo()) return -1;
    			break;
    		case MOVIE:
    			if (lhs.getQuality_movie() < rhs.getQuality_movie()) return 1;
    			if (lhs.getQuality_movie() > rhs.getQuality_movie()) return -1;
    			break;
    		case TEAMLEADER:
    			if (lhs.getQuality_tlead() < rhs.getQuality_tlead()) return 1;
    			if (lhs.getQuality_tlead() > rhs.getQuality_tlead()) return -1;
    			break;
    		}
    		return 0;
    	}
    }

	public static class Statistics {
		public int updateDay;
		public int latestPhoto;
		public int photoToday;
		public int latestMovie;
		public int movieToday;
		public int teamleadToday;
		public int lastVacation;
		public int lastTraining;
		public int latestQuit;
		public int latestUnavailable;
		public int latestAvailable;

		// aggregation over past 4 weeks
		public int w4photoSessions;
		public int w4photoEarnings;
		public int w4movieSessions;
		public int w4movieEarnings;
		public int w4daysTraining;
		public int w4daysVacation;
		public int w4daysSick;
		public int w4payments;
		public int w4bonus;
	}
}
