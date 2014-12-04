package de.rlill.modelmanager.setup;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.rlill.modelmanager.persistance.DiaryDbAdapter;

public class DiarySetup {

	private static final String LOG_TAG = DiarySetup.class.getSimpleName();

	public static void setup(SQLiteDatabase db) {
		// create table
		try {
			Log.i(LOG_TAG, "Create table " + DiaryDbAdapter.TABLE_NAME_DIARY);
			db.execSQL(DiaryDbAdapter.CREATE_DIARY_TABLE);
		} catch (Exception exception) {
			Log.e(LOG_TAG, "Exception onCreate()", exception);
		}
		Log.i(LOG_TAG, "Table " + DiaryDbAdapter.TABLE_NAME_DIARY + " created.");
	}

	public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(LOG_TAG, "Upgrade table " + DiaryDbAdapter.TABLE_NAME_DIARY + " from version " + oldVersion + " to version " + newVersion);

		if (oldVersion <= 5 && newVersion >= 6) {
			db.execSQL("alter table " + DiaryDbAdapter.TABLE_NAME_DIARY + " add column " + DiaryDbAdapter.KEY_AMOUNT + " NUMERIC");
		}
	}
}
