package de.rlill.modelmanager.model;

import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;


public class Diary {
	private int id;
	private EventClass eventClass;
	private EventFlag eventFlag;
	private int day;
	private int modelId;
	private int amount;
	private String description;

	public Diary() {
	}
	public Diary(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	public EventClass getEventClass() {
		return eventClass;
	}
	public void setEventClass(EventClass eventClass) {
		this.eventClass = eventClass;
	}
	public EventFlag getEventFlag() {
		return eventFlag;
	}
	public void setEventFlag(EventFlag eventFlag) {
		this.eventFlag = eventFlag;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
