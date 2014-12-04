package de.rlill.modelmanager.struct;

import android.content.Context;
import de.rlill.modelmanager.R;


public enum CarClass {
	SMALL(1, "small car"),
	MEDIUM(2, "medium sized car"),
	LARGE(3, "large car"),
	SPORTS(4, "sports car"),
	LUXURY(5, "luxury car");

	private int index;
	private String name;
	CarClass(int i, String n) { index = i; name = n; }

	public int getIndex() { return index; }
	public String getName() { return name; }
	public static CarClass getInstanceByIndex(int i) {
		for (CarClass cc : values()) {
			if (cc.index == i) return cc;
		}
		return null;
	}


	public static void translate(Context ctx) {
		SMALL.name = ctx.getString(R.string.typename_small_car);
		MEDIUM.name = ctx.getString(R.string.typename_medium_car);
		LARGE.name = ctx.getString(R.string.typename_large_car);
		SPORTS.name = ctx.getString(R.string.typename_sports_car);
		LUXURY.name = ctx.getString(R.string.typename_luxury_car);
	}
}
