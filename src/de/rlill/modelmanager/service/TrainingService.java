package de.rlill.modelmanager.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import de.rlill.modelmanager.model.ModelTraining;
import de.rlill.modelmanager.model.Training;
import de.rlill.modelmanager.persistance.ModelTrainingDbAdapter;
import de.rlill.modelmanager.setup.Trainings;
import de.rlill.modelmanager.struct.TrainingStatus;


public class TrainingService {

	private static final String LOG_TAG = TrainingService.class.getSimpleName();

	public static void initSpinner(Context ctx, Spinner sp) {

		// list Trainings
		List<Training> trainingList = new ArrayList<Training>();

		Training t = new Training();
		t.setDescription("-");
		trainingList.add(t);
		trainingList.addAll(Trainings.getTrainings());

		ArrayAdapter<Training> trainingAdapter = new ArrayAdapter<Training>(ctx,
				android.R.layout.simple_spinner_item, trainingList);
		trainingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		sp.setAdapter(trainingAdapter);
	}

	public static ModelTraining getCurrentTraining(int modelId) {
		return ModelTrainingDbAdapter.getCurrentTrainingForModel(modelId);
	}

	public static void setTrainingStatus(int modelTrainingId, TrainingStatus status) {
		Log.i(LOG_TAG, "setting training #" + modelTrainingId + " to " + status);
		ModelTraining training = ModelTrainingDbAdapter.getModelTraining(modelTrainingId);
		training.setTrainingStatus(status);
		ModelTrainingDbAdapter.updateTraining(training);
	}

	public static void cancelTrainingPlans(int modelId) {
		ModelTraining mt = ModelTrainingDbAdapter.getNextPlannedTrainingForModel(modelId);
		while (mt != null) {
			ModelTrainingDbAdapter.deleteTraining(mt.getId());
			mt = ModelTrainingDbAdapter.getNextPlannedTrainingForModel(modelId);
		}
	}
}
