package com.example.budgetmanager;


public class Utilities {
	
	/**
	 * 
	 * @param n amount in cents
	 * @return String in $00.00 format
	 */
	public static String amountToDollars(int n){
		String s = "$ " + (n / 100) + ".";
		if ((n % 100) < 10) {
			s += "0";
		}
		s += (n % 100);
		return s;
	}
}
