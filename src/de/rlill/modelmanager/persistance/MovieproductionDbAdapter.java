package de.rlill.modelmanager.persistance;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.rlill.modelmanager.model.Movieproduction;
import de.rlill.modelmanager.struct.MovieStatus;
import de.rlill.modelmanager.struct.MovieType;

public class MovieproductionDbAdapter extends DbAdapter {

	private static final String LOG_TAG = "MM*" + MovieproductionDbAdapter.class.getSimpleName();

	public static final String TABLE_NAME_MOVIEPRODUCTION = "tbl_movieproduction";

	public static final String KEY_NAME = "name";
	public static final String KEY_TYPE = "type";
	public static final String KEY_STATUS = "status";
	public static final String KEY_START_DAY = "start_day";
	public static final String KEY_END_DAY = "end_day";
	public static final String KEY_PRICE = "price";

	public static final String CREATE_MOVIEPRODUCTION_TABLE = "create table " + TABLE_NAME_MOVIEPRODUCTION + "("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_NAME + " TEXT, "
			+ KEY_TYPE + " INTEGER, "
			+ KEY_STATUS + " INTEGER, "
			+ KEY_START_DAY + " INTEGER, "
			+ KEY_END_DAY + " INTEGER, "
			+ KEY_PRICE + " INTEGER)";

	// list all movie productions
	public static List<Movieproduction> listMovieproductions() {
		SQLiteDatabase db = open();
		List<Movieproduction> result = new ArrayList<Movieproduction>();

		Cursor cursor = db.query(
				TABLE_NAME_MOVIEPRODUCTION,
				null,
				null,
				null,
				null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				result.add(readCursorLine(cursor));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	private static Movieproduction readCursorLine(Cursor cursor) {
		Movieproduction mp = new Movieproduction(cursor.getInt(0));
		mp.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
		mp.setType(MovieType.getInstanceByIndex(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TYPE))));
		mp.setStatus(MovieStatus.getInstanceByIndex(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STATUS))));
		mp.setStartDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_START_DAY)));
		mp.setEndDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_END_DAY)));
		mp.setPrice(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRICE)));
		return mp;
	}

	public static void addMovieproduction(Movieproduction mp) {
		final SQLiteDatabase db = open();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, mp.getName());
		values.put(KEY_TYPE, mp.getType().getIndex());
		values.put(KEY_STATUS, mp.getStatus().getIndex());
		values.put(KEY_START_DAY, mp.getStartDay());
		values.put(KEY_END_DAY, mp.getEndDay());
		values.put(KEY_PRICE, mp.getPrice());

		// insert row
		db.insert(TABLE_NAME_MOVIEPRODUCTION, null, values);
	}

	public static void updateMovieproduction(Movieproduction mp) {
		final SQLiteDatabase db = open();

		ContentValues values = new ContentValues();
		values.put(KEY_STATUS, mp.getStatus().getIndex());
		values.put(KEY_START_DAY, mp.getStartDay());
		values.put(KEY_END_DAY, mp.getEndDay());
		values.put(KEY_PRICE, mp.getPrice());

		// update row
		db.update(TABLE_NAME_MOVIEPRODUCTION, values, KEY_ID + "=?", new String[] { Integer.toString(mp.getId()) });
	}

}
