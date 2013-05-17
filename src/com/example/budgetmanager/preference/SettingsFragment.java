package com.example.budgetmanager.preference;

import com.example.budgetmanager.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Main fragment used for allowing users to change settings for the app.
 *
 * @author Ji jiwpark90
 */
public class SettingsFragment extends PreferenceFragment  
		implements OnSharedPreferenceChangeListener {
	/** Keys associated with the settings items */
	public static final String KEY_PREF_STARTING_SCREEN 
			= "pref_starting_screen";
	public static final String KEY_PREF_APP_THEME = "pref_app_theme";
	
	/** Strings associated with the starting screen options */
	public static final String STARTING_SCREEN_LOG = "Log";
	public static final String STARTING_SCREEN_SUMMARY = "Summary";
	public static final String STARTING_SCREEN_ADD_ENTRY = "Add Entry";
	public static final String STARTING_SCREEN_ADD_BUDGET = "Add Budget";
	
	/* reference to the preferences of the app */
	private SharedPreferences spref;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fragment_settings);
        
        // save a reference to the preferences for the app, and make
        // sure all the preference summaries are up-to-date
        spref = getPreferenceManager().getSharedPreferences();
        spref.registerOnSharedPreferenceChangeListener(this);
        updatePreferences();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	// make sure the summaries are up-to-date and register the listener
    	updatePreferences();
    	spref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // unregister the listener before being paused
        spref.unregisterOnSharedPreferenceChangeListener(this);
    }
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs
			, String key) {
		// call helper method to change the summary accordingly
		updatePreferences();
	}
	
	/*
	 * Private helper method to set the preference summaries to the
	 * currently selected preference option.
	 */
	private void updatePreferences() {
		// set the starting screen preference summary
		findPreference(KEY_PREF_STARTING_SCREEN)
				.setSummary("Current starting screen: "
				+ spref.getString(KEY_PREF_STARTING_SCREEN, ""));
		
		// set the theme preference summary
		findPreference(KEY_PREF_APP_THEME)
				.setSummary("Current theme: " + spref
        		.getString(KEY_PREF_APP_THEME, ""));
	}
}
