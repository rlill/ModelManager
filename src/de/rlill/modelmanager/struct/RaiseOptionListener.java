package de.rlill.modelmanager.struct;

import android.content.DialogInterface;
import android.util.Log;

import java.util.List;

import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.Today;
import de.rlill.modelmanager.persistance.TodayDbAdapter;
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

		// remove raise requests
		Today today = null;
		for (Today td : TodayDbAdapter.getEventsForModel(modelId)) {
			if (td.getEvent().getEclass() == EventClass.REQUEST
					&& td.getEvent().getFlag() == EventFlag.RAISE) {
				if (offer >= today.getAmount2())
					TodayDbAdapter.removeToday(today.getId());
				break;
			}
		}

		// update dialog
		if (refresher != null) refresher.refreshTaskList();

		dialog.dismiss();
	}
}
