package de.rlill.modelmanager.fragments;

import java.util.Collections;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.Diary;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.service.DiaryService;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.struct.DiaryIterator;
import de.rlill.modelmanager.struct.Operation;
import de.rlill.modelmanager.struct.Weekday;

public class OperationchartFragment extends Fragment {

	private static final String LOG_TAG = OperationchartFragment.class.getSimpleName();

	private static final int NAME_COLUMN_WIDTH = 140;
	private static final int ACTION_COLUMN_WIDTH = 25;

	private View fragmentView;
	private View parentView;
	private int actionColumns;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		parentView = container.getRootView();

		ViewTreeObserver observer = parentView.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				parentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				createChart();
			}
		});

		// Inflate the layout for this fragment
		fragmentView = inflater.inflate(R.layout.fragment_operationchart, container, false);

		return fragmentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		createChart();
	}

	private void createChart() {

		actionColumns = (parentView.getWidth() - NAME_COLUMN_WIDTH) / ACTION_COLUMN_WIDTH;
//		Log.i(LOG_TAG, "width = " + parentView.getWidth() + " -> " + actionColumns + " columns");
		if (actionColumns < 1) return;
		if (actionColumns > DiaryService.today()) actionColumns = DiaryService.today();


		LinearLayout ll = (LinearLayout) fragmentView.findViewById(R.id.chart_headers);
		ll.removeAllViews();

		TextView tv = new TextView(parentView.getContext());
		tv.setLayoutParams(new LayoutParams(NAME_COLUMN_WIDTH, LayoutParams.WRAP_CONTENT));
		tv.setPadding(4,  4,  4,  4);
		tv.setTextSize(16);
		tv.setText(R.string.labelName);
		ll.addView(tv);

		int startDay = DiaryService.today() - actionColumns + 1;
		for (int day = startDay; day <= DiaryService.today(); day++) {
			tv = new TextView(parentView.getContext());
			tv.setLayoutParams(new LayoutParams(ACTION_COLUMN_WIDTH, LayoutParams.WRAP_CONTENT));
			tv.setPadding(4,  4,  4,  4);
			tv.setGravity(Gravity.CENTER);
			tv.setTextSize(12);
			tv.setText(Integer.toString(day % 100));
			if (Util.weekday(day) == Weekday.SATURDAY || Util.weekday(day) == Weekday.SUNDAY)
				tv.setTextColor(Color.RED);
			tv.setBackgroundResource(R.drawable.thin_frame);
			ll.addView(tv);
		}

		List<Model> listItems = ModelService.getHiredModels();

		DiaryIterator diaryIterator = DiaryService.listOperationsEventsSince(startDay);

		SparseArray<SparseArray<Operation>> operationChart = new SparseArray<SparseArray<Operation>>();

		while (diaryIterator.hasNext()) {
			Diary diary = diaryIterator.next();

			SparseArray<Operation> modelOperation = operationChart.get(diary.getModelId());
			if (modelOperation == null) {
				modelOperation = new SparseArray<Operation>();
				operationChart.put(diary.getModelId(), modelOperation);
			}

			switch (diary.getEventClass()) {
			case BOOKING:
				modelOperation.put(diary.getDay(), Operation.BOOKING);
				break;
			case NOTIFICATION:
				switch (diary.getEventFlag()) {
				case SICK:
					modelOperation.put(diary.getDay(), Operation.SICK);
					break;
				default:
				}
				break;
			case ACCEPT:
				switch (diary.getEventFlag()) {
				case TRAINING:
					modelOperation.put(diary.getDay(), Operation.TRAINING);
					break;
				case VACATION:
					modelOperation.put(diary.getDay(), Operation.VACATION);
					break;
				case HIRE:
					modelOperation.put(diary.getDay(), Operation.HIRED);
					break;
				case QUIT:
					modelOperation.put(diary.getDay(), Operation.QUIT);
					break;
				default:
				}
				break;
			case MOVIE_CAST:
				modelOperation.put(diary.getDay(), Operation.MOVIE);
			default:
			}
		}



		ll = (LinearLayout) fragmentView.findViewById(R.id.chart_body);
		ll.removeAllViews();

		Collections.sort(listItems, new ModelService.ModelNameComparator());
		for (Model model : listItems) {
			LinearLayout llrow = new LinearLayout(parentView.getContext());
			llrow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			llrow.setOrientation(LinearLayout.HORIZONTAL);

			tv = new TextView(parentView.getContext());
			tv.setLayoutParams(new LayoutParams(NAME_COLUMN_WIDTH, LayoutParams.WRAP_CONTENT));
			tv.setPadding(4,  4,  4,  4);
			tv.setTextSize(16);
			tv.setText(model.getFullname());
			llrow.addView(tv);

			SparseArray<Operation> modelOperation = operationChart.get(model.getId());

			boolean hired = false;
			for (int day = startDay; day <= DiaryService.today(); day++) {

				tv = new TextView(parentView.getContext());
				tv.setLayoutParams(new LayoutParams(ACTION_COLUMN_WIDTH, LayoutParams.WRAP_CONTENT));
				tv.setPadding(4,  4,  4,  4);
				tv.setGravity(Gravity.CENTER);
				tv.setTextSize(16);

				Operation op = Operation.FREE;
				if (modelOperation != null) op = modelOperation.get(day, Operation.FREE);
				if (op == Operation.HIRED || op == Operation.BOOKING) hired = true;
				if (op == Operation.QUIT) hired = false;
				if (hired && op == Operation.FREE) op = Operation.HIRED;

				tv.setText(op.getDisplay());
				tv.setBackgroundColor(op.getColor());

				if (!hired) tv.setBackgroundResource(R.drawable.thin_frame);

				llrow.addView(tv);
			}
			ll.addView(llrow);
		}
	}

}
