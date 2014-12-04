package de.rlill.modelmanager.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import de.rlill.modelmanager.R;

public class NewDayProgress extends ProgressDialog {

//	private static final String LOG_TAG = "MM*" + NewDayProgress.class.getSimpleName();

	public NewDayProgress(Context context) {
		super(context);

		setTitle(R.string.labelProgress);
		setIndeterminate(false);
		setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		setCancelable(false);
		setProgress(0);
	}

}
