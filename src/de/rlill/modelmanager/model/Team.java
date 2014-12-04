package de.rlill.modelmanager.model;


public class Team {
	private int id;
	private int leader1;
	private int leader2;
	private int bonus;

	public Team() { }
	public Team(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	public int getLeader1() {
		return leader1;
	}
	public void setLeader1(int leader1) {
		this.leader1 = leader1;
	}
	public int getLeader2() {
		return leader2;
	}
	public void setLeader2(int leader2) {
		this.leader2 = leader2;
	}
	public int getBonus() {
		return bonus;
	}
	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
}
