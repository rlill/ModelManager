package de.rlill.modelmanager.setup;

import java.util.Set;
import java.util.TreeSet;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import de.rlill.modelmanager.model.Event;
import de.rlill.modelmanager.persistance.EventDbAdapter;
import de.rlill.modelmanager.persistance.TodayDbAdapter;

public class EventSetup {

	private static final String LOG_TAG = EventSetup.class.getSimpleName();

	public static void setup(SQLiteDatabase db) {
		// create table
		try {
			Log.i(LOG_TAG, "Create table " + EventDbAdapter.TABLE_NAME_EVENT);
			db.execSQL(EventDbAdapter.CREATE_EVENT_TABLE);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Failed to create table " + EventDbAdapter.TABLE_NAME_EVENT + ": " + e.getMessage(), e);
		}
		Log.i(LOG_TAG, "Table " + EventDbAdapter.TABLE_NAME_EVENT + " created.");
	}

	public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(LOG_TAG, "Upgrade table " + EventDbAdapter.TABLE_NAME_EVENT + " from version " + oldVersion + " to version " + newVersion);

		if (oldVersion <= 15 && newVersion >= 16) {
			try {
				Log.i(LOG_TAG, "Extend table " + EventDbAdapter.TABLE_NAME_EVENT);
				db.execSQL("alter table " + EventDbAdapter.TABLE_NAME_EVENT
						+ " add column " + EventDbAdapter.KEY_SYSTEM_ID + " INTEGER");
			} catch (Exception exception) {
				Log.e(LOG_TAG, "Extend table " + EventDbAdapter.TABLE_NAME_EVENT + ":"
						+ exception.getMessage(), exception);
			}
			Log.i(LOG_TAG, "Table " + EventDbAdapter.TABLE_NAME_EVENT + " extended.");
		}

		if (oldVersion <= 16 && newVersion >= 17) {
			try {
				Log.i(LOG_TAG, "Extend table " + EventDbAdapter.TABLE_NAME_EVENT);
				db.execSQL("alter table " + EventDbAdapter.TABLE_NAME_EVENT
						+ " add column " + EventDbAdapter.KEY_VERSION + " INTEGER");
				db.execSQL("update " + EventDbAdapter.TABLE_NAME_EVENT
						+ " set " + EventDbAdapter.KEY_VERSION + " = 1");
				db.execSQL("alter table " + EventDbAdapter.TABLE_NAME_EVENT
						+ " add column " + EventDbAdapter.KEY_OBSOLETE + " INTEGER");
				db.execSQL("update " + EventDbAdapter.TABLE_NAME_EVENT
						+ " set " + EventDbAdapter.KEY_OBSOLETE + " = 0");
			} catch (Exception exception) {
				Log.e(LOG_TAG, "Extend table " + EventDbAdapter.TABLE_NAME_EVENT + ":"
						+ exception.getMessage(), exception);
			}
			Log.i(LOG_TAG, "Table " + EventDbAdapter.TABLE_NAME_EVENT + " extended.");
		}
	}

	public static void complete(SQLiteDatabase db) {
		Log.i(LOG_TAG, "Complete entries in " + EventDbAdapter.TABLE_NAME_EVENT);

		// collect system IDs + versions of the events available in DB
		SparseIntArray sysIds = new SparseIntArray();
		Cursor cursor = null;
		try {
			cursor = db.query(
					EventDbAdapter.TABLE_NAME_EVENT,
					new String [] { EventDbAdapter.KEY_SYSTEM_ID, EventDbAdapter.KEY_VERSION },
					null,
					null,
					null, null, null, null);


	    	if (cursor.moveToFirst()) {
	    		do {
	    			int si = cursor.getInt(0);
	    			int sv = cursor.getInt(1);
	    			sysIds.put(si, sv);
	    		} while (cursor.moveToNext());
	    	}
		}
		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to read systemIds: " + e.getMessage(), e);
		}
		finally {
	    	if (cursor != null) cursor.close();
		}

		// see if the setup event-set has new items
		for (Event e : Events.getEvents()) {

			// existing version
			int ev = sysIds.get(e.getSystemId(), 0);

			if (ev == e.getVersion()) continue;

			if (ev < e.getVersion()) {
				// set previous version to obsolete
				Log.i(LOG_TAG, "Deactivate " + e);
				ContentValues values = new ContentValues();
				values.put(EventDbAdapter.KEY_OBSOLETE, 1);
				db.update(EventDbAdapter.TABLE_NAME_EVENT,
						values,
						EventDbAdapter.KEY_SYSTEM_ID + "=?",
						new String [] { Integer.toString(e.getSystemId()) }
				);
			}

			Log.i(LOG_TAG, "Create " + e);

			// insert
			ContentValues values = new ContentValues();
			values.put(EventDbAdapter.KEY_SYSTEM_ID, e.getSystemId());
			values.put(EventDbAdapter.KEY_VERSION, e.getVersion());
			values.put(EventDbAdapter.KEY_OBSOLETE, 0);
			values.put(EventDbAdapter.KEY_ECLASS, e.getEclass().getIndex());
			values.put(EventDbAdapter.KEY_FLAG, e.getFlag().getIndex());
			values.put(EventDbAdapter.KEY_ICON, e.getIcon().getIndex());
			values.put(EventDbAdapter.KEY_DESCRIPTION, e.getDescription());
			values.put(EventDbAdapter.KEY_NOTE_FILE, nn(e.getNoteFile()));
			values.put(EventDbAdapter.KEY_NOTE_ACCT, nn(e.getNoteAcct()));
			values.put(EventDbAdapter.KEY_AMOUNT_MIN, e.getAmountMin());
			values.put(EventDbAdapter.KEY_AMOUNT_MAX, e.getAmountMax());
			values.put(EventDbAdapter.KEY_MAXPERCENT, e.getMaxpercent());
			values.put(EventDbAdapter.KEY_USECOUNT, 0);
			values.put(EventDbAdapter.KEY_FACTOR, e.getFactor());
			values.put(EventDbAdapter.KEY_CHANCE, e.getChance());
			db.insert(EventDbAdapter.TABLE_NAME_EVENT, null, values);
		}


		// store events currently referenced in an array
		// to allow removal of obsolete entries which are not in use
		Set<Integer> eventsInUse = new TreeSet<Integer>();
		cursor = null;
		try {
			cursor = db.query(
					true,
					TodayDbAdapter.TABLE_NAME_TODAY,
					new String [] { TodayDbAdapter.KEY_EVENT_ID },
					null,
					null,
					null, null, null, null);


	    	if (cursor.moveToFirst()) {
	    		do {
	    			eventsInUse.add(cursor.getInt(0));
	    		} while (cursor.moveToNext());
	    	}
		}
		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to read eventIds: " + e.getMessage(), e);
		}
		finally {
	    	if (cursor != null) cursor.close();
		}

		String queryClean = "delete from " + EventDbAdapter.TABLE_NAME_EVENT
				+ " where " + EventDbAdapter.KEY_OBSOLETE + "!=0"
				+ " and not " + EventDbAdapter.KEY_ID + " in (" + TextUtils.join(",", eventsInUse) + ")";
		Log.d(LOG_TAG, queryClean);
		db.execSQL(queryClean);
	}

	private static String nn(String s) {
		return (s == null) ? "" : s;
	}
}
