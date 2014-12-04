package de.rlill.modelmanager.setup;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.persistance.CompanyCarDbAdapter;

public class CompanyCarSetup {

	private static final String LOG_TAG = CompanyCarSetup.class.getSimpleName();

	public static void setup(SQLiteDatabase db) {
		// create table
		try {
			Log.i(LOG_TAG, "Create table " + CompanyCarDbAdapter.TABLE_NAME_COMPANY_CAR);
			db.execSQL(CompanyCarDbAdapter.CREATE_COMPANY_CAR_TABLE);
		} catch (Exception exception) {
			Log.e(LOG_TAG, "Exception onCreate()", exception);
		}
		Log.i(LOG_TAG, "Table " + CompanyCarDbAdapter.TABLE_NAME_COMPANY_CAR + " created.");
	}

	public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(LOG_TAG, "Upgrade table " + CompanyCarDbAdapter.TABLE_NAME_COMPANY_CAR + " from version " + oldVersion + " to version " + newVersion);

		if (oldVersion <= 8 && newVersion >= 9) {
			setup(db);
		}
		if (oldVersion <= 17 && newVersion >= 18) {
			Log.i(LOG_TAG, "Extend table " + CompanyCarDbAdapter.TABLE_NAME_COMPANY_CAR);
			db.execSQL("alter table " + CompanyCarDbAdapter.TABLE_NAME_COMPANY_CAR
					+ " add column " + CompanyCarDbAdapter.KEY_LICENSE + " TEXT");
		}
		if (oldVersion <= 18 && newVersion >= 19) {
			Log.i(LOG_TAG, "Set license-plate numbers in " + CompanyCarDbAdapter.TABLE_NAME_COMPANY_CAR);

	    	Cursor cursor = db.query(
	    			CompanyCarDbAdapter.TABLE_NAME_COMPANY_CAR,
	    			new String [] { CompanyCarDbAdapter.KEY_ID },
	    			null,
	    			null,
	    			null, null, null, null);

	    	List<Integer> idlist = new ArrayList<Integer>();
	    	if (cursor.moveToFirst()) {
	    		do {
	    			idlist.add(cursor.getInt(0));
	    		} while (cursor.moveToNext());
	    	}
	    	cursor.close();

	    	for (int cid : idlist) {

	        	ContentValues values = new ContentValues();
	        	values.put(CompanyCarDbAdapter.KEY_LICENSE, Util.randomLicense());

	        	// update row
	        	db.update(CompanyCarDbAdapter.TABLE_NAME_COMPANY_CAR, values,
	        			CompanyCarDbAdapter.KEY_ID + "=?", new String [] { Integer.toString(cid) });

	    	}
		}
	}

}
