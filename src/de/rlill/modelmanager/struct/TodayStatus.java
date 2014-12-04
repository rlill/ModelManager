package de.rlill.modelmanager.struct;


public enum TodayStatus {
	NEW(0),
	PROGRESS(1),
	DONE(2);

	private int index;
	TodayStatus(int i) { index = i; }

	public int getIndex() { return index; }
	public static TodayStatus getInstanceByIndex(int i) {
		for (TodayStatus ts : values()) {
			if (ts.index == i) return ts;
		}
		return null;
	}
}
