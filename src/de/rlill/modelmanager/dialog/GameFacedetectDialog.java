package de.rlill.modelmanager.dialog;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.service.ModelService;

public class GameFacedetectDialog extends Activity implements View.OnClickListener {

	private static final String LOG_TAG = "MM*" + GameFacedetectDialog.class.getSimpleName();

	private int guess;
	private int firstbet;
	private int bet;
	private int winsum;
	public final static String EXTRA_BET = "game.facedetect.bet";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_game_facedetect_dialog);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		StatusBarFragmentAdapter.initStatusBar(findViewById(R.id.status_bar_include));

		Intent intent = getIntent();
		firstbet = intent.getIntExtra(EXTRA_BET, 0);
		bet = firstbet;

		// TODO: what if bet < 1 ?


		newGame();
	}

	private void newGame() {

		List<Model> myModels = ModelService.getHiredModels();

		// TODO: what if there are less than MIN models? (10?)


		int[] mix = Util.randomArray(myModels.size());

		guess = mix[Util.rnd(3)];
		Model model = ModelService.getModelById(guess);
		TextView tv = (TextView)findViewById(R.id.textViewName);
		tv.setText(model.getFullname());

		model = ModelService.getModelById(mix[0]);
		int ir = getResources().getIdentifier(model.getImage(), "drawable", getPackageName());
		ImageView iv = (ImageView)findViewById(R.id.imageViewFace1);
		iv.setImageResource(ir);
		iv.setTag(Integer.valueOf(model.getId()));
		iv.setOnClickListener(this);

		model = ModelService.getModelById(mix[1]);
		ir = getResources().getIdentifier(model.getImage(), "drawable", getPackageName());
		iv = (ImageView)findViewById(R.id.imageViewFace2);
		iv.setImageResource(ir);
		iv.setTag(Integer.valueOf(model.getId()));
		iv.setOnClickListener(this);

		model = ModelService.getModelById(mix[2]);
		ir = getResources().getIdentifier(model.getImage(), "drawable", getPackageName());
		iv = (ImageView)findViewById(R.id.imageViewFace3);
		iv.setImageResource(ir);
		iv.setTag(Integer.valueOf(model.getId()));
		iv.setOnClickListener(this);

		tv = (TextView)findViewById(R.id.textViewDescription);
		tv.setText(R.string.descriptionGameFacedetect);

		tv = (TextView)findViewById(R.id.textViewCondition);
		tv.setText(getResources().getString(R.string.descriptionGameConditions,
				Util.amount(winsum), Util.amount(bet), Util.amount(bet)));
	}

	@Override
	public void onClick(View v) {

		Integer t = (Integer)v.getTag();
		if (t == null) return;
		Log.i(LOG_TAG, String.format("correct: %d, guessed: %d", guess, t));

		if (t == guess) {
			winsum += bet;
			bet += firstbet;
			newGame();
		}
		else {
			finish();
		}
	}

}
