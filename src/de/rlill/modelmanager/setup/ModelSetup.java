package de.rlill.modelmanager.setup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.persistance.ModelDbAdapter;
import de.rlill.modelmanager.struct.ModelStatus;

public class ModelSetup {

	private static final String LOG_TAG = "MM*" + ModelSetup.class.getSimpleName();

	public static void setup(SQLiteDatabase db) {
		// create table
		try {
			Log.i(LOG_TAG, "Create table " + ModelDbAdapter.TABLE_NAME_MODEL);
			db.execSQL(ModelDbAdapter.CREATE_MODEL_TABLE);
		} catch (Exception exception) {
			Log.e(LOG_TAG, "Exception onCreate()", exception);
		}
		Log.i(LOG_TAG, "Table " + ModelDbAdapter.TABLE_NAME_MODEL + " created.");
	}

	public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(LOG_TAG, "Upgrade table " + ModelDbAdapter.TABLE_NAME_MODEL + " from version " + oldVersion + " to version " + newVersion);
	}

	public static void complete(SQLiteDatabase db) {
		Log.i(LOG_TAG, "Complete entries in " + ModelDbAdapter.TABLE_NAME_MODEL);

		Set<String> imageIds = new HashSet<String>();
		Set<String> modelNames = new HashSet<String>();
		try {
			Cursor cursor = db.query(
					ModelDbAdapter.TABLE_NAME_MODEL,
					new String[] { ModelDbAdapter.KEY_IMAGE, ModelDbAdapter.KEY_FIRST_NAME, ModelDbAdapter.KEY_LAST_NAME },
					null,
					null,
					null, null, null, null);
			if (cursor.moveToFirst()) {
				do {
					imageIds.add(cursor.getString(0));
					modelNames.add(cursor.getString(1) + cursor.getString(2));
				} while (cursor.moveToNext());
			}
			cursor.close();
		} catch (SQLException e) { }

		for (Field f : R.drawable.class.getFields()) {
			String resourceName = f.getName();
			if (!resourceName.startsWith("face")) continue;

			if (imageIds.contains(resourceName)) {
				continue;
			}

			// first name
			int fni = rnd(rnd(rnd(rnd(Names.firstnames.length))));
			String firstName = Names.firstnames[fni];

			// last name with identical initial
			char ini = Names.firstnames[fni].charAt(0);
			List<String> lastnames = new ArrayList<String>();
			for (int i = 0; i < Names.lastnames.length; i++)
				if (Names.lastnames[i].charAt(0) == ini) lastnames.add(Names.lastnames[i]);
			int lni = rnd(rnd(rnd(rnd(lastnames.size()))));
			String lastName = lastnames.get(lni);

			if (modelNames.contains(firstName + lastName)) {
				Log.i(LOG_TAG, "Model with name " + firstName + " " + lastName + " already exists, skipping " + resourceName);
				continue;
			}

			ModelStatus status = (Util.rnd(4) == 0) ? ModelStatus.FREE : ModelStatus.UNAVAILABLE;
			Log.i(LOG_TAG, "Creating model " + firstName + " " + lastName + ", " + status + " for " + resourceName);

			// insert
			ContentValues values = new ContentValues();
			values.put(ModelDbAdapter.KEY_STATUS, status.getIndex());
			values.put(ModelDbAdapter.KEY_FIRST_NAME, firstName);
			values.put(ModelDbAdapter.KEY_LAST_NAME, lastName);
			values.put(ModelDbAdapter.KEY_IMAGE, resourceName);
			values.put(ModelDbAdapter.KEY_TEAM_ID, 0);
			values.put(ModelDbAdapter.KEY_SALARY, 0);
			values.put(ModelDbAdapter.KEY_PAYDAY, 0);
			values.put(ModelDbAdapter.KEY_VACATION, 0);
			values.put(ModelDbAdapter.KEY_VACREST, 0);
			values.put(ModelDbAdapter.KEY_COMPANY_CAR_ID, 0);
			values.put(ModelDbAdapter.KEY_QUALITY_PHOTO, 5 + 5 * rnd(20));
			values.put(ModelDbAdapter.KEY_QUALITY_MOVIE, 5 + 5 * rnd(20));
			values.put(ModelDbAdapter.KEY_EROTIC, 5 + 5 * rnd(20));
			values.put(ModelDbAdapter.KEY_QUALITY_TLEAD, 5 + 5 * rnd(20));
			values.put(ModelDbAdapter.KEY_CRIMINAL, 5 + 5 * rnd(20));
			values.put(ModelDbAdapter.KEY_AMBITION, 5 + 5 * rnd(20));
			values.put(ModelDbAdapter.KEY_MOOD, 5 + 5 * rnd(20));
			values.put(ModelDbAdapter.KEY_HEALTH, 5 + 5 * rnd(20));
			values.put(ModelDbAdapter.KEY_HIREDAY, 0);
			db.insert(ModelDbAdapter.TABLE_NAME_MODEL, null, values);
		}
	}

	private static int rnd(int v) {
		return (int)Math.floor(Math.random() * v);
	}
}
