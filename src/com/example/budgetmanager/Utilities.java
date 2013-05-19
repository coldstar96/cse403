package com.example.budgetmanager;

/**
 * Miscellaneous methods that we use in routine
 *
 * @author Chi Ho coldstar96
 *
 */

public class Utilities {
	public static final int DOLLOR_IN_CENTS = 100;
	public static final int CENTS_SPECIAL_CASE = 10;

	private Utilities() {
		// not called
	}

	/**
	 *
	 * @param n amount in cents
	 * @return String in $00.00 format
	 */
	public static String amountToDollars(int n) {
		String s = "$ " + (n / DOLLOR_IN_CENTS) + ".";
		if ((n % DOLLOR_IN_CENTS) < CENTS_SPECIAL_CASE) {
			s += "0";
		}
		s += (n % DOLLOR_IN_CENTS);
		return s;
	}
}