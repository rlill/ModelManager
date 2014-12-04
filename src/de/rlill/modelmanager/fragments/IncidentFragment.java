package de.rlill.modelmanager.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.adapter.IncidentListAdapter;
import de.rlill.modelmanager.model.Event;
import de.rlill.modelmanager.model.Today;
import de.rlill.modelmanager.persistance.EventDbAdapter;
import de.rlill.modelmanager.service.DiaryService;
import de.rlill.modelmanager.service.EventService;
import de.rlill.modelmanager.struct.ViewElements;

public class IncidentFragment extends Fragment implements OnItemClickListener {

	private static final String LOG_TAG = "MM*" + IncidentFragment.class.getSimpleName();
    private List<Event> listItems;
    private ArrayAdapter<Event> adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_incident_list, container, false);

        listItems = new ArrayList<Event>();

        adapter = new IncidentListAdapter(
        		container.getContext(),
        		android.R.layout.simple_list_item_1,
        		R.id.listView1,
        		listItems,
        		inflater);

        ListView listView = (ListView)view.findViewById(R.id.listView1);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(this);

        return view;
	}

    @Override
    public void onResume() {
        super.onResume();
	    listItems.clear();
	    listItems.addAll(EventDbAdapter.getAllEvents(null, null));
	    Log.d(LOG_TAG, "Displaying " + listItems.size() + " incidents");
        adapter.notifyDataSetChanged();
    }

	@Override
	public void onItemClick(android.widget.AdapterView<?> parent, View v, int position, long id) {
		ViewElements ve = (ViewElements)v.getTag();
		if (ve != null) {
			int eventId = ve.getContextInt();
			Today t = EventService.newTodayEvent(eventId);
			if (t != null) {
				DiaryService.log(t);
				Toast.makeText(parent.getContext(), t.getDescription(), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(parent.getContext(), "NO MODEL for " + eventId, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
