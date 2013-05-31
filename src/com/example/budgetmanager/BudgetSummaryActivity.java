package com.example.budgetmanager;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import com.example.budgetmanager.Budget.Duration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
        int cycle = intent.getIntExtra("BUDGET_CYCLE", -1);

        /*
		for(Budget b: Budget.getBudgets()) {
			if(b.getId() == budgetId) {
				myBudget = b;
				break;
			}
		}
		*/
		        
		Budget budget = new Budget("budget", 50, false, new LocalDate(2000,9,9), Duration.WEEK);
		budget.addEntry(new Entry(10, budget, "entry1", new LocalDate(2000,9,10)));
		budget.addEntry(new Entry(5, budget, "entry1", new LocalDate(2000,9,11)));
		budget.addEntry(new Entry(35, budget, "entry1", new LocalDate(2000,9,15)));
		
		myBudget = budget;
		
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
			Log.v("mytag","Will it add it?  I don't know... :/ " + e.getDate().toString());
			Log.v("mytag", myBudget.getStartDate(cycle).toString());
			Log.v("mytag", myBudget.getEndDate(cycle).toString());
			Log.v("mytag", myBudget.getStartDate().toString());
			Log.v("mytag", Boolean.toString(e.getDate().isAfter(myBudget.getStartDate(cycle))));
			Log.v("mytag", Boolean.toString(e.getDate().isBefore(myBudget.getEndDate(cycle))));
			if(e.getDate().isAfter(myBudget.getStartDate(cycle)) && e.getDate().isBefore(myBudget.getEndDate(cycle)) || e.getDate().isEqual(myBudget.getEndDate(cycle))) {
				Log.v("mytag","adding entry! " + e.getNotes());
				myEntries.add(e);
			}	
		}
        //Inflate view
		setContentView(R.layout.activity_budget_summary);

		budgetTotal = (TextView) findViewById(R.id.budget_total);
		budgetBalance = (TextView) findViewById(R.id.budget_balance);
		
		//TODO implement better way to pass entries to surface
		((DrawBudgetGraph) findViewById(R.id.BudgetGraph)).setEntryList(myEntries, myBudget, cycle);
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
