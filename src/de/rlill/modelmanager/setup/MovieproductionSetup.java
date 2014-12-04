package de.rlill.modelmanager.setup;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.rlill.modelmanager.persistance.MovieproductionDbAdapter;

public class MovieproductionSetup {

	private static final String LOG_TAG = "MM*" + MovieproductionSetup.class.getSimpleName();

	public static void setup(SQLiteDatabase db) {
		// create table
		try {
			Log.i(LOG_TAG, "Create table " + MovieproductionDbAdapter.TABLE_NAME_MOVIEPRODUCTION);
			db.execSQL(MovieproductionDbAdapter.CREATE_MOVIEPRODUCTION_TABLE);
		} catch (Exception exception) {
			Log.e(LOG_TAG, "Exception onCreate()", exception);
		}
		Log.i(LOG_TAG, "Table " + MovieproductionDbAdapter.TABLE_NAME_MOVIEPRODUCTION + " created.");
	}

	public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(LOG_TAG, "Upgrade table " + MovieproductionDbAdapter.TABLE_NAME_MOVIEPRODUCTION + " from version " + oldVersion + " to version " + newVersion);

		if (oldVersion <= 23 && newVersion >= 24) setup(db);
	}
}
