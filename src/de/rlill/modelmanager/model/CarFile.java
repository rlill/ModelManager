package de.rlill.modelmanager.model;

import de.rlill.modelmanager.struct.CarAction;


public class CarFile {
	private int id;
	private int carId;
	private int day;
	private String description;
	private int price;
	private CarAction action;

	public CarFile() { }
	public CarFile(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getCarId() {
		return carId;
	}
	public void setCarId(int carId) {
		this.carId = carId;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public CarAction getAction() {
		return action;
	}
	public void setAction(CarAction action) {
		this.action = action;
	}
}
