package de.rlill.modelmanager.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.struct.OpChartElement;
import de.rlill.modelmanager.struct.Operation;
import de.rlill.modelmanager.struct.ViewElements;
import de.rlill.modelmanager.struct.Weekday;

public class OpChartGridAdapter extends ArrayAdapter<OpChartElement> {

	private static final String LOG_TAG = OpChartGridAdapter.class.getSimpleName();
	private LayoutInflater mInflater;

	public OpChartGridAdapter(Context context, int resource, int textViewResourceId,
			List<OpChartElement> objects, LayoutInflater inflater) {
		super(context, resource, textViewResourceId, objects);
		mInflater = inflater;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//data from your adapter
		OpChartElement element = getItem(position);
		OpChartViewElements viewElements = null;

		// reuse already constructed row views...
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_operationchart_item, null);
			viewElements = new OpChartViewElements(convertView);
			convertView.setTag(viewElements);
		}
		else {
			viewElements = (OpChartViewElements) convertView.getTag();
		}

		viewElements.setDay(element.day);
		viewElements.setOperation(element.operation);

		return convertView;
	}

	private class OpChartViewElements extends ViewElements {

		public OpChartViewElements(View view) {
			super(view);
		}

		public void setDay(int day) {
			TextView tv = findTextView(R.id.textViewDay);
			tv.setText(Integer.toString(day % 100));
			if (Util.weekday(day) == Weekday.SATURDAY || Util.weekday(day) == Weekday.SUNDAY)
				tv.setTextColor(Color.RED);
		}

		public void setOperation(Operation op) {
			TextView tv = findTextView(R.id.textViewOperation);
			tv.setText(op.getDisplay());
			tv.setBackgroundColor(op.getColor());
		}
	}
}
