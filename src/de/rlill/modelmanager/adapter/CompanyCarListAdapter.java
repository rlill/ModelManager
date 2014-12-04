package de.rlill.modelmanager.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.CompanyCar;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.service.CarService;
import de.rlill.modelmanager.service.DiaryService;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.struct.CarStatus;
import de.rlill.modelmanager.struct.ViewElements;

public class CompanyCarListAdapter extends ArrayAdapter<CompanyCar> {

	private static final String LOG_TAG = CompanyCarListAdapter.class.getSimpleName();
	private Context appContext;
    private LayoutInflater mInflater;

	public CompanyCarListAdapter(Context context, int resource, int textViewResourceId,
			List<CompanyCar> objects, LayoutInflater inflater) {
		super(context, resource, textViewResourceId, objects);
		appContext = context;
        mInflater = inflater;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//data from your adapter
		CompanyCar companyCar = getItem(position);
		CompanyCarListViewElements viewElements = null;

		// reuse already constructed row views...
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_company_car, null);
			viewElements = new CompanyCarListViewElements(convertView);
			convertView.setTag(viewElements);
		}
		else {
			viewElements = (CompanyCarListViewElements) convertView.getTag();
		}

		viewElements.setName(companyCar.getCarType().getDescription() + " [" + companyCar.getLicensePlate() + "]");
		viewElements.setClass(companyCar.getCarType().getCclass().getName());
		viewElements.setAge(DiaryService.today() - companyCar.getBuyday());
		viewElements.setValue(CarService.getCarValue(companyCar.getId()));

		String driver = "-";
		int modelId = CarService.getDriverId(companyCar.getId());
		if (modelId > 0) {
			Model model = ModelService.getModelById(modelId);
			driver = model.getFullname();

			viewElements.setContextModel(model);

			int ir = appContext.getResources().getIdentifier(model.getImage(), "drawable", appContext.getPackageName());
			viewElements.setModelImage(ir);
		}
		else
			viewElements.setModelImage(0);
		viewElements.setDriver(driver);
		viewElements.setStatus(companyCar.getStatus());
		viewElements.setImage(companyCar.getCarType().getImageId());

		return convertView;
	}

    private class CompanyCarListViewElements extends ViewElements {

    	private ImageView carIcon;
    	private ImageView modelImage;

    	public CompanyCarListViewElements(View view) {
        	super(view);
    	}

    	public void setName(String name) {
    		findTextView(R.id.textViewTitle).setText(name);
    	}
    	public void setClass(String c) {
    		findTextView(R.id.textViewClass).setText(c);
    	}
    	public void setAge(int age) {
    		findTextView(R.id.textViewAge).setText(Util.duration(appContext, age));
    	}
    	public void setValue(int value) {
    		findTextView(R.id.textViewValue).setText(Util.amount(value));
    	}
    	public void setDriver(String name) {
    		findTextView(R.id.textViewDriver).setText(name);
    	}
    	public void setStatus(CarStatus status) {
    		TextView tv =  findTextView(R.id.textViewStatus);
    		tv.setText(status.getName());
    		switch (status) {
    		case NEW:
    		case IN_USE:
    			tv.setTextColor(Color.BLACK);
    			break;
    		case USED_ALITTLE:
    		case USED_ALOT:
    			tv.setTextColor(Color.BLUE);
    			break;
    		default:
    			tv.setTextColor(Color.RED);
    		}
    	}
    	public void setImage(int res) {
    		if (carIcon == null) carIcon = (ImageView)adaptedView.findViewById(R.id.imageView1);
    		carIcon.setImageResource(res);
    	}
    	public void setModelImage(int res) {
    		if (modelImage == null) modelImage = (ImageView)adaptedView.findViewById(R.id.messageImage);
    		if (res > 0) {
    			modelImage.setImageResource(res);
    			modelImage.setVisibility(ImageView.VISIBLE);
    		}
    		else
    			modelImage.setVisibility(ImageView.INVISIBLE);

    	}
    }

}
