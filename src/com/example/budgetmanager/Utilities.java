package com.example.budgetmanager;

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
	public static int dateDifference(LocalDate start, LocalDate end){
		PeriodType monthDay = PeriodType.yearMonthDay();
		int days = new Period(start, end, monthDay).getDays() + 1;
		return days;
	}
}
