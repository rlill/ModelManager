package de.rlill.modelmanager.persistance;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.rlill.modelmanager.model.CarFile;
import de.rlill.modelmanager.struct.CarAction;

public class CarFileDbAdapter extends DbAdapter {

	private static final String LOG_TAG = CarFileDbAdapter.class.getSimpleName();

	public static final String TABLE_NAME_CAR_FILE = "tbl_car_file";

	public static final String KEY_DAY = "day";
	public static final String KEY_CAR_ID = "car_id";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_PRICE = "price";
	public static final String KEY_ACTION = "action";

	public static final String CREATE_CAR_FILE_TABLE = "create table " + TABLE_NAME_CAR_FILE + "("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_CAR_ID + " INTEGER, "
			+ KEY_DAY + " INTEGER, "
			+ KEY_DESCRIPTION + " TEXT, "
			+ KEY_PRICE + " NUMERIC, "
			+ KEY_ACTION + " INTEGER)";

    public static List<CarFile> listEntries(int companyCarId) {
    	List<CarFile> result = new ArrayList<CarFile>();

    	SQLiteDatabase db = open();
    	Cursor cursor = db.query(
    			TABLE_NAME_CAR_FILE,
    			null,
    			KEY_CAR_ID + "=?",
    			new String[] { Integer.toString(companyCarId) },
    			null, null, KEY_ID + " asc", null);

    	if (cursor.moveToFirst()) {
    		do {
    			result.add(readCursorLine(cursor));
    		} while (cursor.moveToNext());
    	}
    	cursor.close();

    	return result;
    }

    public static CarFile readCursorLine(Cursor cursor) {
    	CarFile file = new CarFile(cursor.getInt(0));

    	file.setCarId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CAR_ID)));
    	file.setDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DAY)));
    	file.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
    	file.setPrice(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRICE)));
    	file.setAction(CarAction.getInstanceByIndex(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ACTION))));
    	return file;
    }

    public static int addEntry(CarFile file) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_CAR_ID, file.getCarId());
    	values.put(KEY_DAY, file.getDay());
    	values.put(KEY_DESCRIPTION, file.getDescription());
    	values.put(KEY_PRICE, file.getPrice());
    	values.put(KEY_ACTION, (file.getAction() != null) ? file.getAction().getIndex() : 0);

    	// update row
    	return (int)db.insert(TABLE_NAME_CAR_FILE, null, values);
    }

}
