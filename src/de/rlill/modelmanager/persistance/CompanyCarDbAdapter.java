package de.rlill.modelmanager.persistance;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;
import de.rlill.modelmanager.model.CompanyCar;
import de.rlill.modelmanager.struct.CarStatus;

public class CompanyCarDbAdapter extends DbAdapter {

	private static final String LOG_TAG = CompanyCarDbAdapter.class.getSimpleName();

	public static final String TABLE_NAME_COMPANY_CAR = "tbl_company_car";

	public static final String KEY_CARTYPE = "cartype";
	public static final String KEY_PRICE = "price";
	public static final String KEY_BUYDAY = "buyday";
	public static final String KEY_STATUS = "status";
	public static final String KEY_LICENSE = "license";

	public static final String CREATE_COMPANY_CAR_TABLE = "create table " + TABLE_NAME_COMPANY_CAR + "("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_CARTYPE + " INTEGER, "
			+ KEY_PRICE + " NUMERIC, "
			+ KEY_BUYDAY + " INTEGER, "
			+ KEY_STATUS + " INTEGER, "
			+ KEY_LICENSE + " TEXT)";

    /**
     * Get companyCar
     */
    public static CompanyCar getCompanyCar(int id) {
    	CompanyCar cc = null;
        SQLiteDatabase db = open();
		Cursor cursor = db.query(
				TABLE_NAME_COMPANY_CAR,
				null,
				KEY_ID + "=?",
				new String [] { Integer.toString(id) },
				null, null, null, "1");

        if (cursor.moveToFirst()) {
            do {
            	cc = readCursorLine(cursor);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return cc;
    }

    /**
     * List all companyCars
     */
    public static SparseArray<CompanyCar> listAllCompanyCars() {
    	SparseArray<CompanyCar> result = new SparseArray<CompanyCar>();
    	SQLiteDatabase db = open();
    	Cursor cursor = db.query(
    			TABLE_NAME_COMPANY_CAR,
    			null,
    			null,
    			null,
    			null, null, null, null);

    	if (cursor.moveToFirst()) {
    		do {
    			CompanyCar cc = readCursorLine(cursor);
    			result.put(cc.getId(), cc);
    		} while (cursor.moveToNext());
    	}
    	cursor.close();

    	return result;
    }

    private static CompanyCar readCursorLine(Cursor cursor) {
    	CompanyCar companyCar = new CompanyCar(cursor.getInt(0));

    	companyCar.setCarTypeId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CARTYPE)));
    	companyCar.setPrice(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRICE)));
    	companyCar.setBuyday(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_BUYDAY)));
    	companyCar.setStatus(CarStatus.getInstanceByIndex(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STATUS))));
    	companyCar.setLicensePlate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LICENSE)));
    	return companyCar;
    }

    public static int addCompanyCar(CompanyCar companyCar) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_STATUS, companyCar.getStatus().getIndex());
    	values.put(KEY_CARTYPE, companyCar.getCarTypeId());
    	values.put(KEY_PRICE, companyCar.getPrice());
    	values.put(KEY_BUYDAY, companyCar.getBuyday());
    	values.put(KEY_STATUS, companyCar.getStatus().getIndex());
    	values.put(KEY_LICENSE, companyCar.getLicensePlate());

    	// update row
    	return (int) db.insert(TABLE_NAME_COMPANY_CAR, null, values);
    }

    public static long updateCompanyCar(CompanyCar companyCar) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_STATUS, companyCar.getStatus().getIndex());
    	values.put(KEY_PRICE, companyCar.getPrice());

    	// update row
    	return db.update(TABLE_NAME_COMPANY_CAR, values,
    			KEY_ID + "=?", new String [] { Integer.toString(companyCar.getId()) });
    }

}
