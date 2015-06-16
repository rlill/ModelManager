package de.rlill.modelmanager.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.ModelTraining;
import de.rlill.modelmanager.model.Training;
import de.rlill.modelmanager.persistance.ModelTrainingDbAdapter;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.struct.ViewElements;

public class TrainingListAdapter extends ArrayAdapter<Training> {

	private static final String LOG_TAG = TrainingListAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private Context mContext;

	public TrainingListAdapter(Context context, int resource, int textViewResourceId,
			List<Training> objects, LayoutInflater inflater) {
		super(context, resource, textViewResourceId, objects);
        mInflater = inflater;
        mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//data from your adapter
		Training training = getItem(position);
		TrainingListViewElements viewElements = null;

		// reuse already constructed row views...
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_training_item, null);
			viewElements = new TrainingListViewElements(convertView);
			convertView.setTag(viewElements);
		}
		else {
			viewElements = (TrainingListViewElements) convertView.getTag();
		}

		viewElements.setContextInt(training.getId());
		viewElements.setName(training.getDescription());
		viewElements.setPrice(training.getPrice());

		String days = mContext.getResources().getString(
				(training.getDuration() == 1) ? R.string.display_day_si : R.string.display_day_pl);
		viewElements.setDuration(String.format("%d %s", training.getDuration(), days));

		StringBuilder improvements = new StringBuilder();
		String qualification = mContext.getResources().getString(R.string.display_msg_training_qualification_as);

		String ii = improveIndicator(training.getInc_qphoto());
		if (ii != null) {
			String label = mContext.getResources().getString(R.string.labelQualityPhoto);
			improvements.append(ii).append(qualification).append(' ').append(label).append(' ');
		}

		ii = improveIndicator(training.getInc_qmovie());
		if (ii != null) {
			String label = mContext.getResources().getString(R.string.labelQualityMovie);
			improvements.append(ii).append(qualification).append(' ').append(label).append(' ');
		}

		ii = improveIndicator(training.getInc_qtlead());
		if (ii != null) {
			String label = mContext.getResources().getString(R.string.labelQualityTLead);
			improvements.append(ii).append(qualification).append(' ').append(label).append(' ');
		}

		ii = improveIndicator(training.getInc_ambition());
		if (ii != null) {
			String label = mContext.getResources().getString(R.string.labelAmbition);
			improvements.append(ii).append(label).append(' ');
		}

		ii = improveIndicator(training.getInc_criminal());
		if (ii != null) {
			String label = mContext.getResources().getString(R.string.labelCriminal);
			improvements.append(ii).append(label).append(' ');
		}

		ii = improveIndicator(training.getInc_erotic());
		if (ii != null) {
			String label = mContext.getResources().getString(R.string.labelErotic);
			improvements.append(ii).append(label).append(' ');
		}

		ii = improveIndicator(training.getInc_health());
		if (ii != null) {
			String label = mContext.getResources().getString(R.string.labelHealth);
			improvements.append(ii).append(label).append(' ');
		}

		ii = improveIndicator(training.getInc_mood());
		if (ii != null) {
			String label = mContext.getResources().getString(R.string.labelMood);
			improvements.append(ii).append(label).append(' ');
		}

		viewElements.setDescription(improvements.toString());

		String day = mContext.getResources().getString(R.string.display_day_si);
		StringBuilder details = new StringBuilder();
		int costsum = 0;
		for (ModelTraining mt : ModelTrainingDbAdapter.getTrainings(training.getId())) {

			Model model = ModelService.getModelById(mt.getModelId());
			details.append(model.getFullname()).append(" ");
			details.append(day).append(" ").append(mt.getStartDay()).append("-").append(mt.getEndDay()).append(" ");
			details.append(Util.amount(mt.getPrice())).append(" ");
			details.append(mt.getTrainingStatus().getName()).append(" ");
			details.append("\n");

			costsum += mt.getPrice();
		}
		if (costsum > 0) {
			String sum = mContext.getResources().getString(R.string.labelSum);
			details.append(sum).append(": ").append(Util.amount(costsum)).append("\n");
		}

		viewElements.setDetails(details.toString());

		return convertView;
	}

	private static String improveIndicator(int inc) {
		if (inc > 30) return "++";
		if (inc > 0) return "+";
		if (inc < 0) return "-";
		return null;
	}

    private class TrainingListViewElements extends ViewElements {

    	public TrainingListViewElements(View view) {
        	super(view);
    	}

    	public void setName(String name) {
    		findTextView(R.id.textViewName).setText(name);
    	}
    	public void setPrice(int price) {
    		findTextView(R.id.textViewPrice).setText(Util.amount(price));
    	}
    	public void setDetails(String details) {
    		findTextView(R.id.textViewDetails).setText(details);
    	}
	    public void setDescription(String description) {
		    findTextView(R.id.textViewDescription).setText(description);
	    }
    	public void setDuration(String d) {
    		findTextView(R.id.textViewDuration).setText(d);
    	}
    }

}
