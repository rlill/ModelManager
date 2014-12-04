package de.rlill.modelmanager.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.ViewElements;

public class ModelSpinnerAdapter extends ArrayAdapter<Model> {

	private static final String LOG_TAG = "MM*" + ModelSpinnerAdapter.class.getSimpleName();
	private Context appContext;
    private LayoutInflater mInflater;
    private EventFlag mEventFlag;

	public ModelSpinnerAdapter(Context context, int resource, int textViewResourceId,
			List<Model> objects, LayoutInflater inflater, EventFlag eventFlag) {
		super(context, resource, textViewResourceId, objects);
		appContext = context;
        mInflater = inflater;
        mEventFlag = eventFlag;
	}

	@Override
	public View getView(int pos, View cnvtView, ViewGroup prnt) {
		return getCustomView(pos, cnvtView, prnt);
	}

	@Override
	public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
		return getCustomView(position, cnvtView, prnt);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {

		// data from the adapter
		Model model = getItem(position);
		ModelSpinnerViewElements viewElements = null;

		// reuse already constructed row views...
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_model_spinner_item, null);
			viewElements = new ModelSpinnerViewElements(convertView);
			convertView.setTag(viewElements);
		}
		else {
			viewElements = (ModelSpinnerViewElements) convertView.getTag();
		}

		viewElements.setContextModel(model);
		viewElements.setName(model.getFullname());
		viewElements.setStatus(model.getStatus().getName());

		if (mEventFlag == EventFlag.MOVIE)
			viewElements.setQuality(model.getQuality_movie());
		else if (mEventFlag == EventFlag.PHOTO)
			viewElements.setQuality(model.getQuality_photo());
		else
			viewElements.setQuality(model.getQuality_tlead());

		int ir = appContext.getResources().getIdentifier(model.getImage(), "drawable", appContext.getPackageName());
//		Log.d(LOG_TAG, String.format("Image %s, resource ID 0x%08X", model.getImage(), ir));
		viewElements.getImage().setImageResource(ir);

		return convertView;
	}

    private class ModelSpinnerViewElements extends ViewElements {
        private ImageView image = null;
        private RatingBar ratingBar = null;

    	public ModelSpinnerViewElements(View view) {
        	super(view);
    	}

    	public ImageView getImage() {
    		if (image == null) image = (ImageView) adaptedView.findViewById(R.id.imageView1);
    		return image;
    	}

    	public void setName(String name) {
    		findTextView(R.id.textView1).setText(name);
    	}

    	public void setStatus(String status) {
    		findTextView(R.id.textViewStatus).setText(status);
    	}

    	public void setQuality(int quality) {
    		if (ratingBar == null) ratingBar = (RatingBar)adaptedView.findViewById(R.id.ratingBar1);
    		ratingBar.setRating((float)quality / 10);
    	}
    }

}
