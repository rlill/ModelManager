package de.rlill.modelmanager.setup;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.rlill.modelmanager.persistance.CarFileDbAdapter;

public class CarFileSetup {

	private static final String LOG_TAG = CarFileSetup.class.getSimpleName();

	public static void setup(SQLiteDatabase db) {
		// create table
		try {
			Log.i(LOG_TAG, "Create table " + CarFileDbAdapter.TABLE_NAME_CAR_FILE);
			db.execSQL(CarFileDbAdapter.CREATE_CAR_FILE_TABLE);
		} catch (Exception exception) {
			Log.e(LOG_TAG, "Exception onCreate()", exception);
		}
		Log.i(LOG_TAG, "Table " + CarFileDbAdapter.TABLE_NAME_CAR_FILE + " created.");
	}

	public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(LOG_TAG, "Upgrade table " + CarFileDbAdapter.TABLE_NAME_CAR_FILE + " from version " + oldVersion + " to version " + newVersion);

		if (oldVersion <= 24 && newVersion >= 25) {
			setup(db);
		}
	}
}
