package de.rlill.modelmanager.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.service.TransactionService;

public class GameNamedetectDialog extends Activity implements View.OnClickListener {

	private static final String LOG_TAG = "MM*" + GameNamedetectDialog.class.getSimpleName();
	private static final int MIN_MODELS_FOR_GAME = 10;

	private int rightAnswer;
	private int firstbet;
	private int bet;
	private int winsum;
	private PlayMode playmode;
	private List<Model> allModels;
	private List<GambleItem> gambleItems;

	public final static String EXTRA_BET = "game.facedetect.bet";


	public GameNamedetectDialog() {
		super();
		gambleItems = new ArrayList<GambleItem>();
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_game_namedetect_dialog);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		StatusBarFragmentAdapter.initStatusBar(findViewById(R.id.status_bar_include));

		allModels = ModelService.getHiredModels();
		if (allModels.size() < MIN_MODELS_FOR_GAME) return;

		GambleItem gi = new GambleItem();
		gi.button = (Button)findViewById(R.id.buttonPlay1);
		gi.button.setOnClickListener(this);
		gi.image = (ImageView)findViewById(R.id.imageViewFace1);
		gambleItems.add(gi);

		gi = new GambleItem();
		gi.button = (Button)findViewById(R.id.buttonPlay2);
		gi.button.setOnClickListener(this);
		gi.image = (ImageView)findViewById(R.id.imageViewFace2);
		gambleItems.add(gi);

		gi = new GambleItem();
		gi.button = (Button)findViewById(R.id.buttonPlay3);
		gi.button.setOnClickListener(this);
		gi.image = (ImageView)findViewById(R.id.imageViewFace3);
		gambleItems.add(gi);

		gi = new GambleItem();
		gi.button = (Button)findViewById(R.id.buttonPlay4);
		gi.button.setOnClickListener(this);
		gi.image = (ImageView)findViewById(R.id.imageViewFace4);
		gambleItems.add(gi);

		gi = new GambleItem();
		gi.button = (Button)findViewById(R.id.buttonPlay5);
		gi.button.setOnClickListener(this);
		gi.image = (ImageView)findViewById(R.id.imageViewFace5);
		gambleItems.add(gi);

		Button b = (Button)findViewById(R.id.buttonOk);
		b.setOnClickListener(this);

		Intent intent = getIntent();
		firstbet = intent.getIntExtra(EXTRA_BET, 0);
		bet = firstbet;

		newGame();
	}

	private void newGame() {

		int[] mix = Util.randomArray(allModels.size());

		rightAnswer = Util.rnd(gambleItems.size());
		int rightIndex = mix[rightAnswer];
		Model model = allModels.get(rightIndex);
		ImageView iv = (ImageView)findViewById(R.id.imageView1);
		int ir = getResources().getIdentifier(model.getImage(), "drawable", getPackageName());
		iv.setImageResource(ir);
		iv.setVisibility(View.VISIBLE);

		int i = 0;
		for (GambleItem gi : gambleItems) {
			gi.model = allModels.get(mix[i]);
			gi.button.setBackgroundResource(R.drawable.button_blue);
			gi.button.setText(gi.model.getFullname());
			gi.correct = (rightAnswer == i);
			gi.image.setVisibility(View.GONE);
			i++;
		}


		TextView tv = (TextView)findViewById(R.id.textViewDescription);
		tv.setText(R.string.descriptionGameNamedetect);

		tv = (TextView)findViewById(R.id.textViewCondition);
		tv.setText(getResources().getString(R.string.descriptionGameConditions,
				Util.amount(winsum), Util.amount(bet), Util.amount(bet)));

		Button b = (Button)findViewById(R.id.buttonOk);
		b.setVisibility(View.INVISIBLE);

		playmode = PlayMode.GUESS;
	}

	private void winSummary() {

		for (GambleItem gi : gambleItems) {
			if (gi.correct)
				gi.button.setBackgroundResource(R.drawable.button_green);
			int ir = getResources().getIdentifier(gi.model.getImage(), "drawable", getPackageName());
			gi.image.setImageResource(ir);
			gi.image.setVisibility(View.VISIBLE);
		}

		TextView tv = (TextView)findViewById(R.id.textViewDescription);
		tv.setText(R.string.descriptionGameWon);

		tv = (TextView)findViewById(R.id.textViewCondition);
		tv.setText(getResources().getString(R.string.descriptionGameContinue,
				Util.amount(winsum)));

		ImageView iv = (ImageView)findViewById(R.id.imageView1);
		iv.setVisibility(View.GONE);

		Button b = (Button)findViewById(R.id.buttonOk);
		b.setVisibility(View.VISIBLE);

		playmode = PlayMode.SOLVE;
	}

	private void failSummary(int guess) {

		int i = 0;
		for (GambleItem gi : gambleItems) {
			if (gi.correct)
				gi.button.setBackgroundResource(R.drawable.button_green);
			if (i == guess)
				gi.button.setBackgroundResource(R.drawable.button_red);
			int ir = getResources().getIdentifier(gi.model.getImage(), "drawable", getPackageName());
			gi.image.setImageResource(ir);
			gi.image.setVisibility(View.VISIBLE);
			i++;
		}

		ImageView iv = (ImageView)findViewById(R.id.imageView1);
		iv.setVisibility(View.GONE);

		TextView tv = (TextView)findViewById(R.id.textViewDescription);
		tv.setText(R.string.descriptionGameLost);

		tv = (TextView)findViewById(R.id.textViewCondition);
		tv.setText(getResources().getString(R.string.descriptionGameSummary,
				Util.amount(firstbet)));

		playmode = PlayMode.SOLVE;
	}

	@Override
	public void onClick(View v) {

		int guess = -1;
		switch (v.getId()) {
		case R.id.buttonPlay1:
			guess = 0;
			break;
		case R.id.buttonPlay2:
			guess = 1;
			break;
		case R.id.buttonPlay3:
			guess = 2;
			break;
		case R.id.buttonPlay4:
			guess = 3;
			break;
		case R.id.buttonPlay5:
			guess = 4;
			break;
		case R.id.buttonOk:
			newGame();
			return;
		}
		Log.d(LOG_TAG, "Guess: " + guess + "; right: " + rightAnswer);

		if (playmode == PlayMode.GUESS && guess == rightAnswer) {
			TransactionService.transfer(-1, 0, bet, getResources().getString(R.string.accountmessage_gambling));
			StatusBarFragmentAdapter.initStatusBar(findViewById(R.id.status_bar_include));
			winsum += bet;
			bet += firstbet;
			Log.d(LOG_TAG, "right, winsum: " + winsum + ", next bet: " + bet);
			winSummary();
		}
		else if (playmode == PlayMode.GUESS && guess > -1) {
			TransactionService.transfer(0, -1, winsum + firstbet, getResources().getString(R.string.accountmessage_gambling));
			StatusBarFragmentAdapter.initStatusBar(findViewById(R.id.status_bar_include));
			failSummary(guess);
		}
	}

	private class GambleItem {
		public ImageView image;
		public Button button;
		public Model model;
		public boolean correct;
		public String toString() {
			return "GambleItem [" + model + "] (" + (correct ? "right" : "wrong") + ")";
		}
	}

	private enum PlayMode {
		GUESS,
		SOLVE
	}
}
