package de.rlill.modelmanager.persistance;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import de.rlill.modelmanager.model.Event;
import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.EventIcon;

public class EventDbAdapter extends DbAdapter {

	private static final String LOG_TAG = EventDbAdapter.class.getSimpleName();

	public static final String TABLE_NAME_EVENT = "tbl_event";

	public static final String KEY_SYSTEM_ID = "system_id";
	public static final String KEY_VERSION = "version";
	public static final String KEY_OBSOLETE = "obsolete";
	public static final String KEY_ECLASS = "eclass";
	public static final String KEY_FLAG = "flag";
	public static final String KEY_ICON = "icon";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_NOTE_FILE = "note_file";
	public static final String KEY_NOTE_ACCT = "note_acct";
	public static final String KEY_AMOUNT_MIN = "amount_min";
	public static final String KEY_AMOUNT_MAX = "amount_max";
	public static final String KEY_MAXPERCENT = "maxpercent";
	public static final String KEY_FACTOR = "factor";
	public static final String KEY_CHANCE = "chance";
	public static final String KEY_USECOUNT = "usecount";

	public static final String CREATE_EVENT_TABLE = "create table " + TABLE_NAME_EVENT + "("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_SYSTEM_ID + " INTEGER, "
			+ KEY_VERSION + " INTEGER, "
			+ KEY_OBSOLETE + " INTEGER, "
			+ KEY_ECLASS + " INTEGER, "
			+ KEY_FLAG + " INTEGER, "
			+ KEY_ICON + " INTEGER, "
			+ KEY_DESCRIPTION + " TEXT, "
			+ KEY_NOTE_FILE + " TEXT, "
			+ KEY_NOTE_ACCT + " TEXT, "
			+ KEY_AMOUNT_MIN + " NUMERIC, "
			+ KEY_AMOUNT_MAX + " NUMERIC, "
			+ KEY_MAXPERCENT + " NUMERIC, "
			+ KEY_FACTOR + " NUMERIC, "
			+ KEY_CHANCE + " NUMERIC, "
			+ KEY_USECOUNT + " INTEGER)";

	// Retrieve single event
	public static Event getEvent(int id) {
		SQLiteDatabase db = open();
		Cursor cursor = db.query(
				TABLE_NAME_EVENT,
				null,
				KEY_ID + "=?",
				new String[] { String.valueOf(id) },
				null, null, null, null);

		Event event = null;
		if (cursor.moveToFirst()) event = readCursorLine(cursor);
        cursor.close();

		return event;
	}

	// Retrieve single event
	public static Event getEventBySystemId(int id) {
		SQLiteDatabase db = open();
		Cursor cursor = db.query(
				TABLE_NAME_EVENT,
				null,
				KEY_SYSTEM_ID + "=?",
				new String[] { String.valueOf(id) },
				null, null, null, null);

		Event event = null;
		if (cursor.moveToFirst()) event = readCursorLine(cursor);
		cursor.close();

		return event;
	}

    /**
     * List all events
     */
    public static List<Event> getAllEvents(EventClass eclass, EventFlag flag) {
        List<Event> result = new ArrayList<Event>();

		List<String> matchKeys = new ArrayList<String>();
		List<String> matchValues = new ArrayList<String>();

		matchKeys.add(KEY_OBSOLETE + "=?");
		matchValues.add("0");

        if (eclass != null) {
        	matchKeys.add(KEY_ECLASS + "=?");
        	matchValues.add(Integer.toString(eclass.getIndex()));
        }

        if (flag != null) {
        	matchKeys.add(KEY_FLAG + "=?");
        	matchValues.add(Integer.toString(flag.getIndex()));
        }

        SQLiteDatabase db = open();
        Cursor cursor = db.query(
        		TABLE_NAME_EVENT,
        		null,
				TextUtils.join(" and ", matchKeys),
				matchValues.toArray(new String[matchValues.size()]),
                null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
            	Event event = readCursorLine(cursor);
                result.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    private static Event readCursorLine(Cursor cursor) {
    	Event event = new Event(cursor.getInt(0));
    	event.setSystemId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SYSTEM_ID)));
    	event.setVersion(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_VERSION)));
    	event.setObsolete(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_OBSOLETE)) != 0);
    	event.setEclass(EventClass.getInstanceByIndex(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ECLASS))));
    	event.setFlag(EventFlag.getInstanceByIndex(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FLAG))));
    	event.setIcon(EventIcon.getInstanceByIndex(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ICON))));
    	event.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
    	event.setNoteFile(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTE_FILE)));
    	event.setNoteAcct(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTE_ACCT)));
    	event.setAmountMin(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_AMOUNT_MIN)));
    	event.setAmountMax(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_AMOUNT_MAX)));
    	event.setMaxpercent(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MAXPERCENT)));
    	event.setFactor(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FACTOR)));
    	event.setChance(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CHANCE)));
    	event.setUsecount(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_USECOUNT)));
    	return event;
    }

    public static int updateEvent(Event event) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_USECOUNT, event.getUsecount());

    	// update row
    	return db.update(
    			TABLE_NAME_EVENT,
    			values,
    			KEY_ID + " = ?",
    			new String[] { String.valueOf(event.getId()) }
    			);
    }

}
