package de.rlill.modelmanager.service;

import android.util.Log;
import android.util.SparseIntArray;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.Loan;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.persistance.LoanDbAdapter;

public class CreditService {

	private static final String LOG_TAG = CreditService.class.getSimpleName();

	private static SparseIntArray creditMap = null;

	public static int getMaxCredit(int modelId) {
		Model model = ModelService.getModelById(modelId);
		int balance = TransactionService.getBalance(modelId);
		if (balance <= 100 * model.getSalary()) return 0;
		return Util.niceRound(balance) - 100 * model.getSalary();
	}

	public static double getCreditInterest(int modelId) {
		// criminal 0..100 -> 0.5 .. 5.5 %
		Model model = ModelService.getModelById(modelId);
		return 0.5 + model.getCriminal() / 20;
	}

	public static int getCreditBalanceForModel(int modelId) {
		if (creditMap == null) {
			creditMap = new SparseIntArray();
			for (Loan loan : LoanDbAdapter.getOpenLoans()) {
				creditMap.put(loan.getModelId(), loan.getAmount());
			}
		}
		return creditMap.get(modelId, 0);
	}

	public static int getTotalCredits() {
		if (creditMap == null) getCreditBalanceForModel(0);

		int tc = 0;
		for (int i = 0; i < creditMap.size(); i++) tc += creditMap.valueAt(i);
		return tc;
	}

	public static boolean orderCredit(int modelId, int amount) {

		int maxCredit = CreditService.getMaxCredit(modelId);
		double interest = CreditService.getCreditInterest(modelId);

		if (amount <= 0 || amount > maxCredit) {
			Log.i(LOG_TAG, "Invalid amount: " + amount + " (max is " + maxCredit + ")");
			return false;
		}
		int intRate = (int)(interest * amount / 100);

		TransactionService.transfer(modelId, 0, amount, MessageService.getMessage(R.string.accountmessage_credit_pay));

		Loan loan = LoanDbAdapter.getLoan(modelId);
		if (loan == null) {
			loan = new Loan();
			loan.setModelId(modelId);
			loan.setInterest(interest);
			loan.setAmount(amount + intRate);
			loan.setStartDay(DiaryService.today());
			LoanDbAdapter.addLoan(loan);
		}
		else {
			loan.setInterest(interest);
			loan.setAmount(loan.getAmount() + amount + intRate);
			LoanDbAdapter.updateLoan(loan);
		}

		// cache
		creditMap.put(modelId, loan.getAmount());

		return true;
	}

	public static boolean paybackCredit(int modelId, int amount) {

		if (amount <= 0) {
			Log.i(LOG_TAG, "Invalid amount: " + amount);
			return false;
		}

		// find open credit
		Loan loan = LoanDbAdapter.getLoan(modelId);
		if (loan == null) return false;

		if (amount > loan.getAmount()) amount = loan.getAmount();

		// pay amount back
		TransactionService.transfer(0, modelId, amount, MessageService.getMessage(R.string.accountmessage_credit_payback));

		loan.setAmount(loan.getAmount() - amount);
		if (loan.getAmount() == 0) loan.setFinishDay(DiaryService.today());
		LoanDbAdapter.updateLoan(loan);

		// cache
		creditMap.put(modelId, loan.getAmount());

		return true;
	}

	public static void newDay() {
		if (creditMap == null) creditMap = new SparseIntArray();
		for (Loan loan : LoanDbAdapter.getOpenLoans()) {
			if (loan.getAmount() == 0) continue;

			// increase by interest
			int intRate = (int)(loan.getAmount() * loan.getInterest() / 100);
			loan.setAmount(loan.getAmount() + intRate);
			LoanDbAdapter.updateLoan(loan);

			// cache
			creditMap.put(loan.getModelId(), loan.getAmount());
		}
	}
}
