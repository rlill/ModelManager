package de.rlill.modelmanager.persistance;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import de.rlill.modelmanager.model.Transaction;
import de.rlill.modelmanager.service.DiaryService;
import de.rlill.modelmanager.struct.TransactionIterator;

public class TransactionDbAdapter extends DbAdapter {

	private static final String LOG_TAG = TransactionDbAdapter.class.getSimpleName();

	public static final String TABLE_NAME_TRANSACTION = "tbl_transaction";

	public static final String KEY_DAY = "day";
	public static final String KEY_PERSON1 = "person1";
	public static final String KEY_PERSON2 = "person2";
	public static final String KEY_AMOUNT = "amount";
	public static final String KEY_BALANCE = "balance";
	public static final String KEY_DESCRIPTION = "description";

	public static final String CREATE_TRANSACTION_TABLE = "create table " + TABLE_NAME_TRANSACTION + "("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_DAY + " INTEGER, "
			+ KEY_PERSON1 + " INTEGER, "
			+ KEY_PERSON2 + " INTEGER, "
			+ KEY_AMOUNT + " NUMERIC, "
			+ KEY_BALANCE + " NUMERIC, "
			+ KEY_DESCRIPTION + " TEXT)";

	// Read actual account balance
	public static Transaction getMostCurrentTransaction(int personId) {
		SQLiteDatabase db = open();
		Cursor cursor = db.query(
				TABLE_NAME_TRANSACTION,
				null,
				KEY_PERSON1 + "=?",
				new String[] { String.valueOf(personId) },
				null, null, KEY_ID + " desc", "1");

		Transaction tr = null;
		if (cursor.moveToFirst()) tr = readCursorLine(cursor);
        cursor.close();

		return tr;
	}

    public static Transaction readCursorLine(Cursor cursor) {
    	Transaction tr = new Transaction(cursor.getInt(0));
    	tr.setDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DAY)));
    	tr.setPerson1(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PERSON1)));
    	tr.setPerson2(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PERSON2)));
    	tr.setAmount(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_AMOUNT)));
    	tr.setBalance(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_BALANCE)));
    	tr.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
    	return tr;
    }

    public static void addTransaction(Transaction tr) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_DAY, tr.getDay());
    	values.put(KEY_PERSON1, tr.getPerson1());
    	values.put(KEY_PERSON2, tr.getPerson2());
    	values.put(KEY_AMOUNT, tr.getAmount());
    	values.put(KEY_BALANCE, tr.getBalance());
    	values.put(KEY_DESCRIPTION, tr.getDescription());

    	// update row
    	db.insert(TABLE_NAME_TRANSACTION, null, values);
    }

	// Read 100 most recent transactions
	public static List<Transaction> getTransactions(int personId) {
		List<Transaction> result = new ArrayList<Transaction>();
		SQLiteDatabase db = open();
		Cursor cursor = db.query(
				TABLE_NAME_TRANSACTION,
				null,
				KEY_PERSON1 + "=?",
				new String[] { String.valueOf(personId) },
				null, null, KEY_ID + " desc", "100");


    	if (cursor.moveToFirst()) {
    		do {
    			Transaction tr = readCursorLine(cursor);
    			result.add(tr);
    		} while (cursor.moveToNext());
    	}
    	cursor.close();

		return result;
	}

	// Read all transactions for person on day
	public static List<Transaction> getTransactions(int personId, int day) {
		List<Transaction> result = new ArrayList<Transaction>();
		SQLiteDatabase db = open();
		Cursor cursor = db.query(
				TABLE_NAME_TRANSACTION,
				null,
				KEY_PERSON1 + "=? AND " + KEY_DAY + "=?",
				new String[] { String.valueOf(personId), String.valueOf(day) },
				null, null, KEY_ID + " desc", null);

		if (cursor.moveToFirst()) {
			do {
				Transaction tr = readCursorLine(cursor);
				result.add(tr);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return result;
	}

    /**
     * Query transactions for person since startDay.
     */
    public static TransactionIterator listTransactionsSince(int startDay, int personId) {
    	SQLiteDatabase db = open();
    	Cursor cursor = db.query(
    			TABLE_NAME_TRANSACTION,
    			null,
    			KEY_DAY + ">=? AND " + KEY_PERSON1 + "=?",
    			new String[] { String.valueOf(startDay), String.valueOf(personId) },
    			null, null, KEY_ID + " desc", null);

    	return new TransactionIterator(cursor);
    }

	public static List<Transaction> getTransactions(int fromId, int toId, int fromDay, int toDay) {
		List<Transaction> result = new ArrayList<Transaction>();

		List<String> matchKeys = new ArrayList<String>();
		List<String> matchValues = new ArrayList<String>();

		if (fromId > -2) {
			matchKeys.add(KEY_PERSON1 + "=?");
			matchValues.add(Integer.toString(fromId));
		}

		if (toId > -2) {
			matchKeys.add(KEY_PERSON2 + "=?");
			matchValues.add(Integer.toString(toId));
		}

		if (fromDay > 0) {
			matchKeys.add(KEY_DAY + ">=?");
			matchValues.add(Integer.toString(fromDay));
		}

		if (toDay > 0) {
			matchKeys.add(KEY_DAY + "<=?");
			matchValues.add(Integer.toString(toDay));
		}

		SQLiteDatabase db = open();
		Cursor cursor = db.query(
				TABLE_NAME_TRANSACTION,
				null,
				TextUtils.join(" and ", matchKeys),
				matchValues.toArray(new String[matchValues.size()]),
				null, null, KEY_ID + " desc", null);


    	if (cursor.moveToFirst()) {
    		do {
    			Transaction tr = readCursorLine(cursor);
    			result.add(tr);
    		} while (cursor.moveToNext());
    	}
    	cursor.close();

		return result;
	}

    public static int allPaymentsToModelLastDays(int modelId, int days) {
        SQLiteDatabase db = open();
    	int w4day = DiaryService.today() - days;
        Cursor cursor = db.query(
        		TABLE_NAME_TRANSACTION,
        		null,
        		KEY_PERSON1 + "=? AND " + KEY_DAY + ">=?",
        		new String[] { Integer.toString(modelId), Integer.toString(w4day) },
                null, null, KEY_DAY + " desc", null);

        int paysum = 0;
        if (cursor.moveToFirst()) {
            do {
            	Transaction t = readCursorLine(cursor);
            	if (t.getAmount() > 0) paysum += t.getAmount();
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.i(LOG_TAG, "Model #" + modelId + " has received " + paysum + ".- during the last " + days + " days.");

    	return paysum;
    }

}
