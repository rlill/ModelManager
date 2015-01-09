package de.rlill.modelmanager.dialog;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.Transaction;
import de.rlill.modelmanager.persistance.TransactionDbAdapter;
import de.rlill.modelmanager.service.DiaryService;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.struct.TransactionIterator;

public class AccountDetailDialog extends Activity
	implements OnClickListener, OnKeyListener {

//	private static final String LOG_TAG = AccountDetailDialog.class.getSimpleName();

	private int modelId;
	public final static String EXTRA_MODEL_ID = "account.detail.dialog.model.id";
	private int filterDay;
	private String filterDescription;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_account_transaction_list);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		StatusBarFragmentAdapter.initStatusBar(findViewById(R.id.status_bar_include));

		// Get the message from the intent
		Intent intent = getIntent();
		modelId = intent.getIntExtra(EXTRA_MODEL_ID, -1);
		if (modelId < 0) return;

		// face icon
		ImageView image = (ImageView)findViewById(R.id.messageImage);
		// credit button
		Button b = (Button)findViewById(R.id.buttonCredit);

		if (modelId > 0) {
			Model model = ModelService.getModelById(modelId);
			getActionBar().setTitle(model.getFullname());
			int ir = getResources().getIdentifier(model.getImage(), "drawable", getPackageName());
			image.setVisibility(ImageView.VISIBLE);
			image.setImageResource(ir);

			// opens credit dialog
			b.setOnClickListener(this);
		}
		else {
			// no image
			image.setVisibility(ImageView.INVISIBLE);

			// set actual day filter for player account
			filterDay = DiaryService.today();

			// no credit button
			b.setVisibility(View.GONE);
		}

		refreshTransactionList();


		// Paint a histogram
		SparseIntArray dailyBalance = new SparseIntArray();
		int memDay = 0;
		int minBalance = 0;
		int maxBalance = 0;
		TransactionIterator ti = TransactionDbAdapter.listTransactionsSince(0, modelId);

		int startDay = DiaryService.today() - 60;
		if (startDay < 1) startDay = 1;

		while (ti.hasNext()) {
			Transaction t = ti.next();
			if (t.getDay() != memDay) {
				memDay = t.getDay();
				dailyBalance.put(memDay, t.getBalance());
			}

			if (t.getBalance() > maxBalance) maxBalance = t.getBalance();
			if (t.getBalance() < minBalance) minBalance = t.getBalance();

			if (t.getDay() < startDay) {
				ti.close();
				break;
			}
		}

		Bitmap bg = Bitmap.createBitmap(300, 200, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bg);

		Paint blackLine = new Paint();
		blackLine.setColor(Color.BLACK);
		blackLine.setStyle(Paint.Style.STROKE);

		Paint redLine = new Paint();
		redLine.setColor(Color.RED);
		redLine.setStyle(Paint.Style.STROKE);
		redLine.setStrokeWidth(3);

		canvas.drawRect(0, 0, 299, 199, blackLine);

		int prevA = 0;
		for (int day = startDay; day <= DiaryService.today(); day++) {

			int a = dailyBalance.get(day, 0);
			if (a == 0) a = prevA;
			int h = (int)((double)a * 100 / maxBalance);
			canvas.drawLine(5 * day, 100, 5 * day, 100 - h, redLine);
			prevA = a;
		}

		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(16);
		canvas.drawText(Integer.toString(maxBalance), 0, 20, paint);


		View v = findViewById(R.id.viewAccountChart);
		v.setBackgroundDrawable(new BitmapDrawable(getResources(), bg));


		EditText et = (EditText)findViewById(R.id.editTextFilter);
		et.setOnKeyListener(this);


		if (modelId == 0) {
			// day pager
			LinearLayout ll = (LinearLayout)findViewById(R.id.account_dayselect_list);
			for (int i = 1; i < DiaryService.today(); i++) {
				TextView tv = new TextView(this);
				tv.setText(Integer.toString(i));
				tv.setTextColor(0xFF0000FF);
				tv.setPadding(4, 4, 4, 4);
				tv.setOnClickListener(this);
				tv.setTag(Integer.valueOf(i));
				ll.addView(tv);
			}
			final HorizontalScrollView hv = (HorizontalScrollView)findViewById(R.id.account_dayselect_scroll);
			hv.postDelayed(new Runnable() {
			    public void run() {
			        hv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
			    }
			}, 100L);
		}
	}

	private TableRow mkrow(int day, String description, int amount, int balance, String person2) {
		TableRow tr = new TableRow(this);

		TextView tv = new TextView(this);
		tv.setText(Integer.toString(day));
		tv.setPadding(40, 4, 5, 4);
		tr.addView(tv);

		tv = new TextView(this);
		tv.setText(description);
		tv.setPadding(5, 4, 5, 4);
		tr.addView(tv);

		// colspan
		TableRow.LayoutParams params = (TableRow.LayoutParams) tv.getLayoutParams();
		params.span = 2;
		tv.setLayoutParams(params);

		tv = new TextView(this);
		tv.setText(Util.amount(amount));
		tv.setGravity(Gravity.RIGHT);
		if (amount < 0) tv.setTextColor(0xffff0000);
		tv.setPadding(5, 4, 5, 4);
		tr.addView(tv);

		tv = new TextView(this);
		tv.setText(Util.amount(balance));
		tv.setGravity(Gravity.RIGHT);
		if (balance < 0) tv.setTextColor(0xffff0000);
		tv.setPadding(5, 4, 5, 4);
		tr.addView(tv);

		tv = new TextView(this);
		tv.setText(person2);
		tv.setPadding(5, 4, 5, 4);
		tr.addView(tv);

		return tr;
	}


	public void modelViewDetails(View view) {
		Intent intent = new Intent(this, ModelNegotiationDialog.class);
		intent.putExtra(ModelNegotiationDialog.EXTRA_MODEL_ID, modelId);
		startActivityForResult(intent, 1);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonCredit) {
			Intent intent = new Intent(this, CreditDialog.class);
			intent.putExtra(CreditDialog.EXTRA_MODEL_ID, modelId);
			startActivityForResult(intent, 1);
		}
		else if (v instanceof TextView) {
			Integer day = (Integer)v.getTag();
			if (day > 0) {
				filterDay = day;
				refreshTransactionList();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		refreshTransactionList();
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		EditText et = (EditText)v;
		filterDescription = et.getText().toString();
		if (filterDescription != null && filterDescription.length() == 0) filterDescription = null;
		refreshTransactionList();
		return false;
	}

	private void refreshTransactionList() {

		int stmtcnt = 0;
		TableLayout tl = (TableLayout)findViewById(R.id.account_transaction_list);
		while (tl.getChildCount() > 1) tl.removeViewAt(1);
		List<Transaction> tlist = null;
		if (filterDay > 0)
			tlist = TransactionDbAdapter.getTransactions(modelId, filterDay);
		else
			tlist = TransactionDbAdapter.getTransactions(modelId);
		for (Transaction t : tlist) {
			if (filterDescription != null &&
					!t.getDescription().toLowerCase().contains(filterDescription)) continue;
			String p2 = "";
			if (t.getPerson2() > 0) {
				Model model = ModelService.getModelById(t.getPerson2());
				if (model != null) p2 = model.getFullname();
			}
			stmtcnt++;
			tl.addView(mkrow(t.getDay(), t.getDescription(), t.getAmount(), t.getBalance(), p2));
		}
		if (stmtcnt == 0)
			tl.addView(mkrow(0,
					getResources().getString(
							(filterDescription != null) ? R.string.display_account_no_filtermatch : R.string.display_account_no_statements),
					0, 0, ""));

	}

}
