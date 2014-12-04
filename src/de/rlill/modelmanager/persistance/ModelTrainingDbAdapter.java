package de.rlill.modelmanager.persistance;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.rlill.modelmanager.model.ModelTraining;
import de.rlill.modelmanager.struct.TrainingStatus;

public class ModelTrainingDbAdapter extends DbAdapter {

	private static final String LOG_TAG = ModelTrainingDbAdapter.class.getSimpleName();

	public static final String TABLE_NAME_MODEL_TRAINING = "tbl_model_training";

	public static final String KEY_MODEL_ID = "model_id";
	public static final String KEY_TRAINING_ID = "training_id";
	public static final String KEY_STATUS = "status";
	public static final String KEY_START_DAY = "start_day";
	public static final String KEY_END_DAY = "end_day";
	public static final String KEY_PRICE = "price";

	public static final String CREATE_MODEL_TRAINING_TABLE = "create table " + TABLE_NAME_MODEL_TRAINING + "("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_MODEL_ID + " INTEGER, "
			+ KEY_TRAINING_ID + " INTEGER, "
			+ KEY_STATUS + " INTEGER, "
			+ KEY_START_DAY + " INTEGER, "
			+ KEY_END_DAY + " INTEGER, "
			+ KEY_PRICE + " NUMERIC)";

	// Get single modelTraining
	public static ModelTraining getModelTraining(int modelTrainingId) {
		SQLiteDatabase db = open();
		ModelTraining result = null;

		Cursor cursor = db.query(
				TABLE_NAME_MODEL_TRAINING,
				null,
				KEY_ID + "=?",
				new String[] { String.valueOf(modelTrainingId) },
				null, null, null, "1");

		if (cursor.moveToFirst()) {
			result = readCursorLine(cursor);
		}
		cursor.close();
		return result;
	}

	// Get trainings for model
	public static List<ModelTraining> getTrainingsForModel(int modelId) {
		SQLiteDatabase db = open();
		List<ModelTraining> result = new ArrayList<ModelTraining>();

		Cursor cursor = db.query(
				TABLE_NAME_MODEL_TRAINING,
				null,
				KEY_MODEL_ID + "=?",
				new String[] { String.valueOf(modelId) },
				null, null, KEY_ID + " asc", null);

		if (cursor.moveToFirst()) {
			do {
				result.add(readCursorLine(cursor));
			} while (cursor.moveToNext());
		}
        cursor.close();

		return result;
	}

	// Get trainings for model
	public static ModelTraining getNextPlannedTrainingForModel(int modelId) {
		SQLiteDatabase db = open();
		ModelTraining result = null;

		Cursor cursor = db.query(
				TABLE_NAME_MODEL_TRAINING,
				null,
				KEY_MODEL_ID + "=? AND " + KEY_STATUS + "=?",
				new String[] { String.valueOf(modelId),
						Integer.toString(TrainingStatus.PLANNED.getIndex()) },
				null, null, KEY_START_DAY + " asc", "1");

		if (cursor.moveToFirst()) {
			result = readCursorLine(cursor);
		}
		cursor.close();

		return result;
	}

	// Get trainings scheduled on day
	public static List<ModelTraining> getTrainingsScheduled(int day) {
		SQLiteDatabase db = open();
		List<ModelTraining> result = new ArrayList<ModelTraining>();

		Cursor cursor = db.query(
				TABLE_NAME_MODEL_TRAINING,
				null,
				KEY_START_DAY + "<=? and " + KEY_END_DAY + ">=?",
				new String[] { String.valueOf(day), String.valueOf(day) },
				null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				result.add(readCursorLine(cursor));
			} while (cursor.moveToNext());
		}
		cursor.close();

		return result;
	}

	// Get in-progress training for model
	public static ModelTraining getCurrentTrainingForModel(int modelId) {
		SQLiteDatabase db = open();
		ModelTraining result = null;

		Cursor cursor = db.query(
				TABLE_NAME_MODEL_TRAINING,
				null,
				KEY_MODEL_ID + "=? and " + KEY_STATUS + "=?",
				new String[] { Integer.toString(modelId),
						Integer.toString(TrainingStatus.IN_PROGRESS.getIndex()) },
				null, null, null, "1");

		if (cursor.moveToFirst()) {
			result = readCursorLine(cursor);
		}
		cursor.close();

		return result;
	}

	// Get history for training type
	public static List<ModelTraining> getTrainings(int trainingId) {
		SQLiteDatabase db = open();
		List<ModelTraining> result = new ArrayList<ModelTraining>();

		Cursor cursor = db.query(
				TABLE_NAME_MODEL_TRAINING,
				null,
				KEY_TRAINING_ID + "=?",
				new String[] { String.valueOf(trainingId) },
				null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				result.add(readCursorLine(cursor));
			} while (cursor.moveToNext());
		}
		cursor.close();

		return result;
	}

    private static ModelTraining readCursorLine(Cursor cursor) {
    	ModelTraining training = new ModelTraining(cursor.getInt(0));
		training.setModelId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MODEL_ID)));
		training.setTrainingId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TRAINING_ID)));
		training.setTrainingStatus(TrainingStatus.getInstanceByIndex(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STATUS))));
		training.setStartDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_START_DAY)));
		training.setEndDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_END_DAY)));
		training.setPrice(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRICE)));
    	return training;
    }

    public static void addTraining(ModelTraining training) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_MODEL_ID, training.getModelId());
    	values.put(KEY_TRAINING_ID, training.getTrainingId());
    	values.put(KEY_STATUS, training.getTrainingStatus().getIndex());
    	values.put(KEY_START_DAY, training.getStartDay());
    	values.put(KEY_END_DAY, training.getEndDay());
    	values.put(KEY_PRICE, training.getPrice());

    	// insert row
    	db.insert(TABLE_NAME_MODEL_TRAINING, null, values);
    }

    public static void updateTraining(ModelTraining training) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_STATUS, training.getTrainingStatus().getIndex());
    	values.put(KEY_START_DAY, training.getStartDay());
    	values.put(KEY_END_DAY, training.getEndDay());
    	values.put(KEY_PRICE, training.getPrice());

    	// update row
    	db.update(TABLE_NAME_MODEL_TRAINING, values,
    			KEY_ID + "=?", new String [] { Integer.toString(training.getId()) });
    }

    // for cleanup-function in ModelService
    public static void deleteTraining(int trainingId) {
    	final SQLiteDatabase db = open();
    	db.delete(TABLE_NAME_MODEL_TRAINING,
    			KEY_ID + "=?", new String [] { Integer.toString(trainingId) });
    }

}
