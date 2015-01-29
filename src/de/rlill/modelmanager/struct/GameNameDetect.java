package de.rlill.modelmanager.struct;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.model.Model;

public class GameNameDetect extends Game {

	private static final String LOG_TAG = "MM*" + GameNameDetect.class.getSimpleName();

	public GameNameDetect(Context c, int b) {
		super(c, b);
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

}
