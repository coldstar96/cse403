package com.example.budgetmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.budgetmanager.api.ApiInterface;
import com.example.budgetmanager.preference.SettingsActivity;
import com.example.budgetmanager.preference.SettingsFragment;

public class SummaryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// inflate view
		setContentView(R.layout.activity_summary);
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
			@Override
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
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				ApiInterface.getInstance().logOut();
				Intent logOut = new Intent(SummaryActivity.this, LoginActivity.class);
				// Clear the back stack so when you press the back button you will exit the app
				logOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				// Goes to the login page
				startActivity(logOut);
				return false;
			}
		});
		return true;
	}
}
