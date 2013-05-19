package com.example.budgetmanager.preference;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.example.budgetmanager.AddEntryActivity;
import com.example.budgetmanager.R;

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
	
	/** Strings associated with the app theme options */
	public static final String APP_THEME_LIGHT = "Light";
	public static final String APP_THEME_DARK = "Dark";
	
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
		
		// if the theme has been changed, toast that it will change 
		// on next the next launch of the app
		// TODO Dynamically change the theme when the user changes it
		if (key.equals(KEY_PREF_APP_THEME)) {
        	Toast.makeText(getActivity(), "R.string/toast_theme_change"
					, Toast.LENGTH_LONG).show();
		}
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
