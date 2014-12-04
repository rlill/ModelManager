package de.rlill.modelmanager.model;

import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.EventIcon;


public class Event {
	private int id;
	private int systemId;
	private int version;
	private boolean obsolete;
	private EventClass eclass;
	private EventFlag flag;
	private EventIcon icon;
	private String description;
	private String noteFile;
	private String noteAcct;
	private int amountMin;
	private int amountMax;
	private int maxpercent;
	private int factor;
	private int chance;
	private int usecount;

	public Event() { }
	public Event(int id) { this.id = id; }
	public Event(int sid, int v) { this.systemId = sid; this.version = v; }

	public int getId() {
		return id;
	}
	public int getSystemId() {
		return systemId;
	}
	public void setSystemId(int systemId) {
		this.systemId = systemId;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public boolean isObsolete() {
		return obsolete;
	}
	public void setObsolete(boolean obsolete) {
		this.obsolete = obsolete;
	}
	public EventClass getEclass() {
		return eclass;
	}
	public void setEclass(EventClass eclass) {
		this.eclass = eclass;
	}
	public EventFlag getFlag() {
		return flag;
	}
	public void setFlag(EventFlag flag) {
		this.flag = flag;
	}
	public EventIcon getIcon() {
		return icon;
	}
	public void setIcon(EventIcon icon) {
		this.icon = icon;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getNoteFile() {
		return noteFile;
	}
	public void setNoteFile(String noteFile) {
		this.noteFile = noteFile;
	}
	public String getNoteAcct() {
		return noteAcct;
	}
	public void setNoteAcct(String noteAcct) {
		this.noteAcct = noteAcct;
	}
	public int getAmountMin() {
		return amountMin;
	}
	public void setAmountMin(int amountMin) {
		this.amountMin = amountMin;
	}
	public int getAmountMax() {
		return amountMax;
	}
	public void setAmountMax(int amountMax) {
		this.amountMax = amountMax;
	}
	public int getMaxpercent() {
		return maxpercent;
	}
	public void setMaxpercent(int maxpercent) {
		this.maxpercent = maxpercent;
	}
	public int getFactor() {
		return factor;
	}
	public void setFactor(int factor) {
		this.factor = factor;
	}
	public int getChance() {
		return chance;
	}
	public void setChance(int chance) {
		this.chance = chance;
	}
	public int getUsecount() {
		return usecount;
	}
	public void setUsecount(int usecount) {
		this.usecount = usecount;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Event ").append(id).append("[");
		result.append("SI ").append(systemId);
		result.append(",V ").append(version);
		if (obsolete) result.append(",(obsolete)");
		result.append(",").append(eclass).append('/').append(flag);
		result.append(",'").append(description).append("']");
		return result.toString();
	}
}
