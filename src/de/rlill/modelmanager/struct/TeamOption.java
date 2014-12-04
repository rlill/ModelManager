package de.rlill.modelmanager.struct;

public class TeamOption {

	private String description;
	private int teamId;

	@SuppressWarnings("unused")
	private TeamOption() { }

	public TeamOption(String desc, int id) {
		description = desc;
		teamId = id;
	}

	public int getTeamId() {
		return teamId;
	}

	public String toString() {
		return description;
	}
}
