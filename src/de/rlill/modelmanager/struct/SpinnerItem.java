package de.rlill.modelmanager.struct;

public class SpinnerItem {

	private int id;
	private String name;

	@SuppressWarnings("unused")
	private SpinnerItem() { }

	public SpinnerItem(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SpinnerItem)) return false;
		SpinnerItem so = (SpinnerItem)o;
		return name != null && name.equals(so.name);
	}
}
