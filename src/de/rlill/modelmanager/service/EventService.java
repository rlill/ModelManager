package de.rlill.modelmanager.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.util.Log;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.Event;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.Today;
import de.rlill.modelmanager.persistance.EventDbAdapter;
import de.rlill.modelmanager.persistance.TodayDbAdapter;
import de.rlill.modelmanager.setup.Events;
import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.TodayStatus;


public class EventService {

	private static final String LOG_TAG = EventService.class.getSimpleName();

	private static List<Event> extraEvents = null;
	private static Set<Integer> historyEventIds = new HashSet<Integer>();

	public static Today newTodayEvent(int eventId) {

		Event event = EventDbAdapter.getEvent(eventId);
		if (event == null) {
			Log.w(LOG_TAG, "Invalid event ID " + eventId);
			return null;
		}

		Today today = new Today();
		today.setStatus(TodayStatus.NEW);
		today.setEventId(eventId);
		today.setAmount1(Util.niceRandom(event.getAmountMin(), event.getAmountMax()));

		Model model = ModelService.findModelForEvent(event);
		if (model == null) {
			Log.w(LOG_TAG, "No Model found for Event " + event.getDescription());
			return null;
		}
		today.setModelId(model.getId());
		TodayDbAdapter.addToday(today);

		return today;
	}

	public static Today nextDayEvent() {
		Event event = EventDbAdapter.getAllEvents(EventClass.NOTIFICATION, EventFlag.NEWDAY).get(0);

		Today today = new Today();
		today.setStatus(TodayStatus.NEW);
		today.setEventId(event.getId());

		return today;
	}

	public static Today inDeptEvent(int balance) {
		Event event = EventDbAdapter.getAllEvents(EventClass.NOTIFICATION, EventFlag.BUSTED).get(0);

		Today today = new Today();
		today.setStatus(TodayStatus.NEW);
		today.setAmount1(balance);
		today.setEventId(event.getId());

		return today;
	}

	public static Today applicationEvent(int modelId) {
		Event event = EventDbAdapter.getAllEvents(EventClass.APPLICATION, EventFlag.HIRE).get(0);

		Today today = new Today();
		today.setStatus(TodayStatus.NEW);
		today.setModelId(modelId);
		today.setEventId(event.getId());
		int fair = ModelService.getAverageSalary(modelId);
		if (fair < event.getAmountMin()) fair = event.getAmountMin();
		today.setAmount1(Util.niceRound((fair * (200 + today.getModel().getAmbition())) / 200));
		today.setAmount2(Util.niceRound(fair));

		return today;
	}

	public static Today bookingEvent(EventFlag flag) {
		Event event = EventDbAdapter.getAllEvents(EventClass.BOOKING, flag).get(0);

		Today today = new Today();
		today.setStatus(TodayStatus.NEW);
		today.setEventId(event.getId());

		return today;
	}

	public static void resetEventHistory() {
		historyEventIds = new HashSet<Integer>();
	}

	public static Event getRandomExtraEvent() {
		WeightResult weightResult = exploreEvents(-1);
		int idx = Util.rnd(weightResult.chanceSum);
		weightResult = exploreEvents(idx);
		historyEventIds.add(weightResult.event.getId());
		return weightResult.event;
	}

	private static class WeightResult {
		public Event event;
		public int chanceSum;
	}

	private static WeightResult exploreEvents(int returnAt) {
		if (extraEvents == null) {
			extraEvents = EventDbAdapter.getAllEvents(EventClass.EXTRA_IN, null);
			extraEvents.addAll(EventDbAdapter.getAllEvents(EventClass.EXTRA_OUT, null));
			extraEvents.addAll(EventDbAdapter.getAllEvents(EventClass.EXTRA_LOSS, null));
		}
		WeightResult result = new WeightResult();
		result.chanceSum = 0;
		int abalance = TransactionService.getBalance(0);
		for (Event e : extraEvents) {
			if (historyEventIds.contains(e.getId())) continue;
			if (e.getMaxpercent() == 0 || abalance * e.getMaxpercent() / 100 > e.getAmountMin())
				result.chanceSum += e.getChance();
			if (returnAt >= 0 && result.chanceSum > returnAt) {
				result.event = e;
				return result;
			}
		}
		result.event = extraEvents.get(extraEvents.size() - 1);
		return result;
	}

	public static Today newNotification(int modelId, EventFlag flag, int amount) {
		return newNotification(modelId, flag, amount, 0);
	}

	public static Today newNotification(int modelId, EventFlag flag, int amount, int amount2) {
		Event event = EventDbAdapter.getAllEvents(EventClass.NOTIFICATION, flag).get(0);

		Today today = new Today();
		today.setStatus(TodayStatus.NEW);
		today.setModelId(modelId);
		today.setEventId(event.getId());
		today.setAmount1(amount);
		today.setAmount2(amount2);
		int tid = TodayDbAdapter.addToday(today);

		return TodayDbAdapter.getEvent(tid);
	}

	public static Today currentNotification(int modelId, EventFlag flag) {
		Event event = EventDbAdapter.getAllEvents(EventClass.NOTIFICATION, flag).get(0);
		return TodayDbAdapter.getEvent(event.getId(), modelId);
	}

	public static Today newCarAccidentEvent(int modelId) {
		Event event = EventDbAdapter.getEventBySystemId(Events.SYSTEM_ID_CAR_ACCIDENT_COST);

		Today today = new Today();
		today.setStatus(TodayStatus.NEW);
		today.setModelId(modelId);
		today.setEventId(event.getId());
		today.setAmount1(Util.niceRandom(event.getAmountMin(), event.getAmountMax()));
		TodayDbAdapter.addToday(today);

		return today;
	}

	public static Today newRaiseSalaryRequest(int modelId, int expectSalary, int minSalary) {
		Event event = EventDbAdapter.getAllEvents(EventClass.REQUEST, EventFlag.RAISE).get(0);

		Today today = new Today();
		today.setStatus(TodayStatus.NEW);
		today.setModelId(modelId);
		today.setAmount1(expectSalary);
		today.setAmount2(minSalary);
		today.setEventId(event.getId());
		TodayDbAdapter.addToday(today);

		return today;
	}

	public static Today newBonusRequest(int modelId, int expectBonus, int minBonus) {
		Event event = EventDbAdapter.getAllEvents(EventClass.REQUEST, EventFlag.BONUS).get(0);

		Today today = new Today();
		today.setStatus(TodayStatus.NEW);
		today.setModelId(modelId);
		today.setAmount1(expectBonus);
		today.setAmount2(minBonus);
		today.setEventId(event.getId());
		TodayDbAdapter.addToday(today);

		return today;
	}

	public static Today newQuitRequest(int modelId, int expectBonus, int expectSalary) {
		Event event = EventDbAdapter.getAllEvents(EventClass.REQUEST, EventFlag.QUIT).get(0);

		Today today = new Today();
		today.setStatus(TodayStatus.NEW);
		today.setModelId(modelId);
		today.setAmount1(expectBonus);
		today.setAmount2(expectSalary);
		today.setEventId(event.getId());
		TodayDbAdapter.addToday(today);

		return today;
	}

	public static Today newVacationRequest(int modelId) {
		Event event = EventDbAdapter.getAllEvents(EventClass.REQUEST, EventFlag.VACATION).get(0);

		Today today = new Today();
		today.setStatus(TodayStatus.NEW);
		today.setModelId(modelId);
		today.setEventId(event.getId());
		TodayDbAdapter.addToday(today);

		return today;
	}

	public static Today newTrainingRequest(int modelId) {
		Event event = EventDbAdapter.getAllEvents(EventClass.REQUEST, EventFlag.TRAINING).get(0);

		Today today = new Today();
		today.setStatus(TodayStatus.NEW);
		today.setModelId(modelId);
		today.setEventId(event.getId());
		TodayDbAdapter.addToday(today);

		return today;
	}

	public static Today newCarRequest(int modelId) {
		Event event = EventDbAdapter.getAllEvents(EventClass.REQUEST, EventFlag.CAR_UPDATE).get(0);

		Today today = new Today();
		today.setStatus(TodayStatus.NEW);
		today.setModelId(modelId);
		today.setEventId(event.getId());
		TodayDbAdapter.addToday(today);

		return today;
	}

	public static Today newGambleEvent() {
		Event event = EventDbAdapter.getAllEvents(EventClass.GAMBLE, EventFlag.WIN).get(0);

		Today today = new Today();
		today.setStatus(TodayStatus.NEW);
		today.setModelId(0);
		today.setEventId(event.getId());
		TodayDbAdapter.addToday(today);

		return today;
	}

	public static void rejectBooking(Today today) {
		if (today.getEvent().getEclass() != EventClass.BOOKING) throw new IllegalArgumentException("rejectBooking called with " + today.getEvent().getEclass());
		Event event = EventDbAdapter.getAllEvents(EventClass.BOOKREJECT, today.getEvent().getFlag()).get(0);
		today.setEventId(event.getId());
	}

	public static void unrejectBooking(Today today) {
		if (today.getEvent().getEclass() != EventClass.BOOKREJECT) throw new IllegalArgumentException("unrejectBooking called with " + today.getEvent().getEclass());
		Event event = EventDbAdapter.getAllEvents(EventClass.BOOKING, today.getEvent().getFlag()).get(0);
		today.setEventId(event.getId());
	}

	public static void convertToQuit(Today today) {
		Event event = EventDbAdapter.getAllEvents(EventClass.NOTIFICATION, EventFlag.QUIT).get(0);
		today.setEventId(event.getId());
	}
}
