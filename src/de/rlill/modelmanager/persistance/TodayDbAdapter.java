package de.rlill.modelmanager.persistance;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.rlill.modelmanager.model.Today;
import de.rlill.modelmanager.struct.TodayStatus;

public class TodayDbAdapter extends DbAdapter {

	private static final String LOG_TAG = TodayDbAdapter.class.getSimpleName();

	public static final String TABLE_NAME_TODAY = "tbl_today";

	public static final String KEY_STATUS = "status";
	public static final String KEY_EVENT_ID = "eventId";
	public static final String KEY_MODEL_ID = "modelId";
	public static final String KEY_AMOUNT1 = "amount1";
	public static final String KEY_AMOUNT2 = "amount2";

	public static final String CREATE_TODAY_TABLE = "create table " + TABLE_NAME_TODAY + "("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_STATUS + " INTEGER, "
			+ KEY_EVENT_ID + " INTEGER, "
			+ KEY_MODEL_ID + " INTEGER, "
			+ KEY_AMOUNT1 + " NUMERIC, "
			+ KEY_AMOUNT2 + " NUMERIC)";

	// Getting single event
	public static Today getEvent(int id) {
		SQLiteDatabase db = open();
		Cursor cursor = db.query(
				TABLE_NAME_TODAY,
				null,
				KEY_ID + "=?",
				new String[] { String.valueOf(id) },
				null, null, null, null);

		Today today = null;
		if (cursor.moveToFirst()) today = readCursorLine(cursor);
        cursor.close();

		return today;
	}

	// Getting a certain event
	public static Today getEvent(int eventId, int modelId) {
		SQLiteDatabase db = open();
		Cursor cursor = db.query(
				TABLE_NAME_TODAY,
				null,
				KEY_EVENT_ID + "=? AND " + KEY_MODEL_ID + "=?",
				new String[] { String.valueOf(eventId), String.valueOf(modelId) },
				null, null, null, null);

		Today today = null;
		if (cursor.moveToFirst()) today = readCursorLine(cursor);
		cursor.close();

		return today;
	}

    /**
     * List all events
     */
    public static List<Today> getAllEvents(/* TODO: filter by status ? */) {
        List<Today> result = new ArrayList<Today>();
        SQLiteDatabase db = open();
        Cursor cursor = db.query(
        		TABLE_NAME_TODAY,
        		null,
        		null,
        		null,
                null, null, KEY_ID, null);

        if (cursor.moveToFirst()) {
            do {
            	Today today = readCursorLine(cursor);
                result.add(today);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    /**
     * List events for one model
     */
    public static List<Today> getEventsForModel(int modelId) {
    	List<Today> result = new ArrayList<Today>();
    	SQLiteDatabase db = open();
    	Cursor cursor = db.query(
    			TABLE_NAME_TODAY,
    			null,
    			KEY_MODEL_ID + "=?",
    			new String [] { Integer.toString(modelId) },
    			null, null, KEY_ID, null);

    	if (cursor.moveToFirst()) {
    		do {
    			Today today = readCursorLine(cursor);
    			result.add(today);
    		} while (cursor.moveToNext());
    	}
    	cursor.close();

    	return result;
    }

    private static Today readCursorLine(Cursor cursor) {
    	Today today = new Today(cursor.getInt(0));

    	today.setStatus(TodayStatus.getInstanceByIndex(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STATUS))));
    	today.setEventId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_EVENT_ID)));
    	today.setModelId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MODEL_ID)));
    	today.setAmount1(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_AMOUNT1)));
    	today.setAmount2(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_AMOUNT2)));
    	return today;
    }

    public static int updateToday(Today today) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_STATUS, today.getStatus().getIndex());
    	values.put(KEY_MODEL_ID, today.getModelId());
    	values.put(KEY_AMOUNT1, today.getAmount1());
    	values.put(KEY_AMOUNT2, today.getAmount2());
    	values.put(KEY_EVENT_ID, today.getEventId());

    	// update row
    	return db.update(
    			TABLE_NAME_TODAY,
    			values,
    			KEY_ID + " = ?",
    			new String[] { String.valueOf(today.getId()) }
    			);
    }

    public static int addToday(Today today) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_STATUS, today.getStatus().getIndex());
    	values.put(KEY_EVENT_ID, today.getEventId());
    	values.put(KEY_MODEL_ID, today.getModelId());
    	values.put(KEY_AMOUNT1, today.getAmount1());
    	values.put(KEY_AMOUNT2, today.getAmount2());

    	// update row
    	return (int)db.insert(TABLE_NAME_TODAY, null, values);
    }

    public static void removeToday(int id) {
    	final SQLiteDatabase db = open();
    	db.delete(TABLE_NAME_TODAY, KEY_ID + "=?", new String[] { Integer.toString(id) });
    }

}
