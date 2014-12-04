package de.rlill.modelmanager.model;

import de.rlill.modelmanager.persistance.EventDbAdapter;
import de.rlill.modelmanager.service.MessageService;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.struct.TodayStatus;

public class Today {
	private int id;
	private TodayStatus status;
	private int eventId;
	private int modelId;
	private int amount1;
	private int amount2;

	private Model model = null;
	private Event event = null;

	public Today() { }
	public Today(int id) { this.id = id; }

	public int getId() {
		return id;
	}
	public TodayStatus getStatus() {
		return status;
	}
	public void setStatus(TodayStatus status) {
		this.status = status;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		if (eventId != this.eventId) event = null;
		this.eventId = eventId;
	}
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		if (modelId != this.modelId) model = null;
		this.modelId = modelId;
	}
	public int getAmount1() {
		return amount1;
	}
	public void setAmount1(int amount1) {
		this.amount1 = amount1;
	}
	public int getAmount2() {
		return amount2;
	}
	public void setAmount2(int amount2) {
		this.amount2 = amount2;
	}

	public Model getModel() {
		if (model != null) return model;
		model = ModelService.getModelById(modelId);
		return model;
	}
	public Event getEvent() {
		if (event != null) return event;
		event = EventDbAdapter.getEvent(eventId);
		return event;
	}
	public String getDescription() {
		return MessageService.fillOutLogmessage(
				getEvent().getDescription(),
				getModel(),
				amount1,
				amount2);
	}
	public String getNoteAcct() {
		String desc = getEvent().getNoteAcct();
		if (desc == null) desc = getEvent().getDescription();
		return MessageService.fillOutLogmessage(
				desc,
				getModel(),
				amount1,
				amount2);
	}
	public String getNoteFile() {
		String desc = getEvent().getNoteFile();
		if (desc == null) desc = getEvent().getDescription();
		return MessageService.fillOutLogmessage(
				desc,
				getModel(),
				amount1,
				amount2);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getEvent().getEclass()).append('/').append(getEvent().getFlag())
			.append(" (").append(getId()).append(") ")
			.append(getDescription());
		return sb.toString();
	}
}
