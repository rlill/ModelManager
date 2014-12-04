package de.rlill.modelmanager.struct;

public enum Operation {

	FREE("", 0x00000000),
	BOOKING("B", 0xFF0BEAD4),
	TRAINING("T", 0xFFFFF95B),
	VACATION("V", 0xFF6279FC),
	SICK("S", 0xFF65FC62),
	HIRED("", 0xFF00BFFF),
	MOVIE("M", 0xFFAAAA00),
	QUIT("", 0x00000000);

	private String display;
	private int color;

	Operation(String s, int c) {
		display = s;
		color = c;
	}

	public String getDisplay() {
		return display;
	}

	public int getColor() {
		return color;
	}
}
