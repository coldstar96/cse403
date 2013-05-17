package com.example.budgetmanager.preference;

import com.example.budgetmanager.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Main fragment used for allowing users to change settings for the app.
 *
 * @author Ji jiwpark90
 */
public class PreferencesFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure default values are applied.  In a real app, you would
        // want this in a shared function that is used to retrieve the
        // SharedPreferences wherever they are needed.
//        PreferenceManager.setDefaultValues(getActivity(),
//                R.xml.activity_settings, false);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fragment_settings);
    }
}
