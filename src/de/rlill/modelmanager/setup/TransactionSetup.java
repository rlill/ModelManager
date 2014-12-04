package de.rlill.modelmanager.setup;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.rlill.modelmanager.persistance.TransactionDbAdapter;

public class TransactionSetup {

	private static final String LOG_TAG = TransactionSetup.class.getSimpleName();

	public static void setup(SQLiteDatabase db) {
		// create table
		try {
			Log.i(LOG_TAG, "Create table " + TransactionDbAdapter.TABLE_NAME_TRANSACTION);
			db.execSQL(TransactionDbAdapter.CREATE_TRANSACTION_TABLE);
		} catch (Exception exception) {
			Log.e(LOG_TAG, "Exception onCreate()", exception);
		}
		Log.i(LOG_TAG, "Table " + TransactionDbAdapter.TABLE_NAME_TRANSACTION + " created.");

		// insert
		ContentValues values = new ContentValues();
		values.put(TransactionDbAdapter.KEY_DAY, 0);
		values.put(TransactionDbAdapter.KEY_AMOUNT, 1000000);
		values.put(TransactionDbAdapter.KEY_BALANCE, 1000000);
		values.put(TransactionDbAdapter.KEY_PERSON1, 0);
		values.put(TransactionDbAdapter.KEY_PERSON2, -1);
		values.put(TransactionDbAdapter.KEY_DESCRIPTION, "START");
		db.insert(TransactionDbAdapter.TABLE_NAME_TRANSACTION, null, values);
	}

	public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(LOG_TAG, "Upgrade table " + TransactionDbAdapter.TABLE_NAME_TRANSACTION + " from version " + oldVersion + " to version " + newVersion);

		if (oldVersion <= 19 && newVersion >= 20) {
		}
	}

}
