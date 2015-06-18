package de.rlill.modelmanager.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.ModelTraining;
import de.rlill.modelmanager.model.Training;
import de.rlill.modelmanager.persistance.ModelTrainingDbAdapter;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.struct.TrainingStatus;
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

		if (training.getId() == -1) {
			viewElements.setName(training.getDescription());
			viewElements.hideDetails();

			StringBuilder summary = new StringBuilder();
			int daysum_p = 0;
			int trainingsum_p = 0;
			int costsum_p = 0;
			int daysum_w = 0;
			int trainingsum_w = 0;
			int costsum_w = 0;
			int daysum_f = 0;
			int trainingsum_f = 0;
			int costsum_f = 0;
			int daysum_e = 0;
			int trainingsum_e = 0;
			int costsum_e = 0;

			for (int i = 0; i < getCount(); i++) {
				Training t = getItem(i);
				for (ModelTraining mt : ModelTrainingDbAdapter.getTrainings(t.getId())) {

					switch (mt.getTrainingStatus()) {
						case PLANNED:
							trainingsum_p++;
							daysum_p += t.getDuration();
							costsum_p += mt.getPrice();
							break;
						case IN_PROGRESS:
							trainingsum_w++;
							daysum_w += t.getDuration();
							costsum_w += mt.getPrice();
							break;
						case FINISHED:
							trainingsum_f++;
							daysum_f += t.getDuration();
							costsum_f += mt.getPrice();
							break;
						case FAILED:
							trainingsum_e++;
							daysum_e += t.getDuration();
							costsum_e += mt.getPrice();
							break;
					}
				}
			}

			String day_si = mContext.getResources().getString(R.string.display_day_si);
			String day_pl = mContext.getResources().getString(R.string.display_day_pl);
			String course_si = mContext.getResources().getString(R.string.display_course_si);
			String course_pl = mContext.getResources().getString(R.string.display_course_pl);

			if (trainingsum_p > 0) {
				summary.append(TrainingStatus.PLANNED.getName()).append(": ")
						.append(Integer.toString(trainingsum_p)).append(' ')
						.append(trainingsum_p == 1 ? course_si : course_pl).append(", ")
						.append(Integer.toString(daysum_p)).append(' ')
						.append(daysum_p == 1 ? day_si : day_pl).append(", ")
						.append(Util.amount(costsum_p)).append('\n');
			}

			if (trainingsum_w > 0) {
				summary.append(TrainingStatus.IN_PROGRESS.getName()).append(": ")
						.append(Integer.toString(trainingsum_w)).append(' ')
						.append(trainingsum_w == 1 ? course_si : course_pl).append(", ")
						.append(Integer.toString(daysum_w)).append(' ')
						.append(daysum_w == 1 ? day_si : day_pl).append(", ")
						.append(Util.amount(costsum_w)).append('\n');
			}

			if (trainingsum_f > 0) {
				summary.append(TrainingStatus.FINISHED.getName()).append(": ")
						.append(Integer.toString(trainingsum_f)).append(' ')
						.append(trainingsum_f == 1 ? course_si : course_pl).append(", ")
						.append(Integer.toString(daysum_f)).append(' ')
						.append(daysum_f == 1 ? day_si : day_pl).append(", ")
						.append(Util.amount(costsum_f)).append('\n');
			}

			if (trainingsum_e > 0) {
				summary.append(TrainingStatus.FAILED.getName()).append(": ")
						.append(Integer.toString(trainingsum_e)).append(' ')
						.append(trainingsum_e == 1 ? course_si : course_pl).append(", ")
						.append(Integer.toString(daysum_e)).append(' ')
						.append(daysum_e == 1 ? day_si : day_pl).append(", ")
						.append(Util.amount(costsum_e)).append('\n');
			}

			viewElements.setDetails(summary.toString());

			return convertView;
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
	    public void hideDetails() {
		    adaptedView.findViewById(R.id.trainingDetails).setVisibility(LinearLayout.GONE);
	    }
    }

}
