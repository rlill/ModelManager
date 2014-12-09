package de.rlill.modelmanager.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.adapter.AccountListAdapter;
import de.rlill.modelmanager.dialog.AccountDetailDialog;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.service.ModelService;

public class AccountFragment extends Fragment implements OnItemClickListener {

	private static final String LOG_TAG = "MM*" + AccountFragment.class.getSimpleName();
    private List<Model> listItems;
    private ArrayAdapter<Model> adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        listItems = new ArrayList<Model>();

        adapter = new AccountListAdapter(
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
	    Model myself = new Model(0);
	    myself.setFirstname("Mein");
	    myself.setLastname("Konto");
	    listItems.add(myself);

	    List<Model> accountModels = ModelService.getHiredModels();
	    Collections.sort(accountModels, new ModelService.ModelNameComparator());

	    Log.d(LOG_TAG, "Displaying " + (1 + accountModels.size()) + " accounts");
	    listItems.addAll(accountModels);
        adapter.notifyDataSetChanged();
    }

    @Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		AccountListAdapter adapter = (AccountListAdapter) parent.getAdapter();
		Model model = adapter.getItem(position);

		Intent intent = new Intent(parent.getContext(), AccountDetailDialog.class);
	    intent.putExtra(AccountDetailDialog.EXTRA_MODEL_ID, model.getId());
	    startActivity(intent);
    }

}
