package de.rlill.modelmanager.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import de.rlill.modelmanager.R;

public class MessageBox {

	private static final String LOG_TAG = "MM*" + AccountDetailDialog.class.getSimpleName();

	public static class MessageBoxClickListener implements DialogInterface.OnClickListener {

		private int responseNr = 0;

	    @Override
	    public void onClick(DialogInterface dialog, int which) {

	    	Log.i(LOG_TAG, "MessageBox click " + which);
	    	responseNr = which;
/*
	        switch (which){
	        case DialogInterface.BUTTON_POSITIVE:
	            //Yes button clicked
	        	responseNr = 1;
	            break;

	        case DialogInterface.BUTTON_NEGATIVE:
	            //No button clicked
	        	responseNr = 2;
	            break;
	        }
*/
	    }

	    public int getResponseNr() {
	    	return responseNr;
	    }
	}

	public static int show(Context ctx, String question,
			int ressourceIdOption1, int ... ressourceIdOptions) {

		MessageBoxClickListener dialogClickListener = new MessageBoxClickListener();

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//	    builder.setTitle("Title");
		builder.setMessage(question);

		String option = ctx.getResources().getString(ressourceIdOption1);
		builder.setNeutralButton(option, dialogClickListener);

		if (ressourceIdOptions != null && ressourceIdOptions.length > 0) {
			for (int roid : ressourceIdOptions) {
				option = ctx.getResources().getString(roid);
				builder.setNeutralButton(option, dialogClickListener);
			}
		}
		builder.show();

		return dialogClickListener.getResponseNr();
	}

	public static boolean yesNo(Context ctx, String question) {

		MessageBoxClickListener dialogClickListener = new MessageBoxClickListener();

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//	    builder.setTitle("Title");
		builder.setMessage(question);

		String option = ctx.getResources().getString(R.string.display_option_yes);
		builder.setPositiveButton(option, dialogClickListener);
		option = ctx.getResources().getString(R.string.display_option_no);
		builder.setNegativeButton(option, dialogClickListener);
		builder.show();

		Log.d(LOG_TAG, "RESPONSE " + dialogClickListener.getResponseNr());
		return (dialogClickListener.getResponseNr() == DialogInterface.BUTTON_POSITIVE);
	}

	public static void ok(Context ctx, String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//	    builder.setTitle("Title");
		builder.setMessage(message);

		String option = ctx.getResources().getString(R.string.display_option_ok);
		builder.setPositiveButton(option, null);
		builder.show();
	}

}
