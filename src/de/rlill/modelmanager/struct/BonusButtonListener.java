package de.rlill.modelmanager.struct;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.Today;
import de.rlill.modelmanager.persistance.DiaryDbAdapter;
import de.rlill.modelmanager.persistance.TodayDbAdapter;
import de.rlill.modelmanager.service.ModelService;

/**
 * Created by robert.lill on 23.06.15.
 */
public class BonusButtonListener implements View.OnClickListener {

	private static final String LOG_TAG = "MM*" + BonusButtonListener.class.getSimpleName();

	private Context context;
	private int modelId;
	private TaskListRefresher refresher;

	public BonusButtonListener(Context context, int modelId, TaskListRefresher refresher) {
		this.context = context;
		this.modelId = modelId;
		this.refresher = refresher;
	}

	@Override
	public void onClick(View view) {


		// suggest standard bonus amounts

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getResources().getString(R.string.labelStandardBonus));

		List<Integer> bonusStepsInt = new ArrayList<Integer>();
		List<Integer> w4bonus = DiaryDbAdapter.getRecentBonusPayments(10);
		// Log.d(LOG_TAG, "recent boni: " + w4bonus.size());
		if (w4bonus.size() > 20) {
			// find 5 most popular amounts
			Collections.sort(w4bonus);
			// StringBuilder sb = new StringBuilder(); for (int b : w4bonus) sb.append(b).append(".- "); Log.d(LOG_TAG, sb.toString());
			int gsize = w4bonus.size() / 5;
			int lowerb = w4bonus.get(0);
			int loweri = 0;
			for (int i = 0; i < 5; i++) {
				int upperi = loweri + gsize;
				if (upperi >= w4bonus.size()) upperi = w4bonus.size() - 1;
				int upperb = w4bonus.get(upperi);
				int b = Util.niceRound((lowerb + upperb) / 2);
				Log.d(LOG_TAG, String.format("BONUS %d-%d: min %d, max %d, avg %d", loweri, upperi, lowerb, upperb, b));
				bonusStepsInt.add(b);
				lowerb = upperb;
				loweri = upperi;
			}
		}
		else {
			// default steps
			bonusStepsInt.add(1000);
			bonusStepsInt.add(2000);
			bonusStepsInt.add(5000);
			bonusStepsInt.add(10000);
		}

		CharSequence [] bonusStepsChar = new CharSequence[bonusStepsInt.size()];
		int i = 0;
		for (int bonus : bonusStepsInt) bonusStepsChar[i++] = Util.amount(bonus);

		builder.setItems(bonusStepsChar, new BonusOptionListener(bonusStepsInt, modelId, refresher));

		builder.show();
	}
}
