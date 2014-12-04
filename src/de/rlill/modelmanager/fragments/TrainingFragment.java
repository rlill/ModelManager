package de.rlill.modelmanager.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.adapter.TrainingListAdapter;
import de.rlill.modelmanager.model.Training;
import de.rlill.modelmanager.setup.Trainings;

public class TrainingFragment extends Fragment {

	private static final String LOG_TAG = TrainingFragment.class.getSimpleName();
    private List<Training> listItems;
    private ArrayAdapter<Training> adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_training_list, container, false);

        listItems = new ArrayList<Training>();

        adapter = new TrainingListAdapter(
        		container.getContext(),
        		android.R.layout.simple_list_item_1,
        		R.id.listView1,
        		listItems,
        		inflater);

        ListView listView = (ListView)view.findViewById(R.id.listView1);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        StatusBarFragmentAdapter.initStatusBar(view);

        return view;
	}

    @Override
    public void onResume() {
        super.onResume();
	    listItems.clear();
	    listItems.addAll(Trainings.getTrainings());
        adapter.notifyDataSetChanged();
    }

}
