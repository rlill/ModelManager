package de.rlill.modelmanager.struct;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.service.TransactionService;

public class GameNameDetect extends Game {

	private static final String LOG_TAG = "MM*" + GameNameDetect.class.getSimpleName();

	public GameNameDetect(Context c, int b) {
		super(c, b);
	}

	@Override
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

	@Override
	public void refreshDisplay(Activity dialog) {

		StatusBarFragmentAdapter.initStatusBar(dialog.findViewById(R.id.status_bar_include));

		if (playmode == null) newRound();

		Model model = null;
		int i = 0;
		for (GambleItem gi : gambleItems) {
			gi.button.setBackgroundResource(R.drawable.button_blue);
			gi.button.setText(gi.model.getFullname());
			if (playmode == PlayMode.GUESS) {
				gi.image.setVisibility(View.GONE);

			}
			else {
				if (gi.correct)
					gi.button.setBackgroundResource(R.drawable.button_green);
				else if (playmode == PlayMode.LOST && guess == i)
					gi.button.setBackgroundResource(R.drawable.button_red);

				int ir = ctx.getResources().getIdentifier(gi.model.getImage(), "drawable", ctx.getPackageName());
				gi.image.setImageResource(ir);
				gi.image.setVisibility(View.VISIBLE);
			}

			if (gi.correct) model = gi.model;
			i++;
		}

		ImageView iv = (ImageView)dialog.findViewById(R.id.imageView1);
		int ir = ctx.getResources().getIdentifier(model.getImage(), "drawable", ctx.getPackageName());
		iv.setImageResource(ir);
		iv.setVisibility((playmode == PlayMode.GUESS) ? View.VISIBLE : View.GONE);

		TextView tv = (TextView)dialog.findViewById(R.id.textViewDescription);
		if (playmode == PlayMode.GUESS)
			tv.setText(R.string.descriptionGameNamedetect);
		else if (playmode == PlayMode.SOLVE)
			tv.setText(R.string.descriptionGameWon);
		if (playmode == PlayMode.LOST)
			tv.setText(R.string.descriptionGameLost);

		tv = (TextView)dialog.findViewById(R.id.textViewCondition);
		if (playmode == PlayMode.GUESS)
			tv.setText(dialog.getResources().getString(R.string.descriptionGameConditions,
					Util.amount(winsum), Util.amount(bet), Util.amount(bet)));
		else if (playmode == PlayMode.SOLVE)
			tv.setText(ctx.getResources().getString(R.string.descriptionGameContinue,
					Util.amount(winsum)));
		if (playmode == PlayMode.LOST)
			tv.setText(ctx.getResources().getString(R.string.descriptionGameSummary,
					Util.amount(firstbet)));

		Button b = (Button)dialog.findViewById(R.id.buttonOk);
		b.setVisibility((playmode == PlayMode.SOLVE) ? View.VISIBLE : View.INVISIBLE);
	}

	@Override
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

	@Override
	public boolean guess(int g) {
		if (playmode != playmode.GUESS) return false;
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

}
