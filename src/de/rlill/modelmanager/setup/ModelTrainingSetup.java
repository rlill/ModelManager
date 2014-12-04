package de.rlill.modelmanager.setup;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.rlill.modelmanager.persistance.ModelTrainingDbAdapter;

public class ModelTrainingSetup {

	private static final String LOG_TAG = ModelTrainingSetup.class.getSimpleName();

	public static void setup(SQLiteDatabase db) {
		// create table
		try {
			Log.i(LOG_TAG, "Create table " + ModelTrainingDbAdapter.TABLE_NAME_MODEL_TRAINING);
			db.execSQL(ModelTrainingDbAdapter.CREATE_MODEL_TRAINING_TABLE);
		} catch (Exception exception) {
			Log.e(LOG_TAG, "Exception onCreate()", exception);
		}
		Log.i(LOG_TAG, "Table " + ModelTrainingDbAdapter.TABLE_NAME_MODEL_TRAINING + " created.");
	}

	public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(LOG_TAG, "Upgrade table " + ModelTrainingDbAdapter.TABLE_NAME_MODEL_TRAINING + " from version " + oldVersion + " to version " + newVersion);

		if (oldVersion <= 13 && newVersion >= 14) setup(db);
	}
}
