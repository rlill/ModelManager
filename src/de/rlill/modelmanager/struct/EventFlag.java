package de.rlill.modelmanager.struct;


public enum EventFlag {
	PHOTO(1),
	MOVIE(2),
	VACATION(3),
	TRAINING(4),
	RAISE(5),
	BONUS(6),
	CAR_UPDATE(7),
	CAR_BROKEN(8),
	CAR_WRECKED(19),
	CAR_STOLEN(9),
	PAYCHECK(10),
	SICK(11),
	WIN(12),
	WIN_PERSON(13),
	LOSE(14),
	LOSE_PERSON(15),
	PAYFIX_PERSON(16),
	PAYVAR_PERSON(20),
	PAYOPT_PERSON(21),
	QUIT(17),
	NEWDAY(18),
	GROUPWORK(22),
	HIRE(23),
	TRAINING_FIN(24),
	NO_GROUPWORK(25),
	UNAVAILABLE(26),
	AVAILABLE(27),
	CHANGETEAM(28),
	MOVIE_ENTERTAIN(29),
	MOVIE_EROTIC(30),
	MOVIE_PORN(31);

	private int index;
	EventFlag(int i) { index = i; }

	public int getIndex() { return index; }
	public static EventFlag getInstanceByIndex(int i) {
		for (EventFlag ef : values()) {
			if (ef.index == i) return ef;
		}
		return null;
	}
}
