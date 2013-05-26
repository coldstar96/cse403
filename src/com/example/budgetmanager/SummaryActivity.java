package com.example.budgetmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.budgetmanager.preference.SettingsActivity;
import com.example.budgetmanager.preference.SettingsFragment;

public class SummaryActivity extends Activity {
	private final String TAG = "SummaryActivity";

	// UI reference
	private ListView listView;

	private BudgetLogAdapter adapter;

	private UBudgetApp app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// set default values for settings (if never done before)
		PreferenceManager.setDefaultValues(this, R.xml.fragment_settings, false);

		// check the preference to see which theme to set
		String startingScreen = PreferenceManager.
				getDefaultSharedPreferences(this).getString(SettingsFragment
						.KEY_PREF_APP_THEME, "");

		if (startingScreen.equals(SettingsFragment
				.APP_THEME_LIGHT)) {
			setTheme(android.R.style.Theme_Holo_Light);
		} else {
			setTheme(android.R.style.Theme_Holo);
		}
		
		super.onCreate(savedInstanceState);

		// inflate view
		setContentView(R.layout.activity_summary);

		// retrieve the application data
		app = (UBudgetApp) getApplication();
		Log.d(TAG, "Just got the app, about to make the adapter");
		adapter = new BudgetLogAdapter(this, R.layout.list_budget_layout,
				app.getBudgetList());
		Log.d(TAG, "Made the adapter!");

		// set up Entry Logs screen
		listView = (ListView) findViewById(R.id.budget_list);
		listView.setAdapter(adapter);
		Log.d(TAG, "added the adapter!");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// set up the button that lead to the settings activity
		MenuItem buttonSettings = menu.add(R.string.title_settings);

		// this forces it to go in the overflow menu, which is preferred.
		buttonSettings.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		buttonSettings.setOnMenuItemClickListener(new MenuItem.
				OnMenuItemClickListener() {
			/**
			 * Take the users to the Settings activity upon clicking the button.
			 */
			public boolean onMenuItemClick(MenuItem item) {
				Intent settingsIntent = new Intent(SummaryActivity.this,
						SettingsActivity.class);

				// these extras allow SettingsActivity to skip the 'headers'
				// layer, which is unnecessary since we have very few settings
				settingsIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
						SettingsFragment.class.getName());
				settingsIntent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);

				SummaryActivity.this.startActivity(settingsIntent);

				return false;
			}
		});

		// set up the button that lead to the signout activity
		MenuItem buttonSignout = menu.add(R.string.title_signout);

		// this forces it to go in the overflow menu, which is preferred.
		buttonSignout.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		buttonSignout.setOnMenuItemClickListener(new MenuItem.
				OnMenuItemClickListener() {
			/**
			 * Sign out the user upon clicking the button.
			 */
			public boolean onMenuItemClick(MenuItem item) {
				// TODO implement a signout functionality
				Toast.makeText(SummaryActivity.this,
						"Successfully handled Sign out selection",
						Toast.LENGTH_LONG).show();
				return false;
			}
		});
		return true;
	}
}
