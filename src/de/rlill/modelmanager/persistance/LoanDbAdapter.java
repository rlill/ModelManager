package de.rlill.modelmanager.persistance;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.rlill.modelmanager.model.Loan;

public class LoanDbAdapter extends DbAdapter {

	private static final String LOG_TAG = LoanDbAdapter.class.getSimpleName();

	public static final String TABLE_NAME_LOAN = "tbl_loan";

	public static final String KEY_MODEL_ID = "model_id";
	public static final String KEY_START_DAY = "start_day";
	public static final String KEY_FINISH_DAY = "finish_day";
	public static final String KEY_AMOUNT = "amount";
	public static final String KEY_INTEREST = "interest";

	public static final String CREATE_LOAN_TABLE = "create table " + TABLE_NAME_LOAN + "("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_MODEL_ID + " INTEGER, "
			+ KEY_START_DAY + " INTEGER, "
			+ KEY_FINISH_DAY + " INTEGER, "
			+ KEY_AMOUNT + " INTEGER, "
			+ KEY_INTEREST + " NUMERIC)";

	/**
	 * Get list of loans
	 */
	public static List<Loan> getOpenLoans() {
		SQLiteDatabase db = open();
		List<Loan> result = new ArrayList<Loan>();

		Cursor cursor = db.query(
				TABLE_NAME_LOAN,
				null,
				KEY_AMOUNT + ">0",
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

	/**
	 * Get loan for model
	 */
	public static Loan getLoan(int modelId) {
		SQLiteDatabase db = open();
		Loan result = null;

		Cursor cursor = db.query(
				TABLE_NAME_LOAN,
				null,
				KEY_MODEL_ID + "=?",
				new String[] { String.valueOf(modelId) },
				null, null, null, null);

		if (cursor.moveToFirst()) {
			result = readCursorLine(cursor);
		}
		cursor.close();

		return result;
	}

    private static Loan readCursorLine(Cursor cursor) {
    	Loan loan = new Loan(cursor.getInt(0));
		loan.setModelId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MODEL_ID)));
		loan.setStartDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_START_DAY)));
		loan.setFinishDay(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FINISH_DAY)));
		loan.setAmount(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_AMOUNT)));
		loan.setInterest(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_INTEREST)));
    	return loan;
    }

    public static void addLoan(Loan loan) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_MODEL_ID, loan.getModelId());
    	values.put(KEY_START_DAY, loan.getStartDay());
    	values.put(KEY_FINISH_DAY, loan.getFinishDay());
    	values.put(KEY_AMOUNT, loan.getAmount());
    	values.put(KEY_INTEREST, loan.getInterest());

    	// insert row
    	db.insert(TABLE_NAME_LOAN, null, values);
    }

    public static void updateLoan(Loan loan) {
    	final SQLiteDatabase db = open();

    	ContentValues values = new ContentValues();
    	values.put(KEY_START_DAY, loan.getFinishDay());
    	values.put(KEY_FINISH_DAY, loan.getFinishDay());
    	values.put(KEY_AMOUNT, loan.getAmount());
    	values.put(KEY_INTEREST, loan.getInterest());

    	// update row
    	db.update(TABLE_NAME_LOAN, values,
    			KEY_ID + "=?", new String [] { Integer.toString(loan.getId()) });
    }

}
