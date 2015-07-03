package de.rlill.modelmanager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.adapter.ModelSpinnerAdapter;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.adapter.TeamListAdapter;
import de.rlill.modelmanager.adapter.TeamListAdapter.TeamListViewElements;
import de.rlill.modelmanager.model.Diary;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.Team;
import de.rlill.modelmanager.model.Today;
import de.rlill.modelmanager.persistance.DiaryDbAdapter;
import de.rlill.modelmanager.persistance.TeamDbAdapter;
import de.rlill.modelmanager.persistance.TodayDbAdapter;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.ViewElements;

public class TeamFragment extends Fragment implements OnItemClickListener, View.OnClickListener,
		AdapterView.OnItemSelectedListener {

	private static final String LOG_TAG = "MM*" + TeamFragment.class.getSimpleName();
    private List<Team> listItems;
    private TeamListAdapter adapter;
    private View fragmentView;
    private LayoutInflater inflater;
    private Context context;
    private Team selectedTeam;
    private View selectedView;
	private TeamListViewElements currentTeamListViewElements;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		this.inflater = inflater;
		this.context = container.getContext();

		// Inflate the layout for this fragment
		fragmentView = inflater.inflate(R.layout.fragment_team_list, container, false);

        listItems = new ArrayList<Team>();

        adapter = new TeamListAdapter(
        		container.getContext(),
        		android.R.layout.simple_list_item_1,
        		R.id.listTeams,
        		listItems,
        		inflater,
        		this);

        ListView listView = (ListView)fragmentView.findViewById(R.id.listTeams);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(this);

        StatusBarFragmentAdapter.initStatusBar(fragmentView);

		Spinner sp = (Spinner)fragmentView.findViewById(R.id.selectTeamLeader);
		sp.setOnItemSelectedListener(this);
		sp = (Spinner)fragmentView.findViewById(R.id.selectTeamLeaderSubst);
		sp.setOnItemSelectedListener(this);

		// TL Bonus
		EditText et = (EditText)fragmentView.findViewById(R.id.editTextTeamLeaderBonus);
		et.addTextChangedListener(mTextEditorWatcher);

		return fragmentView;
	}

    @Override
    public void onResume() {
        super.onResume();
	    listItems.clear();
	    listItems.addAll(ModelService.getAllTeams());
	    Team noTeamTeam = new Team(ModelService.TEAM_NO_TEAM);
	    listItems.add(noTeamTeam);
	    Log.d(LOG_TAG, "Displaying " + listItems.size() + " teams");
        adapter.notifyDataSetChanged();
    }

	public void updateCurrentTeamDetails() {
		if (selectedView != null) onItemClick(null, selectedView, 0, 0);
	}

	@Override
	public void onItemClick(android.widget.AdapterView<?> parent, View v, int position, long id) {
		currentTeamListViewElements = (TeamListViewElements)v.getTag();
		if (currentTeamListViewElements == null) return;
		int teamId = currentTeamListViewElements.getContextInt();
		if (teamId == ModelService.TEAM_NO_TEAM) return;
		selectedTeam = ModelService.getTeam(teamId);
		selectedView = v;

        Spinner sp = (Spinner)fragmentView.findViewById(R.id.selectTeamLeader);
		List<Model> substList = new ArrayList<Model>();
		substList.add(ModelService.getModelById(ModelService.UNDEFINED_MODEL));
		substList.addAll(ModelService.getTeamMembers(teamId));
		ArrayAdapter<Model> replacementAdapter = new ModelSpinnerAdapter(
				context,
        		R.layout.fragment_model_spinner_item,
        		R.id.textView1,
        		substList,
        		inflater,
        		EventFlag.NEWDAY);
		sp.setAdapter(replacementAdapter);

		Model model = ModelService.getModelById(selectedTeam.getLeader1());
		int p = substList.indexOf(model);
		if (p < 0) p = 0;
		sp.setSelection(p);

		sp = (Spinner)fragmentView.findViewById(R.id.selectTeamLeaderSubst);
		replacementAdapter = new ModelSpinnerAdapter(
				context,
				R.layout.fragment_model_spinner_item,
				R.id.textView1,
				substList,
				inflater,
				EventFlag.NEWDAY);
		sp.setAdapter(replacementAdapter);

		model = ModelService.getModelById(selectedTeam.getLeader2());
		p = substList.indexOf(model);
		if (p < 0) p = 0;
		sp.setSelection(p);

		// TL Bonus
		EditText et = (EditText)fragmentView.findViewById(R.id.editTextTeamLeaderBonus);
		et.setText(Integer.toString(selectedTeam.getBonus()));

		fillStatistics();
	}

	private void fillStatistics() {

		List<Model> substList = ModelService.getTeamMembers(selectedTeam.getId());

		// finished team bookings
		int count_ph = 0;
		int count_mv = 0;
		int amount_ph = 0;
		int amount_mv = 0;
		for (Diary diary : DiaryDbAdapter.listEventsForToday()) {
			if (diary.getEventClass() != EventClass.BOOKING) continue;
			boolean thisTeam = false;
			for (Model m : substList) if (m.getId() == diary.getModelId()) thisTeam = true;
			if (!thisTeam) continue;
			switch (diary.getEventFlag()) {
			case PHOTO:
				count_ph++;
				amount_ph += diary.getAmount();
				break;
			case MOVIE:
				count_mv++;
				amount_mv += diary.getAmount();
				break;
			default:
				Log.w(LOG_TAG, "Diary BOOKING event " + diary.getEventFlag() + ": #" + diary.getId());
			}
		}

		TextView tv = (TextView)fragmentView.findViewById(R.id.textViewTeamPhotoBooked);
		tv.setText(String.format("%d\n%s", count_ph, Util.amount(amount_ph)));

		tv = (TextView)fragmentView.findViewById(R.id.textViewTeamMovieBooked);
		tv.setText(String.format("%d\n%s", count_mv, Util.amount(amount_mv)));

		// open team requests
		count_ph = 0;
		count_mv = 0;
		amount_ph = 0;
		amount_mv = 0;
		for (Today today : TodayDbAdapter.getAllEvents()) {
			if (today.getEvent().getEclass() != EventClass.BOOKING) continue;
			boolean thisTeam = false;
			for (Model m : substList) if (m.getId() == today.getModelId()) thisTeam = true;
			if (!thisTeam) continue;
			switch (today.getEvent().getFlag()) {
			case PHOTO:
				count_ph++;
				amount_ph += today.getAmount2();
				break;
			case MOVIE:
				count_mv++;
				amount_mv += today.getAmount2();
				break;
			default:
				Log.w(LOG_TAG, "Today BOOKING event " + today.getEvent().getFlag() + ": #" + today.getId());
			}
		}

		tv = (TextView)fragmentView.findViewById(R.id.textViewTeamPhotoPipeline);
		tv.setText(String.format("%d\n%s", count_ph, Util.amount(amount_ph)));

		tv = (TextView)fragmentView.findViewById(R.id.textViewTeamMoviePipeline);
		tv.setText(String.format("%d\n%s", count_mv, Util.amount(amount_mv)));

		// open other requests
		count_ph = 0;
		count_mv = 0;
		amount_ph = 0;
		amount_mv = 0;
		for (Today today : TodayDbAdapter.getAllEvents()) {
			if (today.getEvent().getEclass() != EventClass.BOOKING) continue;
			if (today.getModelId() != ModelService.UNDEFINED_MODEL) continue;
			switch (today.getEvent().getFlag()) {
			case PHOTO:
				count_ph++;
				amount_ph += today.getAmount2();
				break;
			case MOVIE:
				count_mv++;
				amount_mv += today.getAmount2();
				break;
			default:
				Log.w(LOG_TAG, "Today BOOKING event " + today.getEvent().getFlag() + ": #" + today.getId());
			}
		}

		tv = (TextView)fragmentView.findViewById(R.id.textViewRequestPhotoPipeline);
		tv.setText(String.format("%d\n%s", count_ph, Util.amount(amount_ph)));

		tv = (TextView)fragmentView.findViewById(R.id.textViewRequestMoviePipeline);
		tv.setText(String.format("%d\n%s", count_mv, Util.amount(amount_mv)));

	}

	@Override
	public void onClick(View v) {
		if (v instanceof LinearLayout) {
			onItemClick(null, v, 0, 0);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Spinner sp = (Spinner)fragmentView.findViewById(R.id.selectTeamLeader);
		Model m = (Model) sp.getSelectedItem();
		if (selectedTeam.getLeader1() != m.getId()) {
			Log.i(LOG_TAG, "1s:" + m.getFullname());
			selectedTeam.setLeader1(m.getId());
			TeamDbAdapter.updateTeam(selectedTeam);
			ModelService.teamwork(selectedTeam);
			fillStatistics();
			adapter.updateListElement(currentTeamListViewElements, selectedTeam);
		}
		sp = (Spinner) fragmentView.findViewById(R.id.selectTeamLeaderSubst);
		m = (Model) sp.getSelectedItem();
		if (selectedTeam.getLeader2() != m.getId()) {
			Log.i(LOG_TAG, "2s:" + m.getFullname());
			selectedTeam.setLeader2(m.getId());
			TeamDbAdapter.updateTeam(selectedTeam);
			ModelService.teamwork(selectedTeam);
			fillStatistics();
			adapter.updateListElement(currentTeamListViewElements, selectedTeam);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	private final TextWatcher mTextEditorWatcher = new TextWatcher() {

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		public void afterTextChanged(Editable s) {
//			Log.d(LOG_TAG, "afterTextChanged: " + s.toString());
			int bonus = Util.atoi(s.toString());
			if (selectedTeam != null && bonus != selectedTeam.getBonus()) {
				Log.i(LOG_TAG, "bonus:" + bonus);
				selectedTeam.setBonus(bonus);
				TeamDbAdapter.updateTeam(selectedTeam);
			}
		}
	};
}
