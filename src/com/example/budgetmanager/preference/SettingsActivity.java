package com.example.budgetmanager.preference;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Activity which allows users to change settings for the app.
 *
 * @author Ji jiwpark90
 */
public class SettingsActivity extends PreferenceActivity{
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
//	
//	/**
//     * Populate the activity with the top-level headers.
//     */
//    @Override
//    public void onBuildHeaders(List<Header> target) {
//        loadHeadersFromResource(R.xml.headers_settings, target);
//    }
//    
//    /**
//     * This fragment shows the preferences for the first header.
//     */
//    public static class PrefStartingScreenFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//
//            // Make sure default values are applied.  In a real app, you would
//            // want this in a shared function that is used to retrieve the
//            // SharedPreferences wherever they are needed.
////            PreferenceManager.setDefaultValues(getActivity(),
////                    R.xml.activity_settings, false);
//
//            // Load the preferences from an XML resource
//            addPreferencesFromResource(R.xml.fragment_setting_starting_screen);
//        }
//    }
//    
//    /**
//     * This fragment shows the preferences for the second header.
//     */
//    public static class PrefAppThemeFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//
//            // Can retrieve arguments from headers XML.
//            Log.i("args", "Arguments: " + getArguments());
//
//            // Load the preferences from an XML resource
//            addPreferencesFromResource(R.xml.fragment_setting_starting_screen);
//        }
//    }
}
