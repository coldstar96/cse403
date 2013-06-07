package com.example.budgetmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Activity which allows users to view a summary of a single budget.
 *
 * @author Andrew clinger
 */
public class BudgetSummaryActivity extends UBudgetActivity {

	// Budget being viewed
	private Budget myBudget;

	// List of entries from budget that are in the current cycle.
	private List<Entry> myEntries;

	// Text views that are set programmatically.
	private TextView budgetName;
	private TextView budgetTotal;
	private TextView budgetSpent;
	private TextView budgetBalance;

	/**
	 * Sets up the budget data for display
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set theme based on current preferences
		Utilities.setActivityTheme(this, getApplicationContext());

		// for information about the Budget
		Bundle bundle = getIntent().getExtras();

		// get the budget id from the intent
		long budgetId = bundle.getLong("BudgetId", -1);
		int cycle = bundle.getInt("BudgetCycle", -1);

		myBudget = Budget.getBudgetById(budgetId);

		if (cycle == -1) {
			if (myBudget.isRecurring()) {
				cycle = myBudget.getCurrentCycle();
			} else {
				cycle = 0;
			}
		}

		// Only use entries from current period.
		// Code should be refactored to be elsewhere
		myEntries = new ArrayList<Entry>();

		for (Entry e : myBudget.getEntries()) {
			if (e.getDate().isAfter(myBudget.getStartDate(cycle))
					&& e.getDate().isBefore(myBudget.getEndDate(cycle))
					|| e.getDate().isEqual(myBudget.getEndDate(cycle))
					|| e.getDate().isEqual(myBudget.getStartDate(cycle))) {
				myEntries.add(e);
			}
		}

		// Inflate view
		setContentView(R.layout.activity_budget_summary);

		budgetName = (TextView) findViewById(R.id.budget_name);
		budgetTotal = (TextView) findViewById(R.id.budget_total);
		budgetSpent = (TextView) findViewById(R.id.budget_spent);
		budgetBalance = (TextView) findViewById(R.id.budget_balance);

		Collections.sort(myEntries, new EntryLogAdapter.EntryDateComparator());

		// Reversing after sorting, because our comparator has the reverse
		// behavior from what is desired in this situation.
		Collections.reverse(myEntries);

		((DrawBudgetGraph) findViewById(R.id.BudgetGraph)).setProperties(myEntries, myBudget, cycle);

		// set the view items
		setViews();

		// trick to prevent infinite looping when onResume() is called
		getIntent().setAction("Already created");
	}

	/* Helper method to set TextViews in the Activity. */
	private void setViews() {
		int totalBudget = 0;
		int balance;

		for (Entry e : myEntries) {
			totalBudget += e.getAmount();
		}

		balance = myBudget.getBudgetAmount() - totalBudget;

		budgetName.setText(myBudget.getName());
		budgetTotal.setText(Utilities.amountToDollars(myBudget.getBudgetAmount()));
		budgetSpent.setText(Utilities.amountToDollars(totalBudget));
		budgetBalance.setText(Utilities.amountToDollars(balance));
	}

	@Override
	protected void onResume() {
		super.onResume();

		String action = getIntent().getAction();
		if (action == null || !action.equals("Already created")) {
			// don't restart if action is present
			Intent intent = new Intent(this, BudgetSummaryActivity.class);
			intent.putExtra("BudgetId", myBudget.getId());
			intent.putExtra("BudgetCycle", myBudget.getCurrentCycle());
			startActivity(intent);
			finish();
		} else {
			// remove the unique action so the next time onResume
			// call will force restart
			getIntent().setAction(null);
		}
	}
}