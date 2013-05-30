package com.example.budgetmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class BudgetSummaryActivity extends Activity {

	private Budget myBudget;
	private List<Entry> myEntries;
	private TextView budgetTotal;
	private TextView budgetBalance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();

		//get the budget id from the intent
		int budgetId = intent.getIntExtra("BUDGET_ID", -1);

		for(Budget b: Budget.getBudgets()) {
			if(b.getId() == budgetId) {
				myBudget = b;
				break;
			}
		}

		//Only use entries from current period.
		//Code should be refactored to be elsewhere
		int cycle = myBudget.getCurrentCycle();
		myEntries = new ArrayList<Entry>();
		for(Entry e: myBudget.getEntries()) {
			if(e.getDate().isAfter(myBudget.getStartDate(cycle)) && e.getDate().isBefore(myBudget.getEndDate(cycle))) {
				myEntries.add(e);
			}	
		}
        //Inflate view
		setContentView(R.layout.activity_budget_summary);

		budgetTotal = (TextView) findViewById(R.id.budget_total);
		budgetBalance = (TextView) findViewById(R.id.budget_balance);
		
		//TODO implement better way to pass entries to surface
		((DrawBudgetGraph) findViewById(R.id.BudgetGraph)).setEntryList(myEntries, myBudget);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		//TODO currently in cents. change before merge
		int totalBudget = 0;
		int balance;
		
		for(Entry e: myEntries)
			totalBudget += e.getAmount();
		
		balance = myBudget.getBudgetAmount() - totalBudget;
		
		budgetTotal.setText(Integer.toString(totalBudget));
		budgetBalance.setText(Integer.toString(balance));
	}
}
