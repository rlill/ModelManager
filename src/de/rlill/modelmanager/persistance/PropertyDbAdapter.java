package de.rlill.modelmanager.persistance;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.rlill.modelmanager.model.Property;

public class PropertyDbAdapter extends DbAdapter {

	private static final String LOG_TAG = "MM*" + PropertyDbAdapter.class.getSimpleName();

	public static final String TABLE_NAME_PROPERTIES = "tbl_properties";

	public static final String KEY_KEY = "key";
	public static final String KEY_VALUE = "value";

	public static final String CREATE_PROPERTIES_TABLE = "create table " + TABLE_NAME_PROPERTIES + "("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_KEY + " TEXT, "
			+ KEY_VALUE + " TEXT)";

    /**
     * List events of one day
     */
    public static List<Property> getAllProperties() {
        List<Property> result = new ArrayList<Property>();
        SQLiteDatabase db = open();
		Cursor cursor = db.query(
				TABLE_NAME_PROPERTIES,
				null,
				null,
				null,
				null, null, KEY_ID + " desc", null);

        if (cursor.moveToFirst()) {
            do {
            	Property properties = readCursorLine(cursor);
                result.add(properties);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    private static Property readCursorLine(Cursor cursor) {
    	Property properties = new Property(cursor.getInt(0));

    	properties.setKey(cursor.getString(cursor.getColumnIndexOrThrow(KEY_KEY)));
    	properties.setValue(cursor.getString(cursor.getColumnIndexOrThrow(KEY_VALUE)));
    	return properties;
    }

    public static void setProperty(Property properties) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_KEY, properties.getKey());
    	values.put(KEY_VALUE, properties.getValue());

    	// update row
    	int u = db.update(TABLE_NAME_PROPERTIES, values, KEY_KEY + "=?", new String [] { properties.getKey() });

	    // if update failed, insert instead
    	if (u < 1) db.insert(TABLE_NAME_PROPERTIES, null, values);
    }

}
