package de.rlill.modelmanager.struct;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import de.rlill.modelmanager.R;


public enum CarStatus {
	NEW(1, "new"),
	IN_USE(2, "in use"),
	USED_ALITTLE(3, "a little used"),
	USED_ALOT(4, "used a lot"),
	DEFECT(5, "defect"),
	STOLEN(6, "stolen"),
	WRECKED(7, "wrecked"),
	SOLD(8, "sold");

	private int index;
	private String name;
	CarStatus(int i, String n) { index = i; name = n; }

	public int getIndex() { return index; }
	public String getName() { return name; }
	public static CarStatus getInstanceByIndex(int i) {
		for (CarStatus cs : values()) {
			if (cs.index == i) return cs;
		}
		return null;
	}

	public static List<SpinnerItem> valuesAsSpinnerItemList() {
		List<SpinnerItem> result = new ArrayList<SpinnerItem>();
		for (CarStatus cs : values()) {
			result.add(new SpinnerItem(cs.index, cs.name));
		}
		return result;
	}

	public static void translate(Context ctx) {
		NEW.name = ctx.getString(R.string.car_status_new);
		IN_USE.name = ctx.getString(R.string.car_status_in_use);
		USED_ALITTLE.name = ctx.getString(R.string.car_status_used_alittle);
		USED_ALOT.name = ctx.getString(R.string.car_status_used_alot);
		DEFECT.name = ctx.getString(R.string.car_status_defect);
		STOLEN.name = ctx.getString(R.string.car_status_stolen);
		WRECKED.name = ctx.getString(R.string.car_status_wrecked);
		SOLD.name = ctx.getString(R.string.car_status_sold);
	}
}
