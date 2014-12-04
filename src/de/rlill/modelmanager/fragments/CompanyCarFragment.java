package de.rlill.modelmanager.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.adapter.CarTypeListAdapter;
import de.rlill.modelmanager.adapter.CompanyCarListAdapter;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.dialog.CarMaintenanceDialog;
import de.rlill.modelmanager.model.CarType;
import de.rlill.modelmanager.model.CompanyCar;
import de.rlill.modelmanager.service.CarService;
import de.rlill.modelmanager.setup.CarTypes;

public class CompanyCarFragment extends Fragment implements OnItemClickListener {

	private static final String LOG_TAG = CompanyCarFragment.class.getSimpleName();

	private View fragmentView;
    private List<CarType> carTypeListItems;
    private List<CompanyCar> companyCarListItems;
    private ArrayAdapter<CarType> carTypeAdapter;
    private ArrayAdapter<CompanyCar> companyCarAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		fragmentView = inflater.inflate(R.layout.fragment_company_cars, container, false);

        StatusBarFragmentAdapter.initStatusBar(fragmentView);

        carTypeListItems = new ArrayList<CarType>();
        companyCarListItems = new ArrayList<CompanyCar>();

        carTypeAdapter = new CarTypeListAdapter(
        		container.getContext(),
        		android.R.layout.simple_list_item_1,
        		R.id.listCompanyCars,
        		carTypeListItems,
        		inflater,
        		this);

        companyCarAdapter = new CompanyCarListAdapter(
        		container.getContext(),
        		android.R.layout.simple_list_item_1,
        		R.id.listCarTypes,
        		companyCarListItems,
        		inflater);

        ListView listView = (ListView)fragmentView.findViewById(R.id.listCompanyCars);
        listView.setAdapter(companyCarAdapter);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(this);

        updateSummary();

        return fragmentView;
	}

    @Override
	public void onResume() {
		super.onResume();
		carTypeListItems.clear();
		carTypeListItems.addAll(CarTypes.getCarTypes());
		carTypeAdapter.notifyDataSetChanged();

		companyCarListItems.clear();
		companyCarListItems.addAll(CarService.getCompanyPoolCars());
		companyCarAdapter.notifyDataSetChanged();

		updateSummary();

        StatusBarFragmentAdapter.initStatusBar(fragmentView);
    }

    @Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    	CompanyCarListAdapter adapter = (CompanyCarListAdapter) parent.getAdapter();
    	CompanyCar cc = adapter.getItem(position);

		Intent intent = new Intent(parent.getContext(), CarMaintenanceDialog.class);
	    intent.putExtra(CarMaintenanceDialog.EXTRA_COMPANYCAR_ID, cc.getId());
	    startActivity(intent);
//	    updateSummary();
    }

    private void updateSummary() {

        List<CompanyCar> allCars = CarService.getCompanyPoolCars();
        int valsumInUse = 0;
        int countInUse = 0;
        int valsumUnused = 0;
        int countUnused = 0;
        int valsumGone = 0;
        int countGone = 0;
        int valsumDefect = 0;
        int countDefect = 0;
        int valsumTotal = 0;
        int countTotal = allCars.size();
        for (CompanyCar cc : allCars) {
    		int cval = CarService.getCarValue(cc.getId());
    		valsumTotal += cval;
        	switch (cc.getStatus()) {
        	case IN_USE:
        		valsumInUse += cval;
        		countInUse++;
        		break;
        	case NEW:
        	case USED_ALITTLE:
        	case USED_ALOT:
        		valsumUnused += cval;
        		countUnused++;
        		break;
        	case DEFECT:
        		valsumDefect += cval;
        		countDefect++;
        		break;
        	case STOLEN:
        	case WRECKED:
        		valsumGone += cval;
        		countGone++;
        	default:
        	}
        }

        TextView tv = (TextView)fragmentView.findViewById(R.id.textViewCarsInUse);
        tv.setText(Integer.toString(countInUse));

        tv = (TextView)fragmentView.findViewById(R.id.textViewValueInUse);
        tv.setText(Util.amount(valsumInUse));

        tv = (TextView)fragmentView.findViewById(R.id.textViewCarsUnused);
        tv.setText(Integer.toString(countUnused));

        tv = (TextView)fragmentView.findViewById(R.id.textViewValueUnused);
        tv.setText(Util.amount(valsumUnused));

        tv = (TextView)fragmentView.findViewById(R.id.textViewCarsGone);
        tv.setText(Integer.toString(countGone));

        tv = (TextView)fragmentView.findViewById(R.id.textViewValueGone);
        tv.setText(Util.amount(valsumGone));

        tv = (TextView)fragmentView.findViewById(R.id.textViewCarsDefect);
        tv.setText(Integer.toString(countDefect));

        tv = (TextView)fragmentView.findViewById(R.id.textViewValueDefect);
        tv.setText(Util.amount(valsumDefect));

        tv = (TextView)fragmentView.findViewById(R.id.textViewCarsTotal);
        tv.setText(Integer.toString(countTotal));

        tv = (TextView)fragmentView.findViewById(R.id.textViewValueTotal);
        tv.setText(Util.amount(valsumTotal));
    }
}
