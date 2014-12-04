package de.rlill.modelmanager.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.adapter.ModelListAdapter;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.dialog.ModelNegotiationDialog;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.service.ModelService.ModelNameComparator;
import de.rlill.modelmanager.struct.ModelStatus;

public class MyModelsFragment extends Fragment implements OnClickListener, OnItemClickListener {

	private static final String LOG_TAG = MyModelsFragment.class.getSimpleName();

	private List<Model> listItems;
    private ArrayAdapter<Model> adapter;
    private View fragmentView;
    private List<ModelStatus> filterStatusList = new ArrayList<ModelStatus>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		fragmentView = inflater.inflate(R.layout.fragment_models, container, false);

        listItems = new ArrayList<Model>();

        adapter = new ModelListAdapter(
        		container.getContext(),
        		android.R.layout.simple_list_item_1,
        		R.id.listView1,
        		listItems,
        		inflater);

        ListView listView = (ListView)fragmentView.findViewById(R.id.listView1);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(this);

        fragmentView.findViewById(R.id.filter_free).setOnClickListener(this);
        fragmentView.findViewById(R.id.filter_unavailable).setOnClickListener(this);
        fragmentView.findViewById(R.id.filter_hired).setOnClickListener(this);
        fragmentView.findViewById(R.id.filter_sick).setOnClickListener(this);
        fragmentView.findViewById(R.id.filter_vacation).setOnClickListener(this);
        fragmentView.findViewById(R.id.filter_training).setOnClickListener(this);
        fragmentView.findViewById(R.id.filter_movieprod).setOnClickListener(this);

        StatusBarFragmentAdapter.initStatusBar(fragmentView);

        return fragmentView;
	}

    @Override
    public void onResume() {
        super.onResume();
		updateFilterList();
	    refreshData();
        StatusBarFragmentAdapter.initStatusBar(fragmentView);
    }

    private void updateFilterList() {
        filterStatusList.clear();
        if (((ToggleButton)fragmentView.findViewById(R.id.filter_free)).isChecked()) filterStatusList.add(ModelStatus.FREE);
        if (((ToggleButton)fragmentView.findViewById(R.id.filter_unavailable)).isChecked()) filterStatusList.add(ModelStatus.UNAVAILABLE);
        if (((ToggleButton)fragmentView.findViewById(R.id.filter_hired)).isChecked()) filterStatusList.add(ModelStatus.HIRED);
        if (((ToggleButton)fragmentView.findViewById(R.id.filter_sick)).isChecked()) filterStatusList.add(ModelStatus.SICK);
        if (((ToggleButton)fragmentView.findViewById(R.id.filter_vacation)).isChecked()) filterStatusList.add(ModelStatus.VACATION);
        if (((ToggleButton)fragmentView.findViewById(R.id.filter_training)).isChecked()) filterStatusList.add(ModelStatus.TRAINING);
        if (((ToggleButton)fragmentView.findViewById(R.id.filter_movieprod)).isChecked()) filterStatusList.add(ModelStatus.MOVIEPROD);
    }

    public void refreshData() {
    	listItems.clear();
    	listItems.addAll(ModelService.getAllModels(filterStatusList));

    	Collections.sort(listItems, new ModelNameComparator());

    	adapter.notifyDataSetChanged();
    }

	@Override
	public void onClick(View v) {
		updateFilterList();
	    refreshData();
	}

    @Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		ModelListAdapter adapter = (ModelListAdapter) parent.getAdapter();
		Model model = adapter.getItem(position);

		Intent intent = new Intent(parent.getContext(), ModelNegotiationDialog.class);
	    intent.putExtra(ModelNegotiationDialog.EXTRA_MODEL_ID, model.getId());
	    startActivity(intent);
	}
}
