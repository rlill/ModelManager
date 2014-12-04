package de.rlill.modelmanager.setup;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.rlill.modelmanager.persistance.MovieModelDbAdapter;

public class MovieModelSetup {

	private static final String LOG_TAG = "MM*" + MovieModelSetup.class.getSimpleName();

	public static void setup(SQLiteDatabase db) {
		// create table
		try {
			Log.i(LOG_TAG, "Create table " + MovieModelDbAdapter.TABLE_NAME_MOVIE_MODEL);
			db.execSQL(MovieModelDbAdapter.CREATE_MOVIE_MODEL_TABLE);
		} catch (Exception exception) {
			Log.e(LOG_TAG, "Exception onCreate()", exception);
		}
		Log.i(LOG_TAG, "Table " + MovieModelDbAdapter.TABLE_NAME_MOVIE_MODEL + " created.");
	}

	public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(LOG_TAG, "Upgrade table " + MovieModelDbAdapter.TABLE_NAME_MOVIE_MODEL + " from version " + oldVersion + " to version " + newVersion);

		if (oldVersion <= 23 && newVersion >= 24) setup(db);
	}
}
