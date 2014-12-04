package de.rlill.modelmanager.adapter;

import android.view.View;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.service.DiaryService;
import de.rlill.modelmanager.service.TransactionService;
import de.rlill.modelmanager.struct.Weekday;

public class StatusBarFragmentAdapter {

	private static final String LOG_TAG = StatusBarFragmentAdapter.class.getSimpleName();

	public static void initStatusBar(View parent) {

		TextView tv = (TextView)parent.findViewById(R.id.statusBar_textView_date);
		String d = parent.getResources().getString(R.string.display_day_si);
		int w = DiaryService.today();
		Weekday wd = Weekday.getInstanceByIndex(w % 7);
		tv.setText(d + " " + w + " (" + wd.getName().substring(0, 2) + ")");

		tv = (TextView)parent.findViewById(R.id.statusBar_textView_balance);
		int bal = TransactionService.getBalance(0);
		tv.setText(Util.amount(bal));
		if (bal < 0) tv.setTextColor(0xFFFF0000);
		else tv.setTextColor(0xFF000000);
	}
}
