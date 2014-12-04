package de.rlill.modelmanager.persistance;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.struct.ModelStatus;
import de.rlill.modelmanager.struct.Weekday;

public class ModelDbAdapter extends DbAdapter {

	private static final String LOG_TAG = ModelDbAdapter.class.getSimpleName();

	public static final String TABLE_NAME_MODEL = "tbl_model";

	public static final String KEY_STATUS = "status";
	public static final String KEY_FIRST_NAME = "firstname";
	public static final String KEY_LAST_NAME = "lastname";
	public static final String KEY_IMAGE = "image";
	public static final String KEY_TEAM_ID = "teamId";
	public static final String KEY_SALARY = "salary";
	public static final String KEY_PAYDAY = "payday";
	public static final String KEY_VACATION = "vacation";
	public static final String KEY_VACREST = "vacrest";
	public static final String KEY_COMPANY_CAR_ID = "company_carId";
	public static final String KEY_QUALITY_PHOTO = "quality_photo";
	public static final String KEY_QUALITY_MOVIE = "quality_movie";
	public static final String KEY_EROTIC = "erotic";
	public static final String KEY_QUALITY_TLEAD = "quality_tlead";
	public static final String KEY_CRIMINAL = "criminal";
	public static final String KEY_AMBITION = "ambition";
	public static final String KEY_MOOD = "mood";
	public static final String KEY_HEALTH = "health";
	public static final String KEY_HIREDAY = "hireday";

	public static final String CREATE_MODEL_TABLE = "create table " + TABLE_NAME_MODEL + "("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_STATUS + " INTEGER, "
			+ KEY_FIRST_NAME + " TEXT, "
			+ KEY_LAST_NAME + " TEXT, "
			+ KEY_IMAGE + " TEXT, "
			+ KEY_TEAM_ID + " INTEGER, "
			+ KEY_SALARY + " NUMERIC, "
			+ KEY_PAYDAY + " INTEGER, "
			+ KEY_VACATION + " INTEGER, "
			+ KEY_VACREST + " INTEGER, "
			+ KEY_COMPANY_CAR_ID + " INTEGER, "
			+ KEY_QUALITY_PHOTO + " NUMERIC, "
			+ KEY_QUALITY_MOVIE + " NUMERIC, "
			+ KEY_EROTIC + " NUMERIC, "
			+ KEY_QUALITY_TLEAD + " NUMERIC, "
			+ KEY_CRIMINAL + " NUMERIC, "
			+ KEY_AMBITION + " NUMERIC, "
			+ KEY_MOOD + " NUMERIC, "
			+ KEY_HEALTH + " NUMERIC, "
			+ KEY_HIREDAY + " INTEGER)";

    /**
     * List all models
     */
    public static SparseArray<Model> listAllModels() {
    	SparseArray<Model> result = new SparseArray<Model>();

    	SQLiteDatabase db = open();
    	Cursor cursor = db.query(
    			TABLE_NAME_MODEL,
    			null,
    			null,
    			null,
    			null, null, null, null);

    	if (cursor.moveToFirst()) {
    		do {
    			Model model = readCursorLine(cursor);
    			result.put(model.getId(), model);
    		} while (cursor.moveToNext());
    	}
    	cursor.close();

    	return result;
    }

    private static Model readCursorLine(Cursor cursor) {
    	Model model = new Model(cursor.getInt(0));
    	model.setStatus(ModelStatus.getInstanceByIndex(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STATUS))));
    	model.setFirstname(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FIRST_NAME)));
    	model.setLastname(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LAST_NAME)));
    	model.setImage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)));
    	model.setTeamId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TEAM_ID)));
		model.setSalary(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SALARY)));
		model.setPayday(Weekday.getInstanceByIndex(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PAYDAY))));
		model.setVacation(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_VACATION)));
		model.setVacrest(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_VACREST)));
		model.setCarId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COMPANY_CAR_ID)));
		model.setQuality_photo(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUALITY_PHOTO)));
		model.setQuality_movie(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUALITY_MOVIE)));
		model.setErotic(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_EROTIC)));
		model.setQuality_tlead(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_QUALITY_TLEAD)));
		model.setCriminal(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CRIMINAL)));
		model.setAmbition(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_AMBITION)));
		model.setMood(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MOOD)));
		model.setHealth(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_HEALTH)));
		model.setHireday(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_HIREDAY)));
    	return model;
    }

    public static int updateModel(Model model) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_STATUS, model.getStatus().getIndex());
    	values.put(KEY_TEAM_ID, model.getTeamId());
    	values.put(KEY_SALARY, model.getSalary());
    	values.put(KEY_PAYDAY, model.getPayday().getIndex());
    	values.put(KEY_VACATION, model.getVacation());
    	values.put(KEY_VACREST, model.getVacrest());
    	values.put(KEY_COMPANY_CAR_ID, model.getCarId());
    	values.put(KEY_QUALITY_PHOTO, model.getQuality_photo());
    	values.put(KEY_QUALITY_MOVIE, model.getQuality_movie());
    	values.put(KEY_QUALITY_TLEAD, model.getQuality_tlead());
    	values.put(KEY_EROTIC, model.getErotic());
    	values.put(KEY_CRIMINAL, model.getCriminal());
    	values.put(KEY_AMBITION, model.getAmbition());
    	values.put(KEY_HEALTH, model.getHealth());
    	values.put(KEY_MOOD, model.getMood());
    	values.put(KEY_HIREDAY, model.getHireday());

    	// update row
    	return db.update(
    			TABLE_NAME_MODEL,
    			values,
    			KEY_ID + " = ?",
    			new String[] { String.valueOf(model.getId()) }
    			);
    }

    public static int getModelCount() {
        String countQuery = "SELECT * FROM " + TABLE_NAME_MODEL;
        final SQLiteDatabase db = open();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

}
