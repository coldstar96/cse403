package com.example.budgetmanager;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;

import com.example.budgetmanager.preference.SettingsFragment;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

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
	 * Calculates number of days between two dates (inclusive)
	 * @param start starting date
	 * @param end	end date
	 * @return int	number of days between two days (inclusive)
	 */
	public static int dateDifference(LocalDate start, LocalDate end) {
		PeriodType monthDay = PeriodType.yearMonthDay();
		int days = new Period(start, end, monthDay).getDays() + 1;
		return days;
	}

	/**
	 * Sets the theme of the passed Activity.
	 *
	 * @param act The Activity to set the theme of.
	 * @param ctxt The Context of the passed Activity.
	 */
	public static void setActivityTheme(Activity act, Context ctxt) {

		// set default values for settings (if never done before)
		PreferenceManager.setDefaultValues(act, R.layout.fragment_settings, false);

		// check the Activity's preference to see which theme to set
		String theme = PreferenceManager.getDefaultSharedPreferences(ctxt).
				getString(SettingsFragment.KEY_PREF_APP_THEME, "");

		if (theme.equals(SettingsFragment.APP_THEME_LIGHT)) {
			act.setTheme(android.R.style.Theme_Holo_Light);
		} else {
			act.setTheme(android.R.style.Theme_Holo);
		}
	}

	/**
	 * Shortens text with given length with "..." appended at the end
	 * @param s string to shorten
	 * @param len maximum length that can be shown (including ...)
	 * @return String with shortened string
	 */
	public static String shorten(String s, int len) {
		s = s.trim();
		if (s.length() > 12) {
			s = s.substring(0, len - 3).trim() + "...";
		}
		return s;
	}
}
