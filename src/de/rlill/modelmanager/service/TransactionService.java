package de.rlill.modelmanager.service;

import android.util.Log;
import android.util.SparseIntArray;
import de.rlill.modelmanager.model.Transaction;
import de.rlill.modelmanager.persistance.TransactionDbAdapter;


public class TransactionService {

	private static final String LOG_TAG = "MM*" + TransactionService.class.getSimpleName();
	private static SparseIntArray balanceMap = new SparseIntArray();

	public static int getBalance(int modelId) {
		if (balanceMap.indexOfKey(modelId) >= 0)
			return balanceMap.get(modelId);

		Transaction tr = TransactionDbAdapter.getMostCurrentTransaction(modelId);
		if (tr == null) return 0;

		balanceMap.put(modelId, tr.getBalance());
		return tr.getBalance();
	}

	public static void transfer(int fromId, int toId, int amount, String description) {
		if (amount <= 0) {
			Log.w(LOG_TAG, "Ignoring invalid transfer amount " + amount + " from " + fromId + " to " + toId + " (" + description + ")");
			return;
		}
		Log.i(LOG_TAG, "transfer *" + amount + ".- from " + fromId + " to " + toId + " (" + description + ")");
		if (fromId >= 0) {
			int balanceFrom = getBalance(fromId) - amount;
			Transaction t = new Transaction();
			t.setDay(DiaryService.today());
			t.setPerson1(fromId);
			t.setPerson2(toId);
			t.setAmount(-amount);
			t.setBalance(balanceFrom);
			t.setDescription(description);
			TransactionDbAdapter.addTransaction(t);
			balanceMap.put(fromId, balanceFrom);
		}
		if (toId >= 0) {
			int balanceTo = getBalance(toId) + amount;
			Transaction t = new Transaction();
			t.setDay(DiaryService.today());
			t.setPerson1(toId);
			t.setPerson2(fromId);
			t.setAmount(amount);
			t.setBalance(balanceTo);
			t.setDescription(description);
			TransactionDbAdapter.addTransaction(t);
			balanceMap.put(toId, balanceTo);
		}
	}
}
