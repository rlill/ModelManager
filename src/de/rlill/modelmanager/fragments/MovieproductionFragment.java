package de.rlill.modelmanager.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.adapter.MovieproductionListAdapter;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.dialog.MovieproductionDialog;
import de.rlill.modelmanager.model.Movieproduction;
import de.rlill.modelmanager.service.DiaryService;
import de.rlill.modelmanager.service.MovieService;
import de.rlill.modelmanager.struct.MovieType;
import de.rlill.modelmanager.struct.MovieTypeOption;
import de.rlill.modelmanager.struct.Weekday;

public class MovieproductionFragment
		extends Fragment
		implements OnItemClickListener, OnClickListener, OnItemSelectedListener {

	private static final String LOG_TAG = "MM*" + MovieproductionFragment.class .getSimpleName();

	private View fragmentView;
    private List<Movieproduction> listItems;
    private ArrayAdapter<Movieproduction> productionAdapter;
	private ArrayAdapter<MovieTypeOption> movieTypeAdapter;
	private ArrayAdapter<String> startDayAdapter;
	private Context ctx;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		fragmentView = inflater.inflate(R.layout.fragment_movie_production, container, false);
		ctx = container.getContext();

		StatusBarFragmentAdapter.initStatusBar(fragmentView);

		Button b = (Button)fragmentView.findViewById(R.id.button1);
		b.setOnClickListener(this);

		// start day
		Spinner sp = (Spinner)fragmentView.findViewById(R.id.spinnerStartDay);

		List<String> startDayList = new ArrayList<String>();
		String sday = getResources().getString(R.string.display_day_si);
		for (int day = DiaryService.today() + 1; day < DiaryService.today() + 14; day++) {
			if (Util.weekday(day) == Weekday.SATURDAY || Util.weekday(day) == Weekday.SUNDAY) continue;
			startDayList.add(String.format("%s %d (%s)",
					sday, day, Util.weekday(day).getName().substring(0, 2)));
		}
		startDayAdapter = new ArrayAdapter<String>(ctx,
				android.R.layout.simple_spinner_item, startDayList);
		sp.setAdapter(startDayAdapter);

		// movie types
		sp = (Spinner)fragmentView.findViewById(R.id.spinnerMovieType);
		sp.setOnItemSelectedListener(this);

		List<MovieTypeOption> trainingList = new ArrayList<MovieTypeOption>();
		for (MovieType mt : MovieType.values()) {
			trainingList.add(new MovieTypeOption(mt));
		}
		movieTypeAdapter = new ArrayAdapter<MovieTypeOption>(ctx,
				android.R.layout.simple_spinner_item, trainingList);
		sp.setAdapter(movieTypeAdapter);


		listItems = new ArrayList<Movieproduction>();

		productionAdapter = new MovieproductionListAdapter(
				container.getContext(),
				android.R.layout.simple_list_item_1,
				R.id.listView1,
				listItems,
				inflater);

		ListView listView = (ListView) fragmentView.findViewById(R.id.listMovieproductions);
		listView.setAdapter(productionAdapter);
		listView.setOnItemClickListener(this);

		return fragmentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		StatusBarFragmentAdapter.initStatusBar(fragmentView);

		listItems.clear();
		listItems.addAll(MovieService.listAllMovies());
		productionAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// tap on item in movie list
		Movieproduction mpr = productionAdapter.getItem(position);

		Intent intent = new Intent(parent.getContext(), MovieproductionDialog.class);
		intent.putExtra(MovieproductionDialog.EXTRA_MOVIE_ID, mpr.getId());
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		// button create

		Spinner sp = (Spinner)fragmentView.findViewById(R.id.spinnerStartDay);
		String startDayStr = (String) sp.getSelectedItem();
		startDayStr = startDayStr.replaceAll("\\D", "");
		int startDay = Util.atoi(startDayStr);
		if (startDay <= DiaryService.today()) return;
		sp.setSelection(0);

		EditText et = (EditText)fragmentView.findViewById(R.id.editTextMovieName);
		String name = et.getText().toString();
		if (name == null || name.length() < 1) return;
		et.setText("");

		sp = (Spinner)fragmentView.findViewById(R.id.spinnerMovieType);
		MovieTypeOption mto = (MovieTypeOption) sp.getSelectedItem();
		if (mto == null) return;
		sp.setSelection(0);

		MovieService.startMovie(name, mto.getType(), startDay);

		onResume();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View v, int position, long arg3) {
		MovieTypeOption mto = movieTypeAdapter.getItem(position);

		TextView tv = (TextView)fragmentView.findViewById(R.id.textViewPrice);
		StringBuilder sb = new StringBuilder();
		// initiation cost
		sb.append(ctx.getResources().getString(R.string.display_msg_movie_start_cost))
			.append(": ").append(getResources().getString(R.string.labelApproximately))
			.append(" ").append(Util.amount(MovieService.movieStartCostAvg(mto.getType()))).append('\n');
		// daily production cost (+ model payments)
		sb.append(ctx.getResources().getString(R.string.display_msg_movie_daily_cost))
			.append(": ").append(getResources().getString(R.string.labelApproximately))
			.append(" ").append(Util.amount(MovieService.movieProgressCostAvg(mto.getType()))).append('\n');
		sb.append(ctx.getResources().getString(R.string.display_msg_movie_model_cost))
			.append('\n');
		// finishing cost
		sb.append(ctx.getResources().getString(R.string.display_msg_movie_finish_cost))
			.append(": ").append(getResources().getString(R.string.labelApproximately))
			.append(" ").append(Util.amount(MovieService.movieFinishCostAvg(mto.getType()))).append('\n');
		tv.setText(sb.toString());
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

}
