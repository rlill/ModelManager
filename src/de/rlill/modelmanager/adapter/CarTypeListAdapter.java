package de.rlill.modelmanager.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.CarType;
import de.rlill.modelmanager.service.CarService;
import de.rlill.modelmanager.struct.ViewElements;

public class CarTypeListAdapter extends ArrayAdapter<CarType> implements View.OnClickListener {

	private static final String LOG_TAG = CarTypeListAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private Fragment parentFragment;

	public CarTypeListAdapter(Context context, int resource, int textViewResourceId,
			List<CarType> objects, LayoutInflater inflater, Fragment parentFragment) {
		super(context, resource, textViewResourceId, objects);
        mInflater = inflater;
        this.parentFragment = parentFragment;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//data from your adapter
		CarType carType = getItem(position);
		CarTypeListViewElements viewElements = null;

		// reuse already constructed row views...
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_car_type, null);
			viewElements = new CarTypeListViewElements(convertView);
			convertView.setTag(viewElements);
		}
		else {
			viewElements = (CarTypeListViewElements) convertView.getTag();
		}

		viewElements.setTitle(carType.getDescription());
		viewElements.setClass(carType.getCclass().getName());
		viewElements.setPrice(carType.getPrice());
		viewElements.setContextInt(carType.getId());
		viewElements.setUpdateableView(parentFragment);

		Button b = (Button)convertView.findViewById(R.id.button1);
		if (b != null) b.setOnClickListener(this);

		return convertView;
	}

    private class CarTypeListViewElements extends ViewElements {

    	public CarTypeListViewElements(View view) {
        	super(view);
    	}

    	public void setTitle(String name) {
    		findTextView(R.id.textViewTitle).setText(name);
    	}

    	public void setClass(String name) {
    		findTextView(R.id.textViewClass).setText(name);
    	}

    	public void setPrice(int price) {
    		findTextView(R.id.textViewPrice).setText(Util.amount(price));
    	}
    }

	@Override
	public void onClick(View v) {
		ViewElements ve = (ViewElements)v.getTag();
		int carId = ve.getContextInt();
		CarService.buyCar(carId);
		ve.updateParentView();
	}

}
