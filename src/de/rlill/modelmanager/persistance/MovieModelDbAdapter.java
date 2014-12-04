package de.rlill.modelmanager.persistance;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import de.rlill.modelmanager.model.MovieModel;

public class MovieModelDbAdapter extends DbAdapter {

	private static final String LOG_TAG = "MM*" + MovieModelDbAdapter.class.getSimpleName();

	public static final String TABLE_NAME_MOVIE_MODEL = "tbl_movie_model";

	public static final String KEY_MOVIE_ID = "movie_id";
	public static final String KEY_MODEL_ID = "model_id";
	public static final String KEY_DAY = "day";
	public static final String KEY_PRICE = "price";

	public static final String CREATE_MOVIE_MODEL_TABLE = "create table " + TABLE_NAME_MOVIE_MODEL + "("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_MOVIE_ID + " INTEGER, "
			+ KEY_MODEL_ID + " INTEGER, "
			+ KEY_DAY + " INTEGER, "
			+ KEY_PRICE + " INTEGER)";

	// Get assignments for movie on a certain day or a certain model
	public static List<MovieModel> getMovieModels(int movieId, int modelId, int day) {
    	List<MovieModel> result = new ArrayList<MovieModel>();

    	List<String> matchKeys = new ArrayList<String>();
    	List<String> matchValues = new ArrayList<String>();
    	if (movieId > 0) {
    		matchKeys.add(KEY_MOVIE_ID + "=?");
    		matchValues.add(Integer.toString(movieId));
    	}
    	if (modelId > 0) {
    		matchKeys.add(KEY_MODEL_ID + "=?");
    		matchValues.add(Integer.toString(modelId));
    	}
    	if (day > 0) {
    		matchKeys.add(KEY_DAY + "=?");
    		matchValues.add(Integer.toString(day));
    	}

    	SQLiteDatabase db = open();
    	Cursor cursor = db.query(
    			TABLE_NAME_MOVIE_MODEL,
    			null,
    			TextUtils.join(" AND ", matchKeys),
    			matchValues.toArray(new String[matchValues.size()]),
    			null, null, KEY_ID + " asc", null);

    	if (cursor.moveToFirst()) {
    		do {
    			MovieModel mm = readCursorLine(cursor);
    			result.add(mm);
    		} while (cursor.moveToNext());
    	}
    	cursor.close();
		return result;
	}

    private static MovieModel readCursorLine(Cursor cursor) {
    	MovieModel mm = new MovieModel(cursor.getInt(0));
		mm.setMovieId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MOVIE_ID)));
		mm.setModelId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MODEL_ID)));
		mm.setDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DAY)));
		mm.setPrice(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRICE)));
    	return mm;
    }

    public static void addMovieModel(MovieModel mm) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_MOVIE_ID, mm.getMovieId());
    	values.put(KEY_MODEL_ID, mm.getModelId());
    	values.put(KEY_DAY, mm.getDay());
    	values.put(KEY_PRICE, mm.getPrice());

    	// insert row
    	db.insert(TABLE_NAME_MOVIE_MODEL, null, values);
    }

    public static void updateMovieModel(MovieModel mm) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_PRICE, mm.getPrice());

    	// update row
    	db.update(TABLE_NAME_MOVIE_MODEL, values,
    			KEY_ID + "=?", new String [] { Integer.toString(mm.getId()) });
    }

    public static void deleteMovieModel(int mmId) {
    	final SQLiteDatabase db = open();
    	db.delete(TABLE_NAME_MOVIE_MODEL,  KEY_ID + "=?", new String [] { Integer.toString(mmId) });
    }

}
