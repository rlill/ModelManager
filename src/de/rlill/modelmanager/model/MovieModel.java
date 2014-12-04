package de.rlill.modelmanager.model;

public class MovieModel implements Comparable<MovieModel> {

	private int id;
	private int movieId;
	private int modelId;
	private int day;
	private int price;

	public MovieModel() {}
	public MovieModel(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public int getMovieId() {
		return movieId;
	}
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	@Override
	public int compareTo(MovieModel other) {
		if (other.modelId > modelId) return -1;
		if (other.modelId < modelId) return 1;
		return 0;
	}
}
