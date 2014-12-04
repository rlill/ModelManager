package de.rlill.modelmanager.struct;


public enum MovieStatus {
	PLANNED(1),
	IN_PROGRESS(2),
	RENTAL(3),
	SOLD(4),
	CANCELED(5);

	private int index;
	MovieStatus(int i) { index = i; }

	public int getIndex() { return index; }
	public static MovieStatus getInstanceByIndex(int i) {
		for (MovieStatus ms : values()) {
			if (ms.index == i) return ms;
		}
		return null;
	}
}
