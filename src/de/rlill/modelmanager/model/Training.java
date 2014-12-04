package de.rlill.modelmanager.model;

import de.rlill.modelmanager.Util;

public class Training {
	private int id;
	private String description;
	private int duration;
	private int price;
	private int inc_qphoto;
	private int inc_qmovie;
	private int inc_erotic;
	private int inc_qtlead;
	private int inc_ambition;
	private int inc_mood;
	private int inc_health;
	private int inc_criminal;

	public Training() {}
	public Training(int id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getInc_qphoto() {
		return inc_qphoto;
	}
	public void setInc_qphoto(int inc_qphoto) {
		this.inc_qphoto = inc_qphoto;
	}
	public int getInc_qmovie() {
		return inc_qmovie;
	}
	public void setInc_qmovie(int inc_qmovie) {
		this.inc_qmovie = inc_qmovie;
	}
	public int getInc_erotic() {
		return inc_erotic;
	}
	public void setInc_erotic(int inc_erotic) {
		this.inc_erotic = inc_erotic;
	}
	public int getInc_qtlead() {
		return inc_qtlead;
	}
	public void setInc_qtlead(int inc_qtlead) {
		this.inc_qtlead = inc_qtlead;
	}
	public int getInc_ambition() {
		return inc_ambition;
	}
	public void setInc_ambition(int inc_ambition) {
		this.inc_ambition = inc_ambition;
	}
	public int getInc_mood() {
		return inc_mood;
	}
	public void setInc_mood(int inc_mood) {
		this.inc_mood = inc_mood;
	}
	public int getInc_health() {
		return inc_health;
	}
	public void setInc_health(int inc_health) {
		this.inc_health = inc_health;
	}
	public int getInc_criminal() {
		return inc_criminal;
	}
	public void setInc_criminal(int inc_criminal) {
		this.inc_criminal = inc_criminal;
	}
	public int getId() {
		return id;
	}
	public String toString() {
		String result = description;
		if (price > 0) result += " (" + Util.amount(price) + ")";
		return result;
	}
}
