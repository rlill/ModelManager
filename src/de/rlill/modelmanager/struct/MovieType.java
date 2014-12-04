package de.rlill.modelmanager.struct;

import android.content.Context;
import de.rlill.modelmanager.R;


public enum MovieType {
	ENTERTAINMENT(1, "entertainment"),
	EROTIC(2, "erotic"),
	PORN(3, "porn");

	// TODO: DOKUSOAP

	private int index;
	private String name;
	MovieType(int i, String n) { index = i; name = n; }

	public static void translate(Context ctx) {
		ENTERTAINMENT.name = ctx.getString(R.string.movie_type_entertainment);
		EROTIC.name = ctx.getString(R.string.movie_type_erotic);
		PORN.name = ctx.getString(R.string.movie_type_porn);
	}

	public int getIndex() { return index; }
	public String getName() { return name; }
	public static MovieType getInstanceByIndex(int i) {
		for (MovieType mt : values()) {
			if (mt.index == i) return mt;
		}
		return null;
	}
}
