package de.rlill.modelmanager.struct;

import java.util.Iterator;

import android.database.Cursor;
import de.rlill.modelmanager.model.Diary;
import de.rlill.modelmanager.persistance.DiaryDbAdapter;

public class DiaryIterator implements Iterator<Diary> {

	private Cursor cursor;
	boolean started;

	public DiaryIterator(Cursor cursor) {
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
	public Diary next() {
		return DiaryDbAdapter.readCursorLine(cursor);
	}

	@Override
	public void remove() {
	}

}
