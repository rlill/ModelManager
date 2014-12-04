package de.rlill.modelmanager.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.adapter.StatusBarFragmentAdapter;
import de.rlill.modelmanager.model.Loan;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.persistance.LoanDbAdapter;
import de.rlill.modelmanager.service.CreditService;
import de.rlill.modelmanager.service.ModelService;
import de.rlill.modelmanager.service.TransactionService;

public class CreditDialog extends Activity implements View.OnClickListener {

	private static final String LOG_TAG = "MM*" + CreditDialog.class.getSimpleName();

	private int modelId;
	public final static String EXTRA_MODEL_ID = "credit.dialog.model.id";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_credit_dialog);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		StatusBarFragmentAdapter.initStatusBar(findViewById(R.id.status_bar_include));

		// Get the message from the intent
		Intent intent = getIntent();
		modelId = intent.getIntExtra(EXTRA_MODEL_ID, 0);
		if (modelId == 0) return;

		Model model = ModelService.getModelById(modelId);

		int ir = getResources().getIdentifier(model.getImage(), "drawable", getPackageName());
		ImageView iv = (ImageView)findViewById(R.id.imageView1);
		iv.setImageResource(ir);

		Button b = (Button)findViewById(R.id.buttonRequest);
		b.setOnClickListener(this);
		b = (Button)findViewById(R.id.buttonPayback);
		b.setOnClickListener(this);

		updateCreditDialog();
	}

	private void updateCreditDialog() {
		if (modelId == 0) return;
		Model model = ModelService.getModelById(modelId);
		TextView tv = (TextView)findViewById(R.id.textview_name);
		tv.setText(model.getFullname());

		Loan loan = LoanDbAdapter.getLoan(modelId);
		if (loan != null && loan.getAmount() > 0) {
			tv = (TextView)findViewById(R.id.textViewOpenCredits);
			String msg = getResources().getString(R.string.display_msg_credit_open,
					model.getFullname(),
					Util.amount(loan.getAmount()),
					String.format("%4.2f", loan.getInterest())
					);
			tv.setVisibility(View.VISIBLE);
			tv.setText(msg);

			int pba = TransactionService.getBalance(0);
			if (pba > loan.getAmount()) pba = loan.getAmount();

			EditText et = (EditText)findViewById(R.id.editTextPayback);
			et.setText(Integer.toString(pba));
		}
		else {
			tv = (TextView)findViewById(R.id.textViewOpenCredits);
			tv.setVisibility(View.GONE);

			EditText et = (EditText)findViewById(R.id.editTextPayback);
			et.setText("");
		}

		int maxCredit = CreditService.getMaxCredit(modelId);
		double interest = CreditService.getCreditInterest(modelId);
		int intRate = (int)(interest * maxCredit / 100);

		tv = (TextView)findViewById(R.id.textViewDescription);
		String msg = getResources().getString(R.string.display_msg_credit_offer,
				Util.amount(TransactionService.getBalance(modelId)),
				Util.amount(maxCredit),
				String.format("%4.2f", interest),
				Util.amount(intRate)
				);
		tv.setText(msg);

		EditText et = (EditText)findViewById(R.id.editTextRequest);
		et.setText(Integer.toString(maxCredit));
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.buttonRequest) {
			EditText et = (EditText)findViewById(R.id.editTextRequest);
			int amount = Util.atoi(et.getText().toString());

			CreditService.orderCredit(modelId, amount);

			updateCreditDialog();
		}
		else if (v.getId() == R.id.buttonPayback) {
			EditText et = (EditText)findViewById(R.id.editTextPayback);
			int amount = Util.atoi(et.getText().toString());

			CreditService.paybackCredit(modelId, amount);

			updateCreditDialog();
		}
	}

}
