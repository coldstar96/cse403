package com.example.budgetmanager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Activity which allows users to view a summary of a single budget.
 *
 * @author Andrew clinger
 */
public class BudgetSummaryActivity extends Activity {

	//Budget being viewed
	private Budget myBudget;

	//List of entries from budget that are in the current cycle.
	private List<Entry> myEntries;

	//Text views that are set programmatically.
	private TextView budgetName;
	private TextView budgetTotal;
	private TextView budgetSpent;
	private TextView budgetBalance;

	private final String TAG = "budgetsummary";

	/**
	 * Sets up the budget data for display
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		Log.d(TAG, "Found the budget's cycle.");

		//Only use entries from current period.
		//Code should be refactored to be elsewhere
		myEntries = new ArrayList<Entry>();

		for (Entry e : myBudget.getEntries()) {
			if (e.getDate().isAfter(myBudget.getStartDate(cycle))
					&& e.getDate().isBefore(myBudget.getEndDate(cycle))
					|| e.getDate().isEqual(myBudget.getEndDate(cycle))
					|| e.getDate().isEqual(myBudget.getStartDate(cycle))) {
				myEntries.add(e);
			}
		}

		Log.d(TAG, "Created the list of entries");

		//Inflate view
		setContentView(R.layout.activity_budget_summary);

		Log.d(TAG, "Inflated the activity view.");

		budgetName = (TextView) findViewById(R.id.budget_name);
		budgetTotal = (TextView) findViewById(R.id.budget_total);
		budgetSpent = (TextView) findViewById(R.id.budget_spent);
		budgetBalance = (TextView) findViewById(R.id.budget_balance);

		Collections.sort(myEntries, new EntryLogAdapter.EntryDateComparator());

		//Reversing after sorting, because our comparator has the reverse
		//behavior from what is desired in this situation.
		Collections.reverse(myEntries);

		((DrawBudgetGraph) findViewById(R.id.BudgetGraph)).setProperties(myEntries, myBudget, cycle);

		Log.d(TAG, "Set the properties of the graph.");
	}

	/** Called when the activity starts */
	@Override
	protected void onResume() {
		Log.d(TAG, "Resuming the activity.");
		super.onResume();

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
}
