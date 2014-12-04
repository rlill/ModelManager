package de.rlill.modelmanager.setup;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.rlill.modelmanager.persistance.LoanDbAdapter;

public class LoanSetup {

	private static final String LOG_TAG = "MM*" + LoanSetup.class.getSimpleName();

	public static void setup(SQLiteDatabase db) {
		// create table
		try {
			Log.i(LOG_TAG, "Create table " + LoanDbAdapter.TABLE_NAME_LOAN);
			db.execSQL(LoanDbAdapter.CREATE_LOAN_TABLE);
		} catch (Exception exception) {
			Log.e(LOG_TAG, "Exception onCreate()", exception);
		}
		Log.i(LOG_TAG, "Table " + LoanDbAdapter.TABLE_NAME_LOAN + " created.");
	}

	public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(LOG_TAG, "Upgrade table " + LoanDbAdapter.TABLE_NAME_LOAN + " from version " + oldVersion + " to version " + newVersion);

		if (oldVersion <= 22 && newVersion >= 23) setup(db);
	}
}
