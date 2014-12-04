package de.rlill.modelmanager.struct;

public class MovieTypeOption {

	private MovieType type;

	@SuppressWarnings("unused")
	private MovieTypeOption() { }

	public MovieTypeOption(MovieType t) {
		type = t;
	}

	public MovieType getType() {
		return type;
	}

	public String toString() {
		return type.getName();
	}
}
