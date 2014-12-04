package de.rlill.modelmanager.struct;

import java.util.Iterator;

import android.database.Cursor;
import de.rlill.modelmanager.model.Transaction;
import de.rlill.modelmanager.persistance.TransactionDbAdapter;

public class TransactionIterator implements Iterator<Transaction> {

	private Cursor cursor;
	boolean started;

	public TransactionIterator(Cursor cursor) {
		this.cursor = cursor;
		started = false;
	}

	@Override
	public boolean hasNext() {
		if (started) {
			if (!cursor.moveToNext()) {
				cursor.close();
				return false;
			}
			return true;
		}
		else {
			if (!cursor.moveToFirst()) {
				cursor.close();
				return false;
			}
			started = true;
		}
		return true;
	}

	@Override
	public Transaction next() {
		return TransactionDbAdapter.readCursorLine(cursor);
	}

	@Override
	public void remove() {
	}

	public void close() {
		cursor.close();
	}
}
