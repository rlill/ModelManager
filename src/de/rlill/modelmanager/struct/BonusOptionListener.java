package de.rlill.modelmanager.struct;

import android.content.DialogInterface;
import android.util.Log;

import java.util.List;

import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.Today;
import de.rlill.modelmanager.persistance.TodayDbAdapter;
import de.rlill.modelmanager.service.ModelService;

/**
 * Created by robert.lill on 23.06.15.
 */
public class BonusOptionListener implements DialogInterface.OnClickListener {

	private static final String LOG_TAG = "MM*" + BonusOptionListener.class.getSimpleName();

	private List<Integer> bonusStepsInt;
	private int modelId;
	private TaskListRefresher refresher;

	public BonusOptionListener(List<Integer> options, int modelId, TaskListRefresher refresher) {
		bonusStepsInt = options;
		this.modelId = modelId;
		this.refresher = refresher;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		int offer = bonusStepsInt.get(which);
		Log.w(LOG_TAG, "Standardbonus " + offer);

		// remove further bonus requests
		Today today = null;
		for (Today td : TodayDbAdapter.getEventsForModel(modelId)) {
			if (td.getEvent().getEclass() == EventClass.REQUEST
					&& td.getEvent().getFlag() == EventFlag.BONUS) {
				today = td;
				break;
			}
		}
		// expectation
		int eb = (today != null) ? today.getAmount1() : 0;

		// update/remove today entry
		ModelService.grantBonus(modelId, offer, eb);

		// remove further bonus requests
		if (today != null) {
			if (offer >= today.getAmount2())
				TodayDbAdapter.removeToday(today.getId());
			else {
				int expectedBonus = ModelService.getExpectedBonus(modelId);
				today.setAmount1(Util.niceRandom(expectedBonus, 2 * expectedBonus));
				today.setAmount2(expectedBonus);
				TodayDbAdapter.updateToday(today);
			}
		}

		// update today listing
		if (refresher != null) refresher.refreshTaskList();

		dialog.dismiss();
	}
}
