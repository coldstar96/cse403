package com.example.budgetmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.budgetmanager.preference.SettingsActivity;
import com.example.budgetmanager.preference.SettingsFragment;

/**
 * Activity which displays list of budgets screen to the user,
 * showing summaries of the budgets.
 *
 * @author Chi Ho coldstar96
 */

public class SummaryActivity extends Activity {
	private final String TAG = "SummaryActivity";

	// UI reference
	private ListView listView;

	private BudgetLogAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// set theme based on current preferences
		Utilities.setActivityTheme(this, getApplicationContext());

		super.onCreate(savedInstanceState);

		// inflate view
		setContentView(R.layout.activity_summary);

		// retrieve the application data
		Log.d(TAG, "Just got the app, about to make the adapter");
		adapter = new BudgetLogAdapter(this, R.layout.list_budget_layout,
				Budget.getBudgets());
		Log.d(TAG, "Made the adapter!");

		// set up Entry Logs screen
		listView = (ListView) findViewById(R.id.budget_list);
		listView.setAdapter(adapter);
		Log.d(TAG, "added the adapter!");

		adapter.sort(new BudgetLogAdapter.BudgetActiveComparator());
		adapter.notifyDataSetChanged();
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
			Intent settingsIntent = new Intent(SummaryActivity.this,
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
			// TODO implement a signout functionality
			Toast.makeText(SummaryActivity.this,
					"Successfully handled Sign out selection",
					Toast.LENGTH_LONG).show();
			return false;
		}

		return true;
	}
}
