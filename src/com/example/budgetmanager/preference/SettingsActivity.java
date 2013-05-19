package com.example.budgetmanager.preference;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Hollow Activity which allows users to change settings for the app.
 * 
 * All the preferences-related code is in SettingsFragment.java
 *
 * @author Ji jiwpark90
 */
public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
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
	}
}
