package de.rlill.modelmanager.persistance;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import de.rlill.modelmanager.model.Diary;
import de.rlill.modelmanager.service.DiaryService;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.struct.DiaryIterator;
import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;

public class DiaryDbAdapter extends DbAdapter {

	private static final String LOG_TAG = DiaryDbAdapter.class.getSimpleName();

	public static final String TABLE_NAME_DIARY = "tbl_diary";

	public static final String KEY_DAY = "day";
	public static final String KEY_EVENT_CLASS = "event_class";
	public static final String KEY_EVENT_FLAG = "event_flag";
	public static final String KEY_MODEL_ID = "modelId";
	public static final String KEY_AMOUNT = "amount";
	public static final String KEY_DESCRIPTION = "description";

	public static final String CREATE_DIARY_TABLE = "create table " + TABLE_NAME_DIARY + "("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_DAY + " INTEGER, "
			+ KEY_EVENT_CLASS + " INTEGER, "
			+ KEY_EVENT_FLAG + " INTEGER, "
			+ KEY_MODEL_ID + " INTEGER, "
			+ KEY_AMOUNT + " NUMERIC, "
			+ KEY_DESCRIPTION + " TEXT)";

    /**
     * List events
     * @param modelId filter only events for this model, -1 = all
     * @param startDay filter only events from that day or later, -1 = all
     */
    public static List<Diary> listEvents(int modelId, int startDay) {
    	List<Diary> result = new ArrayList<Diary>();

    	List<String> matchKeys = new ArrayList<String>();
    	List<String> matchValues = new ArrayList<String>();
    	if (modelId >= 0) {
    		matchKeys.add(KEY_MODEL_ID + "=?");
    		matchValues.add(Integer.toString(modelId));
    	}
    	if (startDay >= 0) {
    		matchKeys.add(KEY_DAY + ">=?");
    		matchValues.add(Integer.toString(startDay));
    	}

    	SQLiteDatabase db = open();
    	Cursor cursor = db.query(
    			TABLE_NAME_DIARY,
    			null,
    			TextUtils.join(" AND ", matchKeys),
    			matchValues.toArray(new String[matchValues.size()]),
    			null, null, KEY_ID + " desc", null);

    	if (cursor.moveToFirst()) {
    		do {
    			Diary diary = readCursorLine(cursor);
    			result.add(diary);
    		} while (cursor.moveToNext());
    	}
    	cursor.close();

    	return result;
    }

    public static List<Diary> listEventsForModel(int modelId) {
    	return listEvents(modelId, -1);
    }

    /**
     * List events for today
     */
    public static List<Diary> listEventsForToday() {
    	return listEvents(-1, DiaryService.today());
    }

    /**
     * Query all events since day 'startDay' for operationsChart.
     */
    public static DiaryIterator listOperationsEventsSince(int startDay) {
    	SQLiteDatabase db = open();
    	Cursor cursor = db.query(
    			TABLE_NAME_DIARY,
    			null,
    			KEY_DAY + ">=? AND " + KEY_MODEL_ID + ">0",
    			new String[] { String.valueOf(startDay) },
    			null, null, KEY_ID + " asc", null);

    	return new DiaryIterator(cursor);
    }

    public static Diary getDiaryEntry(int id) {
    	Diary result = null;
    	SQLiteDatabase db = open();
    	Cursor cursor = db.query(
    			TABLE_NAME_DIARY,
    			null,
    			KEY_ID + "=?",
    			new String[] { String.valueOf(id) },
    			null, null, null, "1");
    	if (cursor.moveToFirst()) {
    		result = readCursorLine(cursor);
    	}
    	cursor.close();
    	return result;
    }

    public static void update(Diary diary) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
//    	values.put(KEY_DAY, diary.getDay());
//    	values.put(KEY_EVENT_CLASS, (diary.getEventClass() != null) ? diary.getEventClass().getIndex() : 0);
//    	values.put(KEY_EVENT_FLAG, (diary.getEventFlag() != null) ? diary.getEventFlag().getIndex() : 0);
//    	values.put(KEY_MODEL_ID, diary.getModelId());
    	values.put(KEY_AMOUNT, diary.getAmount());
    	values.put(KEY_DESCRIPTION, diary.getDescription());

    	// update row
    	db.update(TABLE_NAME_DIARY, values, KEY_ID + "=?", new String[] { Integer.toString(diary.getId()) } );
    }

    public static Diary readCursorLine(Cursor cursor) {
    	Diary diary = new Diary(cursor.getInt(0));

    	diary.setDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DAY)));
    	diary.setEventClass(EventClass.getInstanceByIndex(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_EVENT_CLASS))));
    	diary.setEventFlag(EventFlag.getInstanceByIndex(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_EVENT_FLAG))));
    	diary.setModelId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MODEL_ID)));
    	diary.setAmount(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_AMOUNT)));
    	diary.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
    	return diary;
    }

    public static int addDiary(Diary diary) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_DAY, diary.getDay());
    	values.put(KEY_EVENT_CLASS, (diary.getEventClass() != null) ? diary.getEventClass().getIndex() : 0);
    	values.put(KEY_EVENT_FLAG, (diary.getEventFlag() != null) ? diary.getEventFlag().getIndex() : 0);
    	values.put(KEY_MODEL_ID, diary.getModelId());
    	values.put(KEY_AMOUNT, diary.getAmount());
    	values.put(KEY_DESCRIPTION, diary.getDescription());

    	// update row
    	return (int)db.insert(TABLE_NAME_DIARY, null, values);
    }

    public static int getMaxDay() {
        String countQuery = "SELECT max(" + KEY_DAY + ") FROM " + TABLE_NAME_DIARY;
        final SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery(countQuery, null);
        int maxDay = 1;
        if (cursor.moveToFirst()) maxDay = cursor.getInt(0);
        cursor.close();

        return maxDay;
    }


    public static ModelService.Statistics getModelStatistics(int modelId) {
    	ModelService.Statistics stat = new ModelService.Statistics();
    	ModelService.Statistics init = new ModelService.Statistics();

        SQLiteDatabase db = open();
        Cursor cursor = db.query(
        		TABLE_NAME_DIARY,
        		null,
        		KEY_MODEL_ID + "=?",
        		new String[] { Integer.toString(modelId) },
                null, null, KEY_DAY + " desc", null);

        if (cursor.moveToFirst()) {
        	int today = DiaryService.today();
        	int w4day = today - 28;
            do {
            	Diary d = readCursorLine(cursor);

            	switch (d.getEventClass()) {
            	case BOOKING:
            		switch (d.getEventFlag()) {
            		case PHOTO:
            			if (init.latestPhoto == 0) {
            				stat.latestPhoto = d.getDay();
            				init.latestPhoto = 1;
            			}
            			if (d.getDay() > w4day) {
            				stat.w4photoEarnings += d.getAmount();
            				stat.w4photoSessions++;
            			}
            			if (d.getDay() == today) stat.photoToday++;
            			break;
            		case MOVIE:
            			if (init.latestMovie == 0) {
            				stat.latestMovie = d.getDay();
            				init.latestMovie = 1;
            			}
            			if (d.getDay() > w4day) {
            				stat.w4movieEarnings += d.getAmount();
            				stat.w4movieSessions++;
            			}
            			if (d.getDay() == today) stat.movieToday++;
            			break;
            		default:
            			Log.w(LOG_TAG, "Booked for unknown event type " + d.getEventFlag());
            		}
            		break;
            	case NOTIFICATION:
            		switch (d.getEventFlag()) {
            		case SICK:
            			if (d.getDay() > w4day) stat.w4daysSick++;
            			break;
            		case QUIT:
            			stat.latestQuit = d.getDay();
            			break;
            		case UNAVAILABLE:
            			stat.latestUnavailable = d.getDay();
            			break;
            		case AVAILABLE:
            			stat.latestAvailable = d.getDay();
            			break;
            		case PAYCHECK:
            		case CAR_BROKEN:
            		case CAR_STOLEN:
            		case CAR_WRECKED:
            		case VACATION:
            		case TRAINING:
            		case TRAINING_FIN:
            		case GROUPWORK:
            		case CHANGETEAM:
            		case HIRE:
            			break;
            		default:
            			Log.w(LOG_TAG, "Notification for unknown NOTIFICATION flag " + d.getEventFlag());
            		}
            		break;
            	case ACCEPT:
            		switch (d.getEventFlag()) {
            		case TRAINING:
            			if (d.getDay() > w4day) stat.w4daysTraining++;
            			if (init.lastTraining == 0) {
            				stat.lastTraining = d.getDay();
            				init.lastTraining = 1;
            			}
            			break;
            		case VACATION:
            			if (d.getDay() > w4day) stat.w4daysVacation++;
            			if (init.lastVacation == 0) {
            				stat.lastVacation = d.getDay();
            				init.lastVacation = 1;
            			}
            			break;
            		case QUIT:
            			stat.latestQuit = d.getDay();
            			break;
            		case PAYFIX_PERSON:
            		case PAYVAR_PERSON:
            		case PAYOPT_PERSON:
            			stat.w4payments += d.getAmount();
            			break;
            		case BONUS:
            			stat.w4bonus += d.getAmount();
            			break;
            		case RAISE:
            		case CAR_UPDATE:
            		case GROUPWORK:
			        case CHANGETEAM:
			        case HIRE:
            			break;
            		case PHOTO:
            			if (init.latestPhoto == 0) {
            				stat.latestPhoto = d.getDay();
            				init.latestPhoto = 1;
            			}
            			if (d.getDay() > w4day) {
            				stat.w4photoEarnings += d.getAmount();
            				stat.w4photoSessions++;
            			}
            			if (d.getDay() == today) stat.photoToday++;
            			break;
            		case MOVIE:
            			if (init.latestMovie == 0) {
            				stat.latestMovie = d.getDay();
            				init.latestMovie = 1;
            			}
            			if (d.getDay() > w4day) {
            				stat.w4movieEarnings += d.getAmount();
            				stat.w4movieSessions++;
            			}
            			if (d.getDay() == today) stat.movieToday++;
            			break;
            		default:
            			Log.w(LOG_TAG, "Accept for unknown event flag " + d.getEventFlag());
            		}
            		break;
            	case REQUEST:
            	case APPLICATION:
            	case EXTRA_IN:
            	case EXTRA_OUT:
            	case EXTRA_LOSS:
            		break;
            	default:
        			Log.w(LOG_TAG, "Unknown event class " + d.getEventClass());
            	}

            	if (d.getDay() < w4day && init.latestMovie != 0 && init.latestPhoto != 0 && init.lastVacation != 0) break;
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
        	Log.d(LOG_TAG, "STAT FOR " + ModelService.getModelById(modelId).getFullname());
	        Log.d(LOG_TAG, "STAT latestPhoto: " + stat.latestPhoto);
	        Log.d(LOG_TAG, "STAT photoToday: " + stat.photoToday);
	        Log.d(LOG_TAG, "STAT latestMovie: " + stat.latestMovie);
	        Log.d(LOG_TAG, "STAT movieToday: " + stat.movieToday);
	        Log.d(LOG_TAG, "STAT lastVacation: " + stat.lastVacation);
	        Log.d(LOG_TAG, "STAT lastTraining: " + stat.lastTraining);
	        Log.d(LOG_TAG, "STAT w4photoSessions: " + stat.w4photoSessions);
	        Log.d(LOG_TAG, "STAT w4photoEarnings: " + stat.w4photoEarnings);
	        Log.d(LOG_TAG, "STAT w4movieSessions: " + stat.w4movieSessions);
	        Log.d(LOG_TAG, "STAT w4movieEarnings: " + stat.w4movieEarnings);
	        Log.d(LOG_TAG, "STAT w4daysTraining: " + stat.w4daysTraining);
	        Log.d(LOG_TAG, "STAT w4daysVacation: " + stat.w4daysVacation);
	        Log.d(LOG_TAG, "STAT w4daysSick: " + stat.w4daysSick);
        }

    	return stat;
    }

	public static ModelService.Statistics getTotalStatistics() {
		ModelService.Statistics stat = new ModelService.Statistics();

		int startDay = DiaryService.today() - 28;
		if (startDay < 0) startDay = 0;
		Set<Integer> modelSet = new TreeSet<Integer>();

		SQLiteDatabase db = open();
		Cursor cursor = db.query(
				TABLE_NAME_DIARY,
				null,
				KEY_DAY + ">=?",
				new String[] { Integer.toString(startDay) },
				null, null, KEY_DAY + " desc", null);

		if (cursor.moveToFirst()) {
			do {
				Diary d = readCursorLine(cursor);

				switch (d.getEventClass()) {
					case BOOKING:
						switch (d.getEventFlag()) {
							case PHOTO:
								stat.w4photoEarnings += d.getAmount();
								stat.w4photoSessions++;
								modelSet.add(d.getModelId());
								break;
							case MOVIE:
								stat.w4movieEarnings += d.getAmount();
								stat.w4movieSessions++;
								modelSet.add(d.getModelId());
								break;
							default:
								Log.w(LOG_TAG, "Booked for unknown event type " + d.getEventFlag());
						}
						break;
					case NOTIFICATION:
						switch (d.getEventFlag()) {
							case SICK:
								stat.w4daysSick++;
								break;
						}
						break;
					case ACCEPT:
						switch (d.getEventFlag()) {
							case TRAINING:
								stat.w4daysTraining++;
								stat.w4payments += d.getAmount();
								break;
							case VACATION:
								stat.w4daysVacation++;
								break;
							case PAYFIX_PERSON:
							case PAYVAR_PERSON:
							case PAYOPT_PERSON:
								stat.w4payments += d.getAmount();
								modelSet.add(d.getModelId());
								break;
							case CAR_UPDATE:
								stat.extraLoss += d.getAmount();
								modelSet.add(d.getModelId());
								break;
							case BONUS:
								stat.w4bonus += d.getAmount();
								modelSet.add(d.getModelId());
								break;
							case RAISE:
							case GROUPWORK:
								break;
							case PHOTO:
								stat.w4photoEarnings += d.getAmount();
								stat.w4photoSessions++;
								modelSet.add(d.getModelId());
								break;
							case MOVIE:
								stat.w4movieEarnings += d.getAmount();
								stat.w4movieSessions++;
								modelSet.add(d.getModelId());
								break;
							default:
								Log.w(LOG_TAG, "Accept for unknown event flag " + d.getEventFlag());
						}
						break;
					case REQUEST:
					case APPLICATION:
						break;

					case EXTRA_IN:
					case EXTRA_OUT:
					case EXTRA_LOSS:
						switch (d.getEventFlag()) {
							case WIN_PERSON:
								stat.extraEarnings += d.getAmount();
								modelSet.add(d.getModelId());
								break;
							case PAYFIX_PERSON:
							case PAYVAR_PERSON:
							case PAYOPT_PERSON:
							case LOSE_PERSON:
								stat.extraLoss += d.getAmount();
								modelSet.add(d.getModelId());
								break;
							default:
						}
						break;
					case MOVIE_CAST:
						if (d.getEventFlag() == EventFlag.MOVIE) {
							stat.w4payments += d.getAmount();
							modelSet.add(d.getModelId());
						}
						break;

					default:
						Log.w(LOG_TAG, "Unknown event class " + d.getEventClass());
				}

			} while (cursor.moveToNext());
		}
		cursor.close();
		stat.extraModelCount = modelSet.size();

		return stat;
	}

}
