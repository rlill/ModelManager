package de.rlill.modelmanager.struct;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.persistance.DiaryDbAdapter;
import de.rlill.modelmanager.service.ModelService;

/**
 * Created by robert.lill on 23.06.15.
 */
public class RaiseButtonListener implements View.OnClickListener {

	private static final String LOG_TAG = "MM*" + RaiseButtonListener.class.getSimpleName();

	private Context context;
	private int modelId;
	private TaskListRefresher refresher;

	public RaiseButtonListener(Context context, int modelId, TaskListRefresher refresher) {
		this.context = context;
		this.modelId = modelId;
		this.refresher = refresher;
	}

	@Override
	public void onClick(View view) {

		// suggest standard raise amounts

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getResources().getString(R.string.labelStandardRaise));

		Set<Integer> salaryStepsInt = new TreeSet<Integer>();
		Set<Integer> salaries = new TreeSet<Integer>();
		Model model = ModelService.getModelById(modelId);

		for (Model m : ModelService.getHiredModels()) {
			if (m.getSalary() > model.getSalary()) salaries.add(m.getSalary());
		}

		int c = 0;
		for (Integer s : salaries) {
			salaryStepsInt.add(s);
			if (++c > 5) break;
		}

		// if < 5 options: add 5%, 10%, 15% ...
		int perc = 5;
		while (salaryStepsInt.size() < 5) {
			int sugg = Util.niceRound((int)((double)model.getSalary() * (1.0 + (double)perc / 100)));
//			Log.d(LOG_TAG, "SUGGEST: " + perc + "% => " + sugg);
			perc += 5;
			if (salaryStepsInt.contains(sugg)) continue;
			salaryStepsInt.add(sugg);
		}

		CharSequence [] salaryStepsChar = new CharSequence[salaryStepsInt.size()];
		int i = 0;
		for (int bonus : salaryStepsInt) salaryStepsChar[i++] = Util.amount(bonus);

		builder.setItems(salaryStepsChar, new RaiseOptionListener(new ArrayList<Integer>(salaryStepsInt), modelId, refresher));

		builder.show();

	}
}
