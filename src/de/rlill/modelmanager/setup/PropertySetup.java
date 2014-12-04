package de.rlill.modelmanager.setup;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.rlill.modelmanager.persistance.PropertyDbAdapter;

public class PropertySetup {

	private static final String LOG_TAG = PropertySetup.class.getSimpleName();

	public static void setup(SQLiteDatabase db) {
		// create table
		try {
			Log.i(LOG_TAG, "Create table " + PropertyDbAdapter.TABLE_NAME_PROPERTIES);
			db.execSQL(PropertyDbAdapter.CREATE_PROPERTIES_TABLE);
		} catch (Exception exception) {
			Log.e(LOG_TAG, "Exception onCreate()", exception);
		}
		Log.i(LOG_TAG, "Table " + PropertyDbAdapter.TABLE_NAME_PROPERTIES + " created.");
	}

	public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(LOG_TAG, "Upgrade table " + PropertyDbAdapter.TABLE_NAME_PROPERTIES + " from version " + oldVersion + " to version " + newVersion);

		if (oldVersion <= 9 && newVersion >= 10) {
			setup(db);
		}
	}
}
