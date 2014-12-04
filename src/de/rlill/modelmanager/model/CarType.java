package de.rlill.modelmanager.model;

import de.rlill.modelmanager.struct.CarClass;


public class CarType {
	private int id;
	private String description;
	private int price;
	private int imageId;
	private CarClass cclass;
	private int cylinders;
	private int capacity;
	private int powerPs;
	private int maxspeedKmh;

	public CarType(int id, CarClass cc, int price, int impageId, String description) {
		this.id = id;
		this.cclass = cc;
		this.price = price;
		this.imageId = impageId;
		this.description = description;
	}

	public void setEngine(int cylinders, int capacity, int powerPs, int maxspeedKmh) {
		this.cylinders = cylinders;
		this.capacity = capacity;
		this.powerPs = powerPs;
		this.maxspeedKmh = maxspeedKmh;
	}

	public int getId() {
		return id;
	}
	public String getDescription() {
		return description;
	}
	public int getPrice() {
		return price;
	}
	public CarClass getCclass() {
		return cclass;
	}
	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	public int getCylinders() {
		return cylinders;
	}
	public int getCapacity() {
		return capacity;
	}
	public int getpowerPs() {
		return powerPs;
	}
	public int getMaxspeedKmh() {
		return maxspeedKmh;
	}
}

