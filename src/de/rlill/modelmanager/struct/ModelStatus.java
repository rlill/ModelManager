package de.rlill.modelmanager.struct;

import android.content.Context;
import de.rlill.modelmanager.R;

public enum ModelStatus {
	FREE(0, "free"),
	HIRED(1, "hired"),
	VACATION(2, "vacation"),
	SICK(3, "sick"),
	TRAINING(4, "training"),
	UNAVAILABLE(5, "unavailable"),
	MOVIEPROD(6, "movieproduction");

	private int index;
	private String name;
	ModelStatus(int i, String n) { index = i; name = n; }

	public static void translate(Context ctx) {
		FREE.name = ctx.getString(R.string.model_state_free);
		HIRED.name = ctx.getString(R.string.model_state_hired);
		VACATION.name = ctx.getString(R.string.model_state_vacation);
		SICK.name = ctx.getString(R.string.model_state_sick);
		TRAINING.name = ctx.getString(R.string.model_state_training);
		UNAVAILABLE.name = ctx.getString(R.string.model_state_unavailable);
		MOVIEPROD.name = ctx.getString(R.string.model_state_movieprod);
	}

	public int getIndex() { return index; }
	public String getName() { return name; }
	public static ModelStatus getInstanceByIndex(int i) {
		for (ModelStatus wd : values()) {
			if (wd.index == i) return wd;
		}
		return null;
	}
}
