package com.example.budgetmanager.preference;

import com.example.budgetmanager.Utilities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Activity which allows users to change settings for the app.
 *
 * @author Ji jiwpark90
 */
public class SettingsActivity extends PreferenceActivity 
		implements OnSharedPreferenceChangeListener {
	
	/* reference to the preferences of the app */
	private SharedPreferences spref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		spref = PreferenceManager.getDefaultSharedPreferences(this);

		// set theme based on current preferences
		Utilities.setActivityTheme(this, getApplicationContext());
		
		// call this after setting the theme to correctly set the theme
		super.onCreate(savedInstanceState);
		
		// listen to changes in preferences for theme changes
		spref.registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		spref.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		spref.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		// restart the activity so that it paints the correct theme
		if (key.equals(SettingsFragment.KEY_PREF_APP_THEME)) {
			recreate();
		}
	}
}