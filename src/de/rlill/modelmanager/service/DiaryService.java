package de.rlill.modelmanager.service;

import java.util.List;

import android.util.Log;
import de.rlill.modelmanager.model.Diary;
import de.rlill.modelmanager.model.Today;
import de.rlill.modelmanager.persistance.DiaryDbAdapter;
import de.rlill.modelmanager.struct.DiaryIterator;
import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.Weekday;


public class DiaryService {

	private static final String LOG_TAG = "MM*" + DiaryService.class.getSimpleName();

	private static Integer today = null;

	public static int today() {
		if (today == null)
			today = DiaryDbAdapter.getMaxDay();
		return today;
	}

	public static Weekday todayWeekday() {
		return Weekday.getInstanceByIndex(today() % 7);
	}

	public static int newDay() {
		today();
		today++;
		Diary diary = new Diary();
		diary.setDay(today);
		diary.setEventClass(EventClass.NOTIFICATION);
		diary.setEventFlag(EventFlag.NEWDAY);
		DiaryDbAdapter.addDiary(diary);
		return today;
	}

	public static int log(Today t) {
		return log(t.getDescription(), t.getEvent().getEclass(), t.getEvent().getFlag(), t.getModelId(), t.getAmount1());
	}

	public static int fileLog(Today t) {
		return log(t.getNoteFile(), t.getEvent().getEclass(), t.getEvent().getFlag(), t.getModelId(), t.getAmount1());
	}

	public static void logAccept(Today t) {
		String msg = t.getNoteFile();
		if (msg == null) msg = t.getDescription();
		log(msg, EventClass.ACCEPT, t.getEvent().getFlag(), t.getModelId(), t.getAmount1());
	}

	public static int fileLogAccept(Today t) {
		return log(t.getNoteFile(), EventClass.ACCEPT, t.getEvent().getFlag(), t.getModelId(), t.getAmount1());
	}

	public static void logUpdateFile(Diary t) {
		Diary d = DiaryDbAdapter.getDiaryEntry(t.getId());
		if (d != null) {
			d.setDescription(t.getDescription());
			d.setAmount(t.getAmount());
			DiaryDbAdapter.update(d);
		} else
			log(t.getDescription(), t.getEventClass(), t.getEventFlag(), t.getModelId(), t.getAmount());
	}

	public static int log(String description, EventClass ec, EventFlag ef, int modelId, int amount) {
		Diary diary = new Diary();
		diary.setDay(today());
		diary.setDescription(description);
		diary.setEventClass(ec);
		diary.setEventFlag(ef);
		diary.setModelId(modelId);
		diary.setAmount(amount);
		int logId = DiaryDbAdapter.addDiary(diary);
		Log.i(LOG_TAG, "LOG #" + logId + ": " + description + "(" + ec + "/" + ef + "/model:" + modelId + "/amount:" + amount + ")");
		return logId;
	}

	public static List<Diary> listEventsForModel(int modelId) {
		return DiaryDbAdapter.listEventsForModel(modelId);
	}

	public static DiaryIterator listOperationsEventsSince(int startDay) {
		return DiaryDbAdapter.listOperationsEventsSince(startDay);
	}

	public static Diary getDiaryEntry(int id) {
		return DiaryDbAdapter.getDiaryEntry(id);
	}

	public static ModelService.Statistics getTotalStatistics() {
		return DiaryDbAdapter.getTotalStatistics();
	}
}
