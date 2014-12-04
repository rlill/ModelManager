package de.rlill.modelmanager.model;

import de.rlill.modelmanager.struct.ModelStatus;
import de.rlill.modelmanager.struct.Weekday;

public class Model {
	private int id;
	private ModelStatus status;
	private String firstname;
	private String lastname;
	private String image;
	private int teamId;
	private int salary;
	private Weekday payday;
	private int vacation;
	private int vacrest;
	private int carId;
	private int quality_photo;
	private int quality_movie;
	private int erotic;
	private int quality_tlead;
	private int criminal;
	private int ambition;
	private int mood;
	private int health;
	private int hireday;
	private boolean dirty;

	public Model() { dirty = false; }
	public Model(int id) { dirty = false; this.id = id; }

	public int getId() {
		return id;
	}
	public ModelStatus getStatus() {
		return status;
	}
	public void setStatus(ModelStatus status) {
		if (this.status != status) dirty = true;
		this.status = status;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		if ((this.firstname == null && firstname != null) || (this.firstname != null && !this.firstname.equals(firstname))) dirty = true;
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		if ((this.lastname == null && lastname != null) || (this.lastname != null && !this.lastname.equals(lastname))) dirty = true;
		this.lastname = lastname;
	}
	public String getFullname() {
		return firstname + " " + lastname;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	public int getSalary() {
		return salary;
	}
	public void setSalary(int salary) {
		this.salary = salary;
	}
	public Weekday getPayday() {
		return payday;
	}
	public void setPayday(Weekday payday) {
		this.payday = payday;
	}
	public int getVacation() {
		return vacation;
	}
	public void setVacation(int vacation) {
		this.vacation = vacation;
	}
	public int getVacrest() {
		return vacrest;
	}
	public void setVacrest(int vacrest) {
		this.vacrest = vacrest;
	}
	public int getCarId() {
		return carId;
	}
	public void setCarId(int carId) {
		this.carId = carId;
	}
	public int getQuality_photo() {
		return quality_photo;
	}
	public void setQuality_photo(int quality_photo) {
		this.quality_photo = fix100(quality_photo);
	}
	public int getQuality_movie() {
		return quality_movie;
	}
	public void setQuality_movie(int quality_movie) {
		this.quality_movie = fix100(quality_movie);
	}
	public int getErotic() {
		return erotic;
	}
	public void setErotic(int erotic) {
		this.erotic = fix100(erotic);
	}
	public int getQuality_tlead() {
		return quality_tlead;
	}
	public void setQuality_tlead(int quality_tlead) {
		this.quality_tlead = fix100(quality_tlead);
	}
	public int getCriminal() {
		return criminal;
	}
	public void setCriminal(int criminal) {
		this.criminal = fix100(criminal);
	}
	public int getAmbition() {
		return ambition;
	}
	public void setAmbition(int ambition) {
		this.ambition = fix100(ambition);
	}
	public int getMood() {
		return mood;
	}
	public void setMood(int mood) {
		this.mood = fix100(mood);
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = fix100(health);
	}
	public int getHireday() {
		return hireday;
	}
	public void setHireday(int hireday) {
		this.hireday = hireday;
	}
	public boolean isDirty() {
		return dirty;
	}

	private static int fix100(int v) {
		if (v < 0) return 0;
		if (v > 100) return 100;
		return v;
	}

	public boolean equals(Object o2) {
		if (!(o2 instanceof Model)) return false;
		return (((Model)o2).id == id);
	}
	public String toString() {
		return "(" + id + ") " + getFullname();
	}
}
