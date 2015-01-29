package de.rlill.modelmanager.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.struct.Game;
import de.rlill.modelmanager.struct.GameNameDetect;

public class GameNamedetectDialog extends Activity implements View.OnClickListener {

	private static final String LOG_TAG = "MM*" + GameNamedetectDialog.class.getSimpleName();
	public final static String EXTRA_BET = "game.facedetect.bet";
	public final static String RESET = "game.facedetect.reset";

	private static Game game = null;

	public GameNamedetectDialog() {
		super();
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_game_namedetect_dialog);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		int bet = intent.getIntExtra(EXTRA_BET, 0);
		int reset = intent.getIntExtra(RESET, 0);

		if (game == null || reset == 1) game = new GameNameDetect(this, bet);
		intent.putExtra(RESET, 0);

		game.registerControls(this,  this);

		game.refreshDisplay(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonPlay1:
			game.guess(0);
			break;
		case R.id.buttonPlay2:
			game.guess(1);
			break;
		case R.id.buttonPlay3:
			game.guess(2);
			break;
		case R.id.buttonPlay4:
			game.guess(3);
			break;
		case R.id.buttonPlay5:
			game.guess(4);
			break;
		case R.id.buttonOk:
			game.newRound();
		}
		game.refreshDisplay(this);
	}
}
