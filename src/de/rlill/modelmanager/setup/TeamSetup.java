package de.rlill.modelmanager.setup;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.rlill.modelmanager.persistance.TeamDbAdapter;

public class TeamSetup {

	private static final String LOG_TAG = TeamSetup.class.getSimpleName();

	public static void setup(SQLiteDatabase db) {
		// create table
		try {
			Log.i(LOG_TAG, "Create table " + TeamDbAdapter.TABLE_NAME_TEAM);
			db.execSQL(TeamDbAdapter.CREATE_TEAM_TABLE);
		} catch (Exception exception) {
			Log.e(LOG_TAG, "Exception onCreate()", exception);
		}
		Log.i(LOG_TAG, "Table " + TeamDbAdapter.TABLE_NAME_TEAM + " created.");
	}

	public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(LOG_TAG, "Upgrade table " + TeamDbAdapter.TABLE_NAME_TEAM + " from version " + oldVersion + " to version " + newVersion);

		if (oldVersion <= 21 && newVersion >= 22) {
			setup(db);
		}
	}

}
