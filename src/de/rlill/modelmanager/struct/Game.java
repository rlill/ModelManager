package de.rlill.modelmanager.struct;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.service.ModelService;

public abstract class Game {

	private static final int MIN_MODELS_FOR_GAME = 10;
	private static final int GUESSING_POSITIONS = 5;
	public final static String EXTRA_BET = "game.facedetect.bet";

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

	public abstract void registerControls(Activity view, View.OnClickListener listener);

	public abstract void refreshDisplay(Activity view);

	public abstract void newRound();

	public abstract boolean guess(int g);

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
