package de.rlill.modelmanager.persistance;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.rlill.modelmanager.setup.CarFileSetup;
import de.rlill.modelmanager.setup.CompanyCarSetup;
import de.rlill.modelmanager.setup.DiarySetup;
import de.rlill.modelmanager.setup.EventSetup;
import de.rlill.modelmanager.setup.LoanSetup;
import de.rlill.modelmanager.setup.ModelSetup;
import de.rlill.modelmanager.setup.ModelTrainingSetup;
import de.rlill.modelmanager.setup.MovieModelSetup;
import de.rlill.modelmanager.setup.MovieproductionSetup;
import de.rlill.modelmanager.setup.PropertySetup;
import de.rlill.modelmanager.setup.TeamSetup;
import de.rlill.modelmanager.setup.TodaySetup;
import de.rlill.modelmanager.setup.TransactionSetup;


public class DbAdapter {

	private static final String LOG_TAG = DbAdapter.class.getSimpleName();

	/******************** Table Fields ************/
	public static final String KEY_ID = "_id";

	/******************** Database Name ************/
	public static final String DATABASE_NAME = "DB_modelmanager";

	/******************** Database Version ************/
	public static final int DATABASE_VERSION = 25;

	/******************** Single instance ************/
	private static DataBaseHelper DBHelper = null;

	protected DbAdapter() { }

	/******************* Initialize database *************/
	public static void init(Context context) {
		if (DBHelper == null) {
			DBHelper = new DataBaseHelper(context);
		}
	}

    /********************** Main Database creation INNER class ********************/
	private static class DataBaseHelper extends SQLiteOpenHelper {
		public DataBaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.w(LOG_TAG, "Creating database with version" + DATABASE_VERSION + "...");
			ModelSetup.setup(db);
			EventSetup.setup(db);
			TodaySetup.setup(db);
			DiarySetup.setup(db);
			TransactionSetup.setup(db);
			CompanyCarSetup.setup(db);
			PropertySetup.setup(db);
			ModelTrainingSetup.setup(db);
			TeamSetup.setup(db);
			LoanSetup.setup(db);
			MovieproductionSetup.setup(db);
			MovieModelSetup.setup(db);
			CarFileSetup.setup(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
			ModelSetup.upgrade(db, oldVersion, newVersion);
			EventSetup.upgrade(db, oldVersion, newVersion);
			TodaySetup.upgrade(db, oldVersion, newVersion);
			DiarySetup.upgrade(db, oldVersion, newVersion);
			TransactionSetup.upgrade(db, oldVersion, newVersion);
			CompanyCarSetup.upgrade(db, oldVersion, newVersion);
			PropertySetup.upgrade(db, oldVersion, newVersion);
			ModelTrainingSetup.upgrade(db, oldVersion, newVersion);
			TeamSetup.upgrade(db, oldVersion, newVersion);
			LoanSetup.upgrade(db, oldVersion, newVersion);
			MovieproductionSetup.upgrade(db, oldVersion, newVersion);
			MovieModelSetup.upgrade(db, oldVersion, newVersion);
			CarFileSetup.upgrade(db, oldVersion, newVersion);
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
			ModelSetup.complete(db);
			EventSetup.complete(db);
		}
	}

	/********************** singleton ********************/
	public static synchronized SQLiteDatabase open() throws SQLException {
		return DBHelper.getWritableDatabase();
	}
}
