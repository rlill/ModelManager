package de.rlill.modelmanager.dialog;

import java.util.Set;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.MovieModel;
import de.rlill.modelmanager.model.Movieproduction;
import de.rlill.modelmanager.service.DiaryService;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.service.MovieService;
import de.rlill.modelmanager.struct.MovieStatus;

public class MovieproductionDialog extends Activity implements View.OnClickListener {

	private static final String LOG_TAG = "MM*" + MovieproductionDialog.class.getSimpleName();

	public final static String EXTRA_MOVIE_ID = "movieproduction.dialog.movie.id";
	private int movieId;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_movieproduction);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		StatusBarFragmentAdapter.initStatusBar(findViewById(R.id.status_bar_include));

		// Get the message from the intent
		Intent intent = getIntent();
		movieId = intent.getIntExtra(EXTRA_MOVIE_ID, 0);

		if (movieId == 0) return;
		refresh();

		Movieproduction mpr = MovieService.getMovie(movieId);

		// Movie Name, Type
		TableLayout tab = (TableLayout) findViewById(R.id.tableMovieDetails);
		addDetailRow(tab, R.string.labelName, mpr.getName(), R.string.labelMovieType, mpr.getType().getName());

		// Movie start, end
		String sday = getResources().getString(R.string.display_day_si) + " " + mpr.getStartDay()
				+ " (" + Util.weekday(mpr.getStartDay()).getName().substring(0, 2) + ")";
		String eday = (mpr.getEndDay() > 0) ? getResources().getString(R.string.display_day_si) + " " + mpr.getEndDay()
				+ " (" + Util.weekday(mpr.getEndDay()).getName().substring(0, 2) + ")" : "";
		addDetailRow(tab, R.string.labelStartDay, sday, R.string.labelEndDay, eday);

		if (mpr.getStatus() == MovieStatus.PLANNED || mpr.getStatus() == MovieStatus.IN_PROGRESS) {

			// Movie value, cost
			int movieValue = MovieService.getMovieValue(movieId);
			int movieCost = MovieService.getMovieCost(movieId);
			addDetailRow(tab, R.string.labelValueSale, Util.amount(movieValue), R.string.labelCostSoFar, Util.amount(movieCost));

			// Button abort, sell, rent / rental conditions
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

			if (movieValue == 0) {

				// empty text to move button to second column
				TextView tv = new TextView(this);
				tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
				tv.setText("");
				tr.addView(tv);

				Button b = new Button(this);
				b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
				b.setText(R.string.labelAbortMovie);
				b.setPadding(5, 4, 5, 4);
				b.setTag(MovieAction.ABORT);
				b.setBackgroundResource(R.drawable.button_red);
				b.setOnClickListener(this);
				tr.addView(b);
			}
			else {
				// estimate finishing cost
				int appFinishCost = MovieService.movieFinishCostAvg(mpr.getType());
				addDetailRow(tab,
						R.string.display_msg_movie_finish_cost,
						getResources().getString(R.string.labelApproximately) + " "
								+ Util.amount(appFinishCost),
						R.string.labelTotalCost,
						getResources().getString(R.string.labelApproximately) + " "
								+ Util.amount(movieCost + appFinishCost));

				Button b = new Button(this);
				b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
				b.setText(R.string.labelSell);
				b.setPadding(5, 4, 5, 4);
				b.setWidth(100);
				b.setTag(MovieAction.SELL);
				b.setBackgroundResource(R.drawable.button_green);
				b.setOnClickListener(this);
				tr.addView(b);

				b = new Button(this);
				b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
				b.setText(R.string.labelRent);
				b.setPadding(5, 4, 5, 4);
				b.setWidth(100);
				b.setTag(MovieAction.RENT);
				b.setBackgroundResource(R.drawable.button_blue);
				b.setOnClickListener(this);
				tr.addView(b);

				TextView tv = new TextView(this);
				tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
				tv.setPadding(5,  5,  5,  5);
				tv.setText(getResources().getString(R.string.labelRentalConditions) + " " + Util.amount(movieValue / 5));
				tr.addView(tv);
			}

			tab.addView(tr);
		}
		else if (mpr.getStatus() == MovieStatus.SOLD) {
			addDetailRow(tab, R.string.labelValueSale, Util.amount(mpr.getPrice()), R.string.labelCost, Util.amount(MovieService.getMovieCost(movieId)));
		}
		else if (mpr.getStatus() == MovieStatus.RENTAL) {
			addDetailRow(tab, R.string.labelInRentalFor, Util.amount(mpr.getPrice() / 5), R.string.labelCost, Util.amount(MovieService.getMovieCost(movieId)));
		}

	}

	public void refresh() {

		TableRow tr = new TableRow(this);
		tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

		TextView tv = new TextView(this);
		tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		tv.setGravity(Gravity.CENTER);
		tv.setText("");
		tr.addView(tv);

		SparseArray<Set<MovieModel>> movieModels = new SparseArray<Set<MovieModel>>();
		for (MovieModel mm : MovieService.getModelsForMovie(movieId, 0)) {

			Set<MovieModel> modelSet = movieModels.get(mm.getDay());
			if (modelSet == null) {
				modelSet = new TreeSet<MovieModel>();
				movieModels.put(mm.getDay(), modelSet);
			}
			modelSet.add(mm);

			modelSet = movieModels.get(0);
			if (modelSet == null) {
				modelSet = new TreeSet<MovieModel>();
				movieModels.put(0, modelSet);
			}
			modelSet.add(mm);
		}

		TableLayout tab = (TableLayout) findViewById(R.id.tableMovieCast);
		tab.removeAllViews();

		boolean addButtonShown = false;
		if (movieModels.size() > 0) {
			// image row
			for (MovieModel mm : movieModels.get(0)) {
				Model model = ModelService.getModelById(mm.getModelId());
				tr.addView(addMovieModel(model));
			}
			tab.addView(tr);

			// payment rows for each day
			for (int i = 0; i < movieModels.size(); i++) {

				int day = movieModels.keyAt(i);
				if (day == 0) continue;

				tr = new TableRow(this);
				tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

				tv = new TextView(this);
				tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
				tv.setGravity(Gravity.CENTER);
				tv.setText(String.format("#%d", day));
				tr.addView(tv);

				for (MovieModel mm : movieModels.get(0)) {
					// lookup model in current column position
					boolean found = false;
					for (MovieModel mm2 : movieModels.valueAt(i)) {
						if (mm2.getModelId() == mm.getModelId()) {
							tv = new TextView(this);
							tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
							tv.setGravity(Gravity.CENTER);
							if (mm2.getPrice() > 0) {
								tv.setText(Util.amount(mm2.getPrice()));
							}
							else {
								tv.setText(getResources().getString(R.string.model_state_sick));
								if (day <= DiaryService.today()) tv.setTextColor(Color.RED);
							}

							// modification link for future entries
							if (day > DiaryService.today()) {
								tv.setTextColor(0xff0000ff);
								tv.setTag(mm2);
								tv.setOnClickListener(this);
							}

							tr.addView(tv);
							found = true;
							break;
						}
					}
					if (!found) {
						tv = new TextView(this);
						tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
						tv.setText("");
						tr.addView(tv);
					}
				}

				// add "+" Button
				if (day > DiaryService.today()) {
					Button b = new Button(this);
					b.setText("+");
					b.setPadding(2, 2, 2, 2);
					b.setWidth(40);
					b.setBackgroundResource(R.drawable.button_blue);
					b.setTag(MovieAction.ADD_MODEL);
					b.setOnClickListener(this);
					tr.addView(b);
					addButtonShown = true;
				}

				tab.addView(tr);
			}
		}

		if (!addButtonShown) {
			tr = new TableRow(this);
			tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

			// add "add" Button
			Button b = new Button(this);
			b.setText("+");
			b.setPadding(2, 2, 2, 2);
			b.setWidth(40);
			b.setBackgroundResource(R.drawable.button_blue);
			b.setTag(MovieAction.ADD_MODEL);
			b.setOnClickListener(this);
			tr.addView(b);

			tab.addView(tr);
		}
	}

	private View addMovieModel(Model model) {
		LinearLayout frame = new LinearLayout(this);
		frame.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		frame.setOrientation(LinearLayout.VERTICAL);
		frame.setPadding(5, 5, 5, 5);

		ImageView iv = new ImageView(this);
		iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		int ir = getResources().getIdentifier(model.getImage(), "drawable", getPackageName());
		iv.setImageResource(ir);
		iv.setTag(model);
		iv.setOnClickListener(this);
		frame.addView(iv);

		TextView tv = new TextView(this);
		tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		tv.setGravity(Gravity.CENTER);
		tv.setText(model.getFullname().replace(' ', '\n'));
		frame.addView(tv);

		return frame;
	}

	private void addDetailRow(TableLayout tab, int label1Id, String value1, int label2Id, String value2) {

		// Movie value, cost
		TableRow tr = new TableRow(this);
		tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

		TextView tv = new TextView(this);
		tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		tv.setPadding(5,  5,  5,  5);
		tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
		tv.setText(label1Id);
		tr.addView(tv);

		tv = new TextView(this);
		tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		tv.setPadding(5,  5,  5,  5);
		tv.setTextSize(22);
		tv.setText(value1);
		tr.addView(tv);

		tv = new TextView(this);
		tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		tv.setPadding(5,  5,  5,  5);
		tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
		tv.setText(label2Id);
		tr.addView(tv);

		tv = new TextView(this);
		tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
		tv.setPadding(5,  5,  5,  5);
		tv.setTextSize(22);
		tv.setText(value2);
		tr.addView(tv);

		tab.addView(tr);
	}

	@Override
	public void onClick(View v) {

		if (v instanceof Button) {
			switch((MovieAction)v.getTag()) {
			case ADD_MODEL:
				// '+' Button
				Intent intent = new Intent(this, MovieModelSelectDialog.class);
				intent.putExtra(MovieModelSelectDialog.EXTRA_MOVIE_ID, movieId);
				intent.putExtra(MovieModelSelectDialog.EXTRA_MODE, MovieModelSelectDialog.MODE_ADD);
				startActivityForResult(intent, 1);
				break;
			case ABORT:
				MovieService.abortMovie(movieId);
				finish();
				break;
			case SELL:
				MovieService.sellMovie(movieId);
				finish();
				break;
			case RENT:
				MovieService.rentMovie(movieId);
				finish();
				break;
			}
		}
		else if (v instanceof TextView) {
			// amount
			MovieModel mm = (MovieModel)v.getTag();
			Intent intent = new Intent(this, MovieModelSelectDialog.class);
			intent.putExtra(MovieModelSelectDialog.EXTRA_MOVIE_ID, movieId);
			intent.putExtra(MovieModelSelectDialog.EXTRA_MODEL_ID, mm.getModelId());
			intent.putExtra(MovieModelSelectDialog.EXTRA_PRICE, mm.getPrice());
			intent.putExtra(MovieModelSelectDialog.EXTRA_MODE, MovieModelSelectDialog.MODE_MODIFY);
			startActivityForResult(intent, 1);
		}
		else if (v instanceof ImageView) {
			// model
			Model model = (Model)v.getTag();
			Intent intent = new Intent(this, ModelNegotiationDialog.class);
			intent.putExtra(ModelNegotiationDialog.EXTRA_MODEL_ID, model.getId());
			startActivity(intent);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		refresh();
	}

	private static enum MovieAction {
		ADD_MODEL,
		ABORT,
		SELL,
		RENT;
	}
}
