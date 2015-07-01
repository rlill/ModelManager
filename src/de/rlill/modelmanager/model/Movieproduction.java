package de.rlill.modelmanager.model;

import de.rlill.modelmanager.struct.MovieStatus;
import de.rlill.modelmanager.struct.MovieType;

public class Movieproduction {

	private int id;
	private String name;
	private MovieType type;
	private MovieStatus status;
	private int startDay;
	private int endDay;
	private int price;

	public Movieproduction() {}
	public Movieproduction(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MovieType getType() {
		return type;
	}
	public void setType(MovieType type) {
		this.type = type;
	}
	public MovieStatus getStatus() {
		return status;
	}
	public void setStatus(MovieStatus status) {
		this.status = status;
	}
	public int getStartDay() {
		return startDay;
	}
	public void setStartDay(int startDay) {
		this.startDay = startDay;
	}
	public int getEndDay() {
		return endDay;
	}
	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Movieproduction that = (Movieproduction) o;

		if (id != that.id) return false;
		if (startDay != that.startDay) return false;
		if (endDay != that.endDay) return false;
		if (price != that.price) return false;
		if (!name.equals(that.name)) return false;
		if (type != that.type) return false;
		return status == that.status;

	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + name.hashCode();
		result = 31 * result + type.hashCode();
		result = 31 * result + status.hashCode();
		result = 31 * result + startDay;
		result = 31 * result + endDay;
		result = 31 * result + price;
		return result;
	}
}
