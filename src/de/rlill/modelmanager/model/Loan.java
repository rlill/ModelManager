package de.rlill.modelmanager.model;

public class Loan {

	private int id;
	private int modelId;
	private int startDay;
	private int finishDay;
	private int amount;
	private double interest;

	public Loan() {}
	public Loan(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
	public int getStartDay() {
		return startDay;
	}
	public void setStartDay(int startDay) {
		this.startDay = startDay;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public double getInterest() {
		return interest;
	}
	public void setInterest(double interest) {
		this.interest = interest;
	}
	public int getFinishDay() {
		return finishDay;
	}
	public void setFinishDay(int finishDay) {
		this.finishDay = finishDay;
	}
}
