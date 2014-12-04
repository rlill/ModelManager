package de.rlill.modelmanager.struct;

import de.rlill.modelmanager.R;

public enum EventIcon {
	ARROW(0, R.drawable.arrow),
	CAMERA(1, R.drawable.camera),
	CAR(2, R.drawable.car),
	CONTRACT(3, R.drawable.contract),
	DOOR(4, R.drawable.door),
	EDUCATION(5, R.drawable.education),
	HANDCUFFS(6, R.drawable.handcuffs),
	LIGHTENING(7, R.drawable.lightening),
	MONEY(8, R.drawable.money),
	MOVIECAM(9, R.drawable.moviecam),
	PALMTREE(10, R.drawable.palmtree),
	SICKNESS(11, R.drawable.sickness);

	private int index;
	private int resourceId;
	EventIcon(int i, int id) { index = i; resourceId = id; }

	public int getIndex() { return index; }
	public int getResourceId() { return resourceId; }
	public static EventIcon getInstanceByIndex(int i) {
		for (EventIcon ei : values()) {
			if (ei.index == i) return ei;
		}
		return null;
	}

}
