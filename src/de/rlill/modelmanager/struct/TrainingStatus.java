package de.rlill.modelmanager.struct;

import android.content.Context;
import de.rlill.modelmanager.R;


public enum TrainingStatus {
	PLANNED(1, "planned"),
	IN_PROGRESS(2, "in progress"),
	FINISHED(3, "finished"),
	FAILED(4, "failed");

	private int index;
	private String name;
	TrainingStatus(int i, String n) { index = i; name = n; }

	public static void translate(Context ctx) {
		PLANNED.name = ctx.getString(R.string.training_state_planned);
		IN_PROGRESS.name = ctx.getString(R.string.training_state_in_progress);
		FINISHED.name = ctx.getString(R.string.training_state_finished);
		FAILED.name = ctx.getString(R.string.training_state_failed);
	}

	public int getIndex() { return index; }
	public String getName() { return name; }
	public static TrainingStatus getInstanceByIndex(int i) {
		for (TrainingStatus ts : values()) {
			if (ts.index == i) return ts;
		}
		return null;
	}
}
