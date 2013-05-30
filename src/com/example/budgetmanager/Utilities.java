package com.example.budgetmanager;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;

import com.example.budgetmanager.preference.SettingsFragment;

/**
 * Miscellaneous methods that we use in routine
 *
 * @author Chi Ho coldstar96
 *
 */

public class Utilities {
	public static final int DOLLOR_IN_CENTS = 100;

	private Utilities() {
		// not called
	}

	/**
	 * Transforms a number of cents into a dollar-formatted string.
	 * @param n amount in cents
	 * @return String in $00.00 format
	 */
	public static String amountToDollars(int n) {
		String s = "$" + (n / DOLLOR_IN_CENTS) + ".";
		if ((n % DOLLOR_IN_CENTS) < 10) {
			s += "0";
		}
		s += (n % DOLLOR_IN_CENTS);
		return s;
	}
	
	/**
	 * Sets the theme of the passed Activity.
	 * 
	 * @param act The Activity to set the theme of.
	 * @param ctxt The Context of the passed Activity.
	 */
	public static void setThemeToActivity(Activity act, Context ctxt) {
		// check the Activity's preference to see which theme to set
		String theme = PreferenceManager.getDefaultSharedPreferences(ctxt).
				getString(SettingsFragment.KEY_PREF_APP_THEME, "");

		if (theme.equals(SettingsFragment.APP_THEME_LIGHT)) {
			act.setTheme(android.R.style.Theme_Holo_Light);
		} else {
			act.setTheme(android.R.style.Theme_Holo);
		}				
	}
}