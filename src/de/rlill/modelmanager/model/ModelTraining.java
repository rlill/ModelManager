package de.rlill.modelmanager.model;

import de.rlill.modelmanager.setup.Trainings;
import de.rlill.modelmanager.struct.TrainingStatus;

public class ModelTraining {

	private int id;
	private int modelId;
	private int trainingId;
	private TrainingStatus trainingStatus;
	private int startDay;
	private int endDay;
	private int price;
	private Training training;

	public ModelTraining() {}
	public ModelTraining(int id) {
		this.id = id;
	}
	public int getModelId() {
		return modelId;
	}
	public void setModelId(int modelId) {
		this.modelId = modelId;
	}
	public int getTrainingId() {
		return trainingId;
	}
	public void setTrainingId(int trainingId) {
		this.trainingId = trainingId;
	}
	public Training getTraining() {
		if (training == null) training = Trainings.getTraining(trainingId);
		return training;
	}
	public TrainingStatus getTrainingStatus() {
		return trainingStatus;
	}
	public void setTrainingStatus(TrainingStatus trainingStatus) {
		this.trainingStatus = trainingStatus;
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
	public int getId() {
		return id;
	}

	public String toString() {
		return getTraining().getDescription() + " from #" + startDay + " to #" + endDay + " for Model " + modelId;
	}
}
