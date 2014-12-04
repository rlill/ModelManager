package de.rlill.modelmanager.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.Event;
import de.rlill.modelmanager.struct.ViewElements;

public class IncidentListAdapter extends ArrayAdapter<Event> {

	private static final String LOG_TAG = "MM*" + IncidentListAdapter.class.getSimpleName();
    private LayoutInflater mInflater;

	public IncidentListAdapter(Context context, int resource, int textViewResourceId,
			List<Event> objects, LayoutInflater inflater) {
		super(context, resource, textViewResourceId, objects);
        mInflater = inflater;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//data from your adapter
		Event event = getItem(position);
		IncidentListViewElements viewElements = null;

		// reuse already constructed row views...
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_incident_item, null);
			viewElements = new IncidentListViewElements(convertView);
			convertView.setTag(viewElements);
		}
		else {
			viewElements = (IncidentListViewElements) convertView.getTag();
		}

		viewElements.setContextInt(event.getId());
		viewElements.setDescription(event.getDescription());
		viewElements.setDetails(
				"#" + event.getSystemId() + "(" + event.getId() + ")v" + event.getVersion()
				+ ", min: " + Util.amount(event.getAmountMin())
				+ ", max: " + Util.amount(event.getAmountMax()) + ((event.getMaxpercent() > 0) ? "(" + event.getMaxpercent() + "%)" : "")
				+ ", " + event.getEclass() + "/" + event.getFlag()
				+ ", chance " + event.getChance() + "%"
			);

		return convertView;
	}

    private class IncidentListViewElements extends ViewElements {

    	public IncidentListViewElements(View view) {
        	super(view);
    	}

    	public void setDescription(String description) {
    		findTextView(R.id.textView1).setText(description);
    	}

    	public void setDetails(String details) {
    		findTextView(R.id.textViewDetails).setText(details);
    	}
    }

}
