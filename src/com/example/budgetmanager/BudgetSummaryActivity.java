package com.example.budgetmanager;

import android.app.Activity;
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

	/**
	 * Sets up the budget data for display
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();

		//get the budget id from the intent
		int budgetId = intent.getIntExtra("BUDGET_ID", -1);
		int cycle = intent.getIntExtra("BUDGET_CYCLE", -1);


		for(Budget b: Budget.getBudgets()) {
			if(b.getId() == budgetId) {
				myBudget = b;
				break;
			}
		}

		if(myBudget == null) {
			throw new IllegalArgumentException();
		}

		if(cycle == -1) {
			if(myBudget.isRecurring()) {
				cycle = myBudget.getCurrentCycle();
			} else {
				cycle = 0;
			}
		}


		//Only use entries from current period.
		//Code should be refactored to be elsewhere
		myEntries = new ArrayList<Entry>();

		for(Entry e: myBudget.getEntries()) {
			if(e.getDate().isAfter(myBudget.getStartDate(cycle)) && e.getDate().isBefore(myBudget.getEndDate(cycle)) || e.getDate().isEqual(myBudget.getEndDate(cycle))) {
				myEntries.add(e);
			}
		}

		//Inflate view
		setContentView(R.layout.activity_budget_summary);

		budgetName = (TextView) findViewById(R.id.budget_name);
		budgetTotal = (TextView) findViewById(R.id.budget_total);
		budgetSpent = (TextView) findViewById(R.id.budget_spent);
		budgetBalance = (TextView) findViewById(R.id.budget_balance);

		Collections.sort(myEntries, new EntryLogAdapter.EntryDateComparator());
		Collections.reverse(myEntries);

		((DrawBudgetGraph) findViewById(R.id.BudgetGraph)).setProperties(myEntries, myBudget, cycle);
	}

	/** Called when the activity starts */
	@Override
	protected void onResume() {
		super.onResume();

		int totalBudget = 0;
		int balance;

		for(Entry e: myEntries) {
			totalBudget += e.getAmount();
		}

		balance = myBudget.getBudgetAmount() - totalBudget;

		budgetName.setText(myBudget.getName());
		budgetTotal.setText(Integer.toString(myBudget.getBudgetAmount()));
		budgetSpent.setText(Integer.toString(totalBudget));
		budgetBalance.setText(Integer.toString(balance));
	}
}
