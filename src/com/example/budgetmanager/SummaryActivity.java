package com.example.budgetmanager;

import com.example.budgetmanager.preference.SettingsActivity;
import com.example.budgetmanager.preference.SettingsFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class SummaryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// set default values for settings (if never done before)
		PreferenceManager.setDefaultValues(this, R.layout.fragment_settings, false);

		// set theme based on current preferences	
		Utilities.setThemeToActivity(this, getApplicationContext());
		
		super.onCreate(savedInstanceState);

		// inflate view
		setContentView(R.layout.activity_summary);
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
