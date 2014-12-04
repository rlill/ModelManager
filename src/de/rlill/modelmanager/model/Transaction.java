package de.rlill.modelmanager.model;

public class Transaction {
	private int id;
	private int day;
	private int person1;
	private int person2;
	private int amount;
	private int balance;
	private String description;

	public Transaction() {}
	public Transaction(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getPerson1() {
		return person1;
	}
	public void setPerson1(int person1) {
		this.person1 = person1;
	}
	public int getPerson2() {
		return person2;
	}
	public void setPerson2(int person2) {
		this.person2 = person2;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
