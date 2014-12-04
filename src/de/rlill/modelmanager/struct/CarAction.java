package de.rlill.modelmanager.struct;


public enum CarAction {
	BUY(1),
	ASSIGN(2),
	TAKEAWAY(3),
	REPAIR(4),
	SELL(5),
	TRASH(6),
	ACCIDENT(7),
	BREAKDOWN(8),
	THEFT(9),
	DEASSIGN(10);

	private int index;
	CarAction(int i) { index = i; }

	public int getIndex() { return index; }
	public static CarAction getInstanceByIndex(int i) {
		for (CarAction cc : values()) {
			if (cc.index == i) return cc;
		}
		return null;
	}
}
