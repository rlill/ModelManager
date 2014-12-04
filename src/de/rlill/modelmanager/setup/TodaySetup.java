package de.rlill.modelmanager.setup;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.rlill.modelmanager.persistance.TodayDbAdapter;

public class TodaySetup {

	private static final String LOG_TAG = TodaySetup.class.getSimpleName();

	public static void setup(SQLiteDatabase db) {
		// create table
		try {
			Log.i(LOG_TAG, "Create table " + TodayDbAdapter.TABLE_NAME_TODAY);
			db.execSQL(TodayDbAdapter.CREATE_TODAY_TABLE);
		} catch (Exception exception) {
			Log.e(LOG_TAG, "Exception onCreate()", exception);
		}
		Log.i(LOG_TAG, "Table " + TodayDbAdapter.TABLE_NAME_TODAY + " created.");
	}

	public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(LOG_TAG, "Upgrade table " + TodayDbAdapter.TABLE_NAME_TODAY + " from version " + oldVersion + " to version " + newVersion);
	}
}
