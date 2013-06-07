package com.example.budgetmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.budgetmanager.api.ApiInterface;
import com.example.budgetmanager.preference.SettingsActivity;
import com.example.budgetmanager.preference.SettingsFragment;

/**
 * Activity which allows users to view a summary of a single budget.
 *
 * @author Andrew clinger
 */
public class BudgetSummaryActivity extends Activity {

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// inflate the menu
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.items, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()) {
		case R.id.menu_settings:
			// take the user to the Settings screen
			Intent settingsIntent = new Intent(BudgetSummaryActivity.this,
					SettingsActivity.class);

			// these extras allow SettingsActivity to skip the 'headers'
			// layer, which is unnecessary since we have very few settings
			settingsIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
					SettingsFragment.class.getName());
			settingsIntent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);

			startActivity(settingsIntent);

			return false;

		case R.id.menu_signout:
			// sign the user out
			ApiInterface.getInstance().logOut();
			Intent logOut = new Intent(BudgetSummaryActivity.this, LoginActivity.class);
			// Clear the back stack so when you press the back button you will exit the app
			logOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			// Goes to the login page
			startActivity(logOut);
			return false;
		}
		return true;
	}
}