package de.rlill.modelmanager.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.service.CreditService;
import de.rlill.modelmanager.service.TransactionService;
import de.rlill.modelmanager.struct.ViewElements;

public class AccountListAdapter extends ArrayAdapter<Model> {

	private static final String LOG_TAG = AccountListAdapter.class.getSimpleName();
    private LayoutInflater mInflater;

	public AccountListAdapter(Context context, int resource, int textViewResourceId,
			List<Model> objects, LayoutInflater inflater) {
		super(context, resource, textViewResourceId, objects);
        mInflater = inflater;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//data from your adapter
		Model model = getItem(position);
		AccountListViewElements viewElements = null;

		// reuse already constructed row views...
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_account_item, null);
			viewElements = new AccountListViewElements(convertView);
			convertView.setTag(viewElements);
		}
		else {
			viewElements = (AccountListViewElements) convertView.getTag();
		}

		viewElements.setContextModel(model);
		viewElements.setName(model.getFullname());
		viewElements.setBalance(TransactionService.getBalance(model.getId()));

		if (model.getId() == 0)
			viewElements.setCredit(CreditService.getTotalCredits(), 0xFFFF0000);
		else
			viewElements.setCredit(CreditService.getCreditBalanceForModel(model.getId()), 0xFF0000FF);

		return convertView;
	}

	private class AccountListViewElements extends ViewElements {

		public AccountListViewElements(View view) {
			super(view);
		}

		public void setName(String name) {
			findTextView(R.id.textViewName).setText(name);
		}

		public void setBalance(int balance) {
			findTextView(R.id.textViewBalance).setText(Util.amount(balance));
		}

		public void setCredit(int credit, int color) {
			findTextView(R.id.textViewCredit).setText((credit > 0) ? Util.amount(credit) : "");
			findTextView(R.id.textViewCredit).setTextColor(color);
		}
	}

}
