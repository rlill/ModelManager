package de.rlill.modelmanager.adapter;

import java.util.List;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.dialog.ModelNegotiationDialog;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.Team;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.struct.ViewElements;

public class TeamListAdapter extends ArrayAdapter<Team> {

	private static final String LOG_TAG = "MM*" + TeamListAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private Context context;
    private OnClickListener clickListener;

    Drawable enterShape;
    Drawable normalShape;

    public TeamListAdapter(Context context, int resource, int textViewResourceId,
			List<Team> objects, LayoutInflater inflater,
			OnClickListener listener) {
		super(context, resource, textViewResourceId, objects);
        this.inflater = inflater;
        this.context = context;
        clickListener = listener;

        enterShape = context.getResources().getDrawable(R.drawable.shape_droptarget);
        normalShape = context.getResources().getDrawable(R.drawable.shape);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//data from your adapter
		Team team = getItem(position);
		TeamListViewElements viewElements = null;

		// reuse already constructed row views...
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.fragment_team_item, null);
			viewElements = new TeamListViewElements(convertView);
			convertView.setTag(viewElements);
        }
		else {
			viewElements = (TeamListViewElements) convertView.getTag();
		}

		viewElements.setContextInt(team.getId());

		updateListElement(convertView, team);

        LinearLayout ll = (LinearLayout)convertView.findViewById(R.id.teamImageList);
        ll.setOnClickListener(clickListener);
        ll.setTag(viewElements);

		return convertView;
	}

	public void updateListElement(View v, Team team) {

		TeamListViewElements viewElements = (TeamListViewElements)v.getTag();
		viewElements.clearTeamMembers();

		if (team.getId() == ModelService.TEAM_NO_TEAM)
			viewElements.setDescription(context.getResources().getString(R.string.labelNoTeam));
		else
			viewElements.setDescription(context.getResources().getString(R.string.labelTeam)
					+ " " + ModelService.getTeamName(team.getId()));

		// 1st and 2nd leader
		Model model = ModelService.getModelById(team.getLeader1());
		if (model != null) viewElements.addTeamMember(model);
		model = ModelService.getModelById(team.getLeader2());
		if (model != null) viewElements.addTeamMember(model);

		for (Model m : ModelService.getTeamMembers(team.getId())) {
			if (m.getId() != team.getLeader1() && m.getId() != team.getLeader2())
				viewElements.addTeamMember(m);
		}
	}

	private class TeamMemberViewElements extends ViewElements {
		public TeamMemberViewElements(View view) {
			super(view);
		}
	}

	private class TeamListViewElements extends ViewElements {

        private int teamId = 0;

    	public TeamListViewElements(View view) {
        	super(view);
    	}

    	public void setDescription(String description) {
    		findTextView(R.id.textViewTeamName).setText(description);
    	}

    	public void clearTeamMembers() {
    		LinearLayout ll = (LinearLayout) adaptedView.findViewById(R.id.teamImageList);
    		ll.removeAllViews();

    		ImageView iv = new ImageView(context);
    		iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    	    iv.setImageResource(R.drawable.arrow);
    		iv.setPadding(15, 25, 15, 25);
    	    ll.addView(iv);
    	}

    	public void addTeamMember(Model model) {
            // remember team id
            teamId = model.getTeamId();

            // paint frame with image and name
    		LinearLayout frame = new LinearLayout(context);
    		frame.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		frame.setOrientation(LinearLayout.VERTICAL);
    		frame.setPadding(5, 5, 5, 5);

            frame.setOnTouchListener(new TeamTouchListener());
            frame.setTag(model);

            ImageView iv = new ImageView(context);
    		iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		int ir = context.getResources().getIdentifier(model.getImage(), "drawable", context.getPackageName());
    	    iv.setImageResource(ir);

    	    ViewElements viewElements = new TeamMemberViewElements(iv);
    		viewElements.setContextModel(model);
    		iv.setTag(viewElements);

    	    //android:onClick="modelViewDetails"
    	    iv.setOnClickListener(new OnClickListener() {
    	        @Override
    	        public void onClick(View v) {
    	    		ViewElements ve = (ViewElements)v.getTag();
    	    		if (ve != null) {
    	    			Model model = ve.getContextModel();
    	    			if (model != null && model.getId() > 0) {
    	    				Intent intent = new Intent(context, ModelNegotiationDialog.class);
    	    			    intent.putExtra(ModelNegotiationDialog.EXTRA_MODEL_ID, model.getId());
    	    			    context.startActivity(intent);
    	    			}
    	    		}
    	        }
    	    });
    	    frame.addView(iv);

    		TextView tv = new TextView(context);
    		tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    		tv.setGravity(Gravity.CENTER);
    		tv.setText(model.getFullname().replace(' ', '\n'));
    		frame.addView(tv);

            LinearLayout ll = (LinearLayout) adaptedView.findViewById(R.id.teamImageList);
            ll.addView(frame);

            View scroller = (HorizontalScrollView)adaptedView.findViewById(R.id.teamImageScroll);
            scroller.setOnDragListener(new TeamDragListener(scroller, ll));

    	}

        public int getTeamId() {
            return teamId;
        }
    }


    private final class TeamTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    class TeamDragListener implements OnDragListener {

        private View scroller;
        private LinearLayout container;
        public TeamDragListener(View sc, LinearLayout ll) {
            scroller = sc;
            container = ll;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    scroller.setBackgroundDrawable(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    scroller.setBackgroundDrawable(normalShape);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();

                    // check whether container is valid
                    if (!(view.getTag() instanceof Model)) return false;

                    TeamListViewElements tve = (TeamListViewElements)container.getTag();
                    int tteam = tve.getTeamId();
                    Model model = (Model)view.getTag();
                    int mid = model.getId();

                    Log.i(LOG_TAG, "Assign model " + model.getFullname() + " #" + mid + " to team #" + tteam);

                    // transfer icon
                    owner.removeView(view);
                    container.addView(view);
                    view.setVisibility(View.VISIBLE);

                    // assignment to new team
                    ModelService.setTeam(mid, tteam);

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    // make item visible again if dropped outside a valid target view
                    view = (View) event.getLocalState();
                    view.setVisibility(View.VISIBLE);
                    scroller.setBackgroundDrawable(normalShape);
            }
            return true;
        }
    }

}
