package de.rlill.modelmanager.model;

public class Property {

	private int id;
	private String key;
	private String value;

	public Property() { }
	public Property(int id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getId() {
		return id;
	}

}
