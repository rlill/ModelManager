package de.rlill.modelmanager.setup;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseArray;
import de.rlill.modelmanager.model.Training;
import de.rlill.modelmanager.service.PropertiesService;

public class Trainings {

	private static List<Training> trainingList = null;
	private static SparseArray<Training> trainingMap = null;

	public static List<Training> getTrainings() {
		if (trainingList == null) init();
		return trainingList;
	}

	public static Training getTraining(int id) {
		if (trainingMap == null) init();
		return trainingMap.get(id, null);
	}

	private static void init() {
		trainingList = new ArrayList<Training>();
		trainingMap = new SparseArray<Training>();
		double worthincreaseFactor = PropertiesService.getWorthincrease();

		Training training = new Training(1);
		training.setDescription("Profitraining für Fotomodels");
		training.setDuration(4);
		training.setPrice((int)(worthincreaseFactor * 24000));
		training.setInc_qphoto(40);
		training.setInc_mood(20);
		trainingList.add(training);
		trainingMap.put(training.getId(), training);

		training = new Training(2);
		training.setDescription("Profitraining für Filmmodels");
		training.setDuration(4);
		training.setPrice((int)(worthincreaseFactor * 32000));
		training.setInc_qmovie(40);
		training.setInc_mood(20);
		trainingList.add(training);
		trainingMap.put(training.getId(), training);

		training = new Training(3);
		training.setDescription("Erotiktraining für Models");
		training.setDuration(3);
		training.setPrice((int)(worthincreaseFactor * 18000));
		training.setInc_erotic(40);
		training.setInc_mood(20);
		training.setInc_criminal(40);
		trainingList.add(training);
		trainingMap.put(training.getId(), training);

		training = new Training(3);
		training.setDescription("Shopping-Urlaub");
		training.setDuration(3);
		training.setPrice((int)(worthincreaseFactor * 20000));
		training.setInc_erotic(40);
		training.setInc_mood(20);
		trainingList.add(training);
		trainingMap.put(training.getId(), training);

		training = new Training(4);
		training.setDescription("Teamleiterkurs");
		training.setDuration(5);
		training.setPrice((int)(worthincreaseFactor * 28000));
		training.setInc_ambition(20);
		training.setInc_qtlead(40);
		trainingList.add(training);
		trainingMap.put(training.getId(), training);

		training = new Training(5);
		training.setDescription("Wellness-Kurzurlaub");
		training.setDuration(3);
		training.setPrice((int)(worthincreaseFactor * 25000));
		training.setInc_ambition(10);
		training.setInc_mood(20);
		training.setInc_health(30);
		training.setInc_criminal(10);
		trainingList.add(training);
		trainingMap.put(training.getId(), training);

		training = new Training(6);
		training.setDescription("Wellness-Urlaub");
		training.setDuration(8);
		training.setPrice((int)(worthincreaseFactor * 32000));
		training.setInc_ambition(20);
		training.setInc_mood(40);
		training.setInc_health(50);
		trainingList.add(training);
		trainingMap.put(training.getId(), training);
/*
		training = new Training(7);
		training.setDescription("Powertraining");
		training.setDuration(1);
		training.setPrice(100000);
		training.setInc_qphoto(10);
		training.setInc_qmovie(10);
		training.setInc_qtlead(10);
		training.setInc_erotic(20);
		training.setInc_ambition(30);
		training.setInc_mood(40);
		training.setInc_health(50);
		training.setInc_criminal(60);
		trainingList.add(training);
		trainingMap.put(training.getId(), training);
*/
	}
}
