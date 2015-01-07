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
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.CompanyCar;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.Team;
import de.rlill.modelmanager.persistance.CompanyCarDbAdapter;
import de.rlill.modelmanager.service.DiaryService;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.service.ModelService.Statistics;
import de.rlill.modelmanager.service.TransactionService;
import de.rlill.modelmanager.struct.CarStatus;
import de.rlill.modelmanager.struct.ModelStatus;
import de.rlill.modelmanager.struct.ViewElements;
import de.rlill.modelmanager.struct.Weekday;

public class ModelListAdapter extends ArrayAdapter<Model> {

	private static final String LOG_TAG = ModelListAdapter.class.getSimpleName();
	private Context appContext;
    private LayoutInflater mInflater;

	public ModelListAdapter(Context context, int resource, int textViewResourceId,
			List<Model> objects, LayoutInflater inflater) {
		super(context, resource, textViewResourceId, objects);
		appContext = context;
        mInflater = inflater;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//data from your adapter
		Model model = getItem(position);
		ModelListViewElements viewElements = null;

		// reuse already constructed row views...
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_model_list_item, null);
			viewElements = new ModelListViewElements(convertView);
			convertView.setTag(viewElements);
		}
		else {
			viewElements = (ModelListViewElements) convertView.getTag();
		}

		viewElements.setContextModel(model);
		viewElements.setName(model.getFullname());
		viewElements.setStatus(model.getStatus().getName());
		viewElements.setSalary(model.getSalary(), model.getPayday());

		String car = "-";
		if (model.getCarId() > 0) {
			CompanyCar cc = CompanyCarDbAdapter.getCompanyCar(model.getCarId());
			if (cc != null) {
				car = cc.getCarType().getDescription();
				if (cc.getStatus() != CarStatus.IN_USE)
					car += " (" + cc.getStatus().getName() + ")";
			}
		}
		viewElements.setCar(car);
		viewElements.setBalance(TransactionService.getBalance(model.getId()));

		Statistics st = ModelService.getStatistics(model.getId());
		StringBuilder ts = new StringBuilder();
		if (st.movieToday > 0) {
			String msess = appContext.getResources().getString(
					(st.movieToday == 1) ? R.string.labelMovieSession_si : R.string.labelMovieSession_pl);
			ts.append(st.movieToday).append(" ").append(msess);
		}
		if (st.photoToday > 0) {
			String msess = appContext.getResources().getString(
					(st.photoToday == 1) ? R.string.labelPhotoSession_si : R.string.labelPhotoSession_pl);
			if (ts.length() > 0) ts.append(", ");
			ts.append(st.photoToday).append(" ").append(msess);
		}
		if (st.teamleadToday > 0) {
			String msess = appContext.getResources().getString(
					(st.teamleadToday == 1) ? R.string.labelTeamleadWork_si : R.string.labelTeamleadWork_pl);
			if (ts.length() > 0) ts.append(", ");
			ts.append(st.teamleadToday).append(" ").append(msess);
		}
		viewElements.setToday(ts.toString());

		viewElements.setOnBoard((model.getStatus() == ModelStatus.FREE) ? ""
				: "#" + model.getHireday() + " (" + Util.duration(appContext, (DiaryService.today() - model.getHireday())) + ")");

		String name = "";
		if (model.getStatus() != ModelStatus.FREE && model.getTeamId() > 0) {
			name = ModelService.getTeamName(model.getTeamId());
			Team team = ModelService.getTeam(model.getTeamId());
			if (team.getLeader1() == model.getId() || team.getLeader2() == model.getId()) name += " *";
		}
		viewElements.setTeam(name);

		viewElements.setMood(model.getMood());

		viewElements.setQualityPhoto(model.getQuality_photo());
		viewElements.setQualityMovie(model.getQuality_movie());
		viewElements.setQualityTLead(model.getQuality_tlead());

		int ir = appContext.getResources().getIdentifier(model.getImage(), "drawable", appContext.getPackageName());
//		Log.d(LOG_TAG, "Image " + model.getImage() + " resource ID: " + ir);
		viewElements.getImage().setImageResource(ir);

		return convertView;
	}

    private class ModelListViewElements extends ViewElements {
        private ImageView image = null;
        private RatingBar ratingQualityPhoto;
        private RatingBar ratingQualityMovie;
        private RatingBar ratingQualityTLead;
        private RatingBar ratingMood;

    	public ModelListViewElements(View view) {
        	super(view);
    	}

    	public ImageView getImage() {
    		if (image == null) image = (ImageView) adaptedView.findViewById(R.id.imageView1);
    		return image;
    	}

    	public void setName(String name) {
    		findTextView(R.id.textViewName).setText(name);
    	}

    	public void setStatus(String status) {
    		findTextView(R.id.textViewStatus).setText(status);
    	}

    	public void setSalary(int salary, Weekday payday) {
    		findTextView(R.id.textViewSalary).setText(Util.amount(salary) + " (" + payday.getName().substring(0, 2) + ")");
    	}

    	public void setCar(String car) {
    		findTextView(R.id.textViewCar).setText(car);
    	}

    	public void setBalance(int balance) {
    		findTextView(R.id.textViewBalance).setText(Util.amount(balance));
    	}

    	public void setToday(String t) {
    		findTextView(R.id.textViewToday).setText(t);
    	}

    	public void setOnBoard(String s) {
    		findTextView(R.id.textViewOnBoard).setText(s);
    	}

    	public void setTeam(String t) {
    		findTextView(R.id.textViewTeam).setText(t);
    	}

    	public void setQualityPhoto(int quality) {
    		if (ratingQualityPhoto == null) ratingQualityPhoto = (RatingBar)adaptedView.findViewById(R.id.ratingBarQualityPhoto);
    		ratingQualityPhoto.setRating((float)quality / 10);
    	}

    	public void setQualityMovie(int quality) {
    		if (ratingQualityMovie == null) ratingQualityMovie = (RatingBar)adaptedView.findViewById(R.id.ratingBarQualityMovie);
    		ratingQualityMovie.setRating((float)quality / 10);
    	}

    	public void setQualityTLead(int quality) {
    		if (ratingQualityTLead == null) ratingQualityTLead = (RatingBar)adaptedView.findViewById(R.id.ratingBarQualityTLead);
    		ratingQualityTLead.setRating((float)quality / 10);
    	}

    	public void setMood(int mood) {
    		if (ratingMood == null) ratingMood = (RatingBar)adaptedView.findViewById(R.id.ratingBarMood);
    		ratingMood.setRating((float)mood / 10);
    	}
    }

}
