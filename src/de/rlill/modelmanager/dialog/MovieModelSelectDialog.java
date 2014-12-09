package de.rlill.modelmanager.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.adapter.ModelSpinnerAdapter;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.MovieModel;
import de.rlill.modelmanager.model.Movieproduction;
import de.rlill.modelmanager.persistance.MovieModelDbAdapter;
import de.rlill.modelmanager.service.DiaryService;
import de.rlill.modelmanager.service.MessageService;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.service.MovieService;
import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.ModelStatus;

public class MovieModelSelectDialog extends Activity implements OnClickListener {

	private static final String LOG_TAG = "MM*" + MovieModelSelectDialog.class.getSimpleName();

	public final static String EXTRA_MOVIE_ID = "movie.model.select.dialog.movie.id";
	public final static String EXTRA_MODEL_ID = "movie.model.select.dialog.model.id";
	public final static String EXTRA_PRICE = "movie.model.select.dialog.price";
	public final static String EXTRA_MODE= "movie.model.select.dialog.mode";

	public final static int MODE_ADD = 1;
	public final static int MODE_MODIFY = 2;

	private int movieId;
	private int modelId;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_movie_model_select);

		// Show the Up button in the action bar.
//		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the message from the intent
		Intent intent = getIntent();
		movieId = intent.getIntExtra(EXTRA_MOVIE_ID, 0);
		modelId = intent.getIntExtra(EXTRA_MODEL_ID, 0);

		if (movieId == 0) return;

		TextView tv = (TextView)findViewById(R.id.textViewDay);
		tv.setText(Integer.toString(DiaryService.today() + 1));

		Spinner sp = (Spinner)findViewById(R.id.selectModel);

		List<Model> modelList = new ArrayList<Model>();

		int mode = intent.getIntExtra(EXTRA_MODE, 0);
		if (mode == MODE_MODIFY) {
			// add models currently assigned to movie
			for (MovieModel mm : MovieService.getModelsForMovie(movieId, DiaryService.today() + 1)) {
				modelList.add(ModelService.getModelById(mm.getModelId()));
			}
		}
		else {
			// add available models
			Set<Integer> modelsPlannedTomorrow = new TreeSet<Integer>();
			for (MovieModel mm : MovieService.getModelsInMovieTomorrow()) {
				modelsPlannedTomorrow.add(mm.getModelId());
			}

			List<ModelStatus> statusFilters = new ArrayList<ModelStatus>();
			statusFilters.add(ModelStatus.HIRED);
			statusFilters.add(ModelStatus.SICK);

			for (Model model : ModelService.getAllModels(statusFilters)) {
				if (modelsPlannedTomorrow.contains(model.getId())) continue;
				modelList.add(model);
			}
		}

		Collections.sort(modelList, new ModelService.ModelNameComparator());

		ArrayAdapter<Model> replacementAdapter = new ModelSpinnerAdapter(
				this,
        		R.layout.fragment_model_spinner_item,
        		R.id.textView1,
        		modelList,
        		getLayoutInflater(),
        		EventFlag.MOVIE);
		sp.setAdapter(replacementAdapter);

		// select model to be modified/removed
		if (mode == MODE_MODIFY) {
			Model model = ModelService.getModelById(modelId);
			int p = modelList.indexOf(model);
			if (p < 0) p = 0;
			sp.setSelection(p);
		}

		Button b = (Button)findViewById(R.id.button1);
		b.setOnClickListener(this);

		b = (Button)findViewById(R.id.button2);
		if (mode == MODE_MODIFY)
			b.setOnClickListener(this);
		else
			b.setVisibility(View.GONE);

		if (mode == MODE_MODIFY) {
			int price = intent.getIntExtra(EXTRA_PRICE, 0);
			EditText et = (EditText)findViewById(R.id.editTextOffer);
			et.setText(Integer.toString(price));
		}
	}

	@Override
	public void onClick(View v) {

		Spinner sp = (Spinner)findViewById(R.id.selectModel);
		Model model = (Model) sp.getSelectedItem();

		if (v.getId() == R.id.button1) {
			// validate offer

			EditText et = (EditText)findViewById(R.id.editTextOffer);
			int offer = Util.atoi(et.getText().toString());

			// TODO: do minimum check



			// update already booked or insert new?
			List<MovieModel> mmList = MovieModelDbAdapter.getMovieModels(movieId, model.getId(), DiaryService.today() + 1);
			Log.i(LOG_TAG, String.format("Movie %d, Model %d, day %d: %d relations",
					movieId, model.getId(), DiaryService.today() + 1, mmList.size()));
			if (mmList.size() == 0) {
				String msg = MessageService.getMessage(R.string.logmessage_movie_assign, model);
				Movieproduction mpr = MovieService.getMovie(movieId);
				DiaryService.log(msg.replace("%M", mpr.getName()),
						EventClass.MOVIE_CAST, EventFlag.AVAILABLE, model.getId(), offer);
			}

			// add() performs update (price) if entry already exists
			MovieService.addModelForMovie(movieId, model.getId(), DiaryService.today() + 1, offer);

			Intent intent = getIntent();
			intent.putExtra(EXTRA_MODEL_ID, model.getId());
			setResult(RESULT_OK, intent);

			finish();
		}
		else if (v.getId() == R.id.button2) {
			// remove
			String msg = MessageService.getMessage(R.string.logmessage_movie_deassign, model);
			Movieproduction mpr = MovieService.getMovie(movieId);
			DiaryService.log(msg.replace("%M", mpr.getName()),
					EventClass.MOVIE_CAST, EventFlag.UNAVAILABLE, modelId, 0);

			MovieService.removeModelFromMovie(movieId, model.getId());

			Intent intent = getIntent();
			intent.putExtra(EXTRA_MODEL_ID, model.getId());
			setResult(RESULT_OK, intent);

			finish();
		}
	}


}
