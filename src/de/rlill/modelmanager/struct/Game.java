package de.rlill.modelmanager.struct;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.service.TransactionService;

public abstract class Game {

	private static final String LOG_TAG = "MM*" + Game.class.getSimpleName();

	private static final int GUESSING_POSITIONS = 5;
	public final static String EXTRA_BET = "game.facedetect.bet";
	public final static int MIN_GAME_MODELS = 10;

	protected int firstbet;
	protected int bet;
	protected int rightAnswer;
	protected int winsum;
	protected int guess;
	protected PlayMode playmode;
	protected List<Model> allModels;
	protected GambleItem[] gambleItems;
	protected Context ctx;

	public Game(Context c, int b) {
		ctx = c;
		gambleItems = new GambleItem[GUESSING_POSITIONS];
		for (int i = 0; i < GUESSING_POSITIONS; i++) gambleItems[i] = new GambleItem();
		allModels = ModelService.getHiredModels();
		firstbet = bet = b;
	}

	public abstract void refreshDisplay(Activity view);

	public void registerControls(Activity dialog, View.OnClickListener listener) {
		GambleItem gi = gambleItems[0];
		gi.button = (Button)dialog.findViewById(R.id.buttonPlay1);
		gi.button.setOnClickListener(listener);
		gi.image = (ImageView)dialog.findViewById(R.id.imageViewFace1);

		gi = gambleItems[1];
		gi.button = (Button)dialog.findViewById(R.id.buttonPlay2);
		gi.button.setOnClickListener(listener);
		gi.image = (ImageView)dialog.findViewById(R.id.imageViewFace2);

		gi = gambleItems[2];
		gi.button = (Button)dialog.findViewById(R.id.buttonPlay3);
		gi.button.setOnClickListener(listener);
		gi.image = (ImageView)dialog.findViewById(R.id.imageViewFace3);

		gi = gambleItems[3];
		gi.button = (Button)dialog.findViewById(R.id.buttonPlay4);
		gi.button.setOnClickListener(listener);
		gi.image = (ImageView)dialog.findViewById(R.id.imageViewFace4);

		gi = gambleItems[4];
		gi.button = (Button)dialog.findViewById(R.id.buttonPlay5);
		gi.button.setOnClickListener(listener);
		gi.image = (ImageView)dialog.findViewById(R.id.imageViewFace5);

		Button b = (Button)dialog.findViewById(R.id.buttonOk);
		b.setOnClickListener(listener);
	}

	public void newRound() {
		int[] mix = Util.randomArray(allModels.size());
		rightAnswer = Util.rnd(gambleItems.length);

		int i = 0;
		for (GambleItem gi : gambleItems) {
			gi.correct = (rightAnswer == i);
			gi.model = allModels.get(mix[i]);
			i++;
		}
		playmode = PlayMode.GUESS;
	}

	public boolean guess(int g) {
		if (playmode != PlayMode.GUESS) return false;
		guess = g;
		if (g == rightAnswer) {
			TransactionService.transfer(-1, 0, bet, ctx.getResources().getString(R.string.accountmessage_gambling));
			winsum += bet;
			bet += firstbet;
			Log.d(LOG_TAG, "right, winsum: " + winsum + ", next bet: " + bet);
			playmode = PlayMode.SOLVE;
			return true;
		}

		// else
		TransactionService.transfer(0, -1, winsum + firstbet, ctx.getResources().getString(R.string.accountmessage_gambling));
		Log.d(LOG_TAG, "wrong, lost: " + (winsum + firstbet));
		playmode = PlayMode.LOST;
		return false;
	}

	public static class GambleItem {
		public ImageView image;
		public Button button;
		public Model model;
		public boolean correct;
		public String toString() {
			return "GambleItem [" + model + "] (" + (correct ? "right" : "wrong") + ")";
		}
	}

	public enum PlayMode {
		GUESS,
		SOLVE,
		LOST
	}

}
