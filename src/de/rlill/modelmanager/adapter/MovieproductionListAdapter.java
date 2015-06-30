package de.rlill.modelmanager.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.MovieModel;
import de.rlill.modelmanager.model.Movieproduction;
import de.rlill.modelmanager.service.DiaryService;
import de.rlill.modelmanager.service.MovieService;
import de.rlill.modelmanager.struct.MovieType;
import de.rlill.modelmanager.struct.ViewElements;

public class MovieproductionListAdapter extends ArrayAdapter<Movieproduction> {

	private static final String LOG_TAG = MovieproductionListAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private Context mContext;

	public MovieproductionListAdapter(Context context, int resource, int textViewResourceId,
			List<Movieproduction> objects, LayoutInflater inflater) {
		super(context, resource, textViewResourceId, objects);
        mInflater = inflater;
        mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//data from your adapter
		Movieproduction mpr = getItem(position);
		MovieproductionListViewElements viewElements = null;

		// reuse already constructed row views...
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_movieproduction_item, null);
			viewElements = new MovieproductionListViewElements(convertView);
			convertView.setTag(viewElements);
		}
		else {
			viewElements = (MovieproductionListViewElements) convertView.getTag();
		}

		int ws = 0;
		for (MovieModel mm : MovieService.getModelsForMovie(mpr.getId(), DiaryService.today() + 1)) {
			ws += mm.getPrice();
		}

		viewElements.setContextInt(mpr.getId());
		viewElements.setName(mpr.getName());
		viewElements.setType(mpr.getType());
		viewElements.setStartDay(mpr.getStartDay());
		viewElements.setDetails(mpr.getStatus().getName() + ", "
				+ convertView.getResources().getString(R.string.labelMovieDailyPayments)
				+ ": "
				+ Util.amount(ws));

		return convertView;
	}

    private class MovieproductionListViewElements extends ViewElements {

    	public MovieproductionListViewElements(View view) {
        	super(view);
    	}

    	public void setName(String name) {
    		findTextView(R.id.textViewName).setText(name);
    	}
    	public void setType(MovieType type) {
    		findTextView(R.id.textViewType).setText(type.getName());
    	}
    	public void setDetails(String details) {
    		findTextView(R.id.textViewDetails).setText(details);
    	}
    	public void setStartDay(int d) {
    		findTextView(R.id.textViewStartDay).setText(Integer.toString(d));
    	}
    }

}
