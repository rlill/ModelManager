package de.rlill.modelmanager.struct;

import android.content.Context;
import de.rlill.modelmanager.R;


public enum MovieStatus {
	PLANNED(1, "planned"),
	IN_PROGRESS(2, "in progress"),
	RENTAL(3, "let for rent"),
	SOLD(4, "sold"),
	CANCELED(5, "canceled");

	private int index;
	private String name;
	MovieStatus(int i, String n) { index = i; name = n; }

	public int getIndex() { return index; }
	public String getName() { return name; }

	public static void translate(Context ctx) {
		PLANNED.name = ctx.getString(R.string.movie_state_planned);
		IN_PROGRESS.name = ctx.getString(R.string.movie_state_in_progress);
		RENTAL.name = ctx.getString(R.string.movie_state_rental);
		SOLD.name = ctx.getString(R.string.movie_state_sold);
		CANCELED.name = ctx.getString(R.string.movie_state_canceled);
	}

	public static MovieStatus getInstanceByIndex(int i) {
		for (MovieStatus ms : values()) {
			if (ms.index == i) return ms;
		}
		return null;
	}
}
