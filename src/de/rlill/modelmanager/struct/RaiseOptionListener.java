package de.rlill.modelmanager.struct;

import android.content.DialogInterface;
import android.util.Log;

import java.util.List;

import de.rlill.modelmanager.service.ModelService;

/**
 * Created by robert.lill on 03.07.2015.
 */
public class RaiseOptionListener implements DialogInterface.OnClickListener {

	private static final String LOG_TAG = "MM*" + RaiseOptionListener.class.getSimpleName();

	private List<Integer> raiseStepsInt;
	private int modelId;
	private TaskListRefresher refresher;

	public RaiseOptionListener(List<Integer> options, int modelId, TaskListRefresher refresher) {
		raiseStepsInt = options;
		this.modelId = modelId;
		this.refresher = refresher;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		int offer = raiseStepsInt.get(which);
		Log.w(LOG_TAG, "Raise to " + offer);

		// grant raise
		ModelService.grantRaise(modelId, offer);

		// update dialog
		if (refresher != null) refresher.refreshTaskList();

		dialog.dismiss();
	}
}
