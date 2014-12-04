package de.rlill.modelmanager.service;

import android.util.Log;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.ModelTraining;
import de.rlill.modelmanager.model.Training;
import de.rlill.modelmanager.persistance.ModelTrainingDbAdapter;
import de.rlill.modelmanager.setup.Trainings;
import de.rlill.modelmanager.struct.TrainingStatus;
import de.rlill.modelmanager.struct.Weekday;

public class ModelTrainingService {

	private static final String LOG_TAG = ModelTrainingService.class.getSimpleName();

	public static void bookTraining(int modelId, int trainingId) {

		int trainingStart = DiaryService.today() + 1;
		while (Util.weekday(trainingStart) == Weekday.SATURDAY
				|| Util.weekday(trainingStart) == Weekday.SUNDAY) trainingStart++;

		int trainingEnd = trainingStart;
		Training training = Trainings.getTraining(trainingId);
		for (int i = 1; i < training.getDuration(); i++) {
			trainingEnd++;
			while (Util.weekday(trainingEnd) == Weekday.SATURDAY
					|| Util.weekday(trainingEnd) == Weekday.SUNDAY) trainingEnd++;
		}
		Log.i(LOG_TAG, "booking model #" + modelId + " for " + training.getDescription()
				+ " from #" + trainingStart + " to #" + trainingEnd);

		ModelTraining modelTraining = new ModelTraining();
		modelTraining.setModelId(modelId);
		modelTraining.setTrainingId(trainingId);
		modelTraining.setTrainingStatus(TrainingStatus.PLANNED);
		modelTraining.setStartDay(trainingStart);
		modelTraining.setEndDay(trainingEnd);
		modelTraining.setPrice(training.getPrice());

		ModelTrainingDbAdapter.addTraining(modelTraining);
	}
}
