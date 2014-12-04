package de.rlill.modelmanager.struct;

import android.content.Context;
import de.rlill.modelmanager.R;


public enum Weekday {
	SUNDAY(0, "Sunday"),
	MONDAY(1, "Monday"),
	TUESDAY(2, "Tuesday"),
	WEDNESDAY(3, "Wednesday"),
	THURSDAY(4, "Thursday"),
	FRIDAY(5, "Friday"),
	SATURDAY(6, "Saturday");

	private int index;
	private String name;
	Weekday(int i, String n) { index = i; name = n; }

	public static void translate(Context ctx) {
		SUNDAY.name = ctx.getString(R.string.display_sunday);
		MONDAY.name = ctx.getString(R.string.display_monday);
		TUESDAY.name = ctx.getString(R.string.display_tuesday);
		WEDNESDAY.name = ctx.getString(R.string.display_wednesday);
		THURSDAY.name = ctx.getString(R.string.display_thursday);
		FRIDAY.name = ctx.getString(R.string.display_friday);
		SATURDAY.name = ctx.getString(R.string.display_saturday);
	}

	public int getIndex() { return index; }
	public String getName() { return name; }
	public static Weekday getInstanceByIndex(int i) {
		for (Weekday wd : values()) {
			if (wd.index == i) return wd;
		}
		return null;
	}
}
