package de.rlill.modelmanager.model;

import de.rlill.modelmanager.setup.CarTypes;
import de.rlill.modelmanager.struct.CarStatus;


public class CompanyCar {
	private int id;
	private int carTypeId;
	private int price;
	private int buyday;
	private CarStatus status;
	private CarType carType;
	private String licensePlate;

	public CompanyCar() {
	}
	public CompanyCar(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	public int getCarTypeId() {
		return carTypeId;
	}
	public void setCarTypeId(int carTypeId) {
		this.carTypeId = carTypeId;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getBuyday() {
		return buyday;
	}
	public void setBuyday(int buyday) {
		this.buyday = buyday;
	}
	public CarStatus getStatus() {
		return status;
	}
	public void setStatus(CarStatus status) {
		this.status = status;
	}
	public CarType getCarType() {
		if (carType == null) carType = CarTypes.getCarType(carTypeId);
		if (carType == null) throw new IllegalArgumentException("Invalid carTypeId " + carTypeId);
		return carType;
	}
	public String getLicensePlate() {
		return licensePlate;
	}
	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}
}
