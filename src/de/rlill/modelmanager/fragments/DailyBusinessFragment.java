package de.rlill.modelmanager.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.adapter.DailyBusinessListAdapter;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.model.Today;
import de.rlill.modelmanager.persistance.TodayDbAdapter;
import de.rlill.modelmanager.service.EventService;
import de.rlill.modelmanager.service.TransactionService;
import de.rlill.modelmanager.struct.TaskListRefresher;

public class DailyBusinessFragment extends Fragment implements OnItemClickListener, TaskListRefresher {

	private static final String LOG_TAG = DailyBusinessFragment.class.getSimpleName();

	private View fragmentView;
	private List<Today> listItems;
	private ArrayAdapter<Today> adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		fragmentView = inflater.inflate(R.layout.fragment_dailytasks, container, false);

        listItems = new ArrayList<Today>();

        adapter = new DailyBusinessListAdapter(
        		container.getContext(),
        		android.R.layout.simple_list_item_1,
        		R.id.listView1,
        		listItems,
        		inflater,
        		this);

        ListView listView = (ListView)fragmentView.findViewById(R.id.listView1);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(this);

        StatusBarFragmentAdapter.initStatusBar(fragmentView);

        return fragmentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		listItems.clear();

		int balance = TransactionService.getBalance(0);
		if (balance < 0) {
			Today t = EventService.inDeptEvent(balance);
			listItems.add(t);
		}
		else {
			listItems.addAll(TodayDbAdapter.getAllEvents());
		}
		if (listItems.size() < 1) {
			Today t = EventService.nextDayEvent();
			listItems.add(t);
		}
		adapter.notifyDataSetChanged();
		StatusBarFragmentAdapter.initStatusBar(fragmentView);
	}

    @Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    	DailyBusinessListAdapter adapter = (DailyBusinessListAdapter) parent.getAdapter();
		adapter.setActiveIndex(position);
        adapter.notifyDataSetChanged();
	}

	public void refreshTaskList() {
		onResume();
	}

}
