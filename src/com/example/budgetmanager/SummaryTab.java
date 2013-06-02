package com.example.budgetmanager;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Fragment which displays list of budgets screen to the user, offering
 * brief information about budgets and add entry and add budget as well
 *
 * @author Chi Ho coldstar96
 */

/**
 * Activity which displays list of Budgets screen to the user.
 *
 * @author Chi Ho coldstar96
 */
public class SummaryTab extends Fragment {
	private final String TAG = "SummaryTab";

	// UI reference
	private ListView listView;

	private BudgetSummaryAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout layout =
				(RelativeLayout) inflater.inflate(R.layout.activity_summary,
						container, false);

		// set adapter
		adapter = new BudgetSummaryAdapter(getActivity(),
				R.layout.list_budget_layout, Budget.getBudgets());

		// set up Entry Logs screen
		listView = (ListView) layout.findViewById(R.id.budget_list);
		listView.setAdapter(adapter);
		Log.d(TAG, "added the adapter!");

		adapter.sort(new BudgetSummaryAdapter.BudgetActiveComparator());
		adapter.notifyDataSetChanged();

		// trick to prevent infinite looping when onResume() is called
		getActivity().getIntent().setAction("Already created");

		return layout;
	}
}