package de.rlill.modelmanager.struct;


public enum EventClass {
	BOOKING(1),
	REQUEST(2),
	NOTIFICATION(3),
	EXTRA_IN(4),
	EXTRA_OUT(5),
	EXTRA_LOSS(9),
	APPLICATION(6),
	ACCEPT(7),
	GAME(8),
	BOOKREJECT(10),
	MOVIE_START(11),
	MOVIE_PROGRESS(12),
	MOVIE_FINISH(13),
	MOVIE_CAST(14);

	private int index;
	EventClass(int i) { index = i; }

	public int getIndex() { return index; }
	public static EventClass getInstanceByIndex(int i) {
		for (EventClass ec : values()) {
			if (ec.index == i) return ec;
		}
		return null;
	}
}
