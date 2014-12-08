package de.rlill.modelmanager.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.adapter.ModelSpinnerAdapter;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.adapter.TeamListAdapter;
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

public class TeamFragment extends Fragment implements OnItemClickListener, View.OnClickListener {

	private static final String LOG_TAG = "MM*" + TeamFragment.class.getSimpleName();
    private List<Team> listItems;
    private TeamListAdapter adapter;
    private View fragmentView;
    private LayoutInflater inflater;
    private Context context;
    private Team selectedTeam;

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

        Button b = (Button)fragmentView.findViewById(R.id.buttonSave);
        b.setOnClickListener(this);

        b = (Button)fragmentView.findViewById(R.id.buttonTeamwork);
        b.setOnClickListener(this);

        StatusBarFragmentAdapter.initStatusBar(fragmentView);

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

	@Override
	public void onItemClick(android.widget.AdapterView<?> parent, View v, int position, long id) {
		ViewElements ve = (ViewElements)v.getTag();
		if (ve == null) return;
		int teamId = ve.getContextInt();
		if (teamId == ModelService.TEAM_NO_TEAM) return;
		selectedTeam = ModelService.getTeam(teamId);

        Spinner sp = (Spinner)fragmentView.findViewById(R.id.selectTeamLeader);
		List<Model> substList = ModelService.getTeamMembers(teamId);
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

		// save ViewElements in context for updateListElement() call in onClick(save)
		Button b = (Button)fragmentView.findViewById(R.id.buttonSave);
		b.setTag(ve);

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
		else if (v.getId() == R.id.buttonTeamwork && selectedTeam != null) {
			// teamwork
			ModelService.teamwork(selectedTeam);
			fillStatistics();
		}
		else if (v.getId() == R.id.buttonSave && selectedTeam != null) {
			// save
			Spinner sp = (Spinner) fragmentView.findViewById(R.id.selectTeamLeader);
			Model m = (Model) sp.getSelectedItem();
			selectedTeam.setLeader1(m.getId());

			sp = (Spinner) fragmentView.findViewById(R.id.selectTeamLeaderSubst);
			m = (Model) sp.getSelectedItem();
			selectedTeam.setLeader2(m.getId());

			EditText et = (EditText) fragmentView.findViewById(R.id.editTextTeamLeaderBonus);
			selectedTeam.setBonus(Util.atoi(et.getText().toString()));

			TeamDbAdapter.updateTeam(selectedTeam);
			ModelService.teamwork(selectedTeam);

			adapter.updateListElement(v, selectedTeam);
		}
	}
}
