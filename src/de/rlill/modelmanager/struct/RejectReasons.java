package de.rlill.modelmanager.struct;

public class RejectReasons {
	public RejectReasons() {
		carMissing = false;
		bonusMissing = 0;
		vacationMissing = 0;
		badMood = false;
	}
	public boolean carMissing;
	public int bonusMissing;
	public int vacationMissing;
	public boolean badMood;

	public boolean willReject() {
		return carMissing || (bonusMissing > 0) || (vacationMissing > 0) || badMood;
	}
}
