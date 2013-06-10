package com.example.budgetmanager.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Utilities;

import junit.framework.TestCase;

/**
 * Tests our {@link Utilities} class, and it's functions.
 */
public class TestCaseUtilities extends TestCase {

	/**
	 * Perform preliminary set-up of the tests.
	 * Namely, clears out all cached budgets.
	 */
	@Override
	protected void setUp() {
		Budget.clearBudgets();
	}

	/**
	 * Tests our amount to dollars for an amount with
	 * zero cents.
	 */
	@SmallTest
	public void test_amountToDollars_zeroCents() {
		int amount = 0;
		String expected = "$0.00";
		String actual = Utilities.amountToCurrency(amount);
		assertEquals(expected, actual);
	}

	/**
	 * Tests our amount to dollars for an amount less
	 * than one dollar.
	 */
	@SmallTest
	public void test_amountToDollars_lessThanOneDollar() {
		int amount = 19;
		String expected = "$0.19";
		String actual = Utilities.amountToCurrency(amount);
		assertEquals(expected, actual);
	}

	/**
	 * Tests our amount to dollars for an amount less
	 * than ten cents.
	 */
	@SmallTest
	public void test_amountToDollars_lessThanTenCents() {
		int amount = 9;
		String expected = "$0.09";
		String actual = Utilities.amountToCurrency(amount);
		assertEquals(expected, actual);
	}

	/**
	 * Tests our amount to dollars for an amount higher than
	 * 2 dollar digits with no cents.
	 */
	@SmallTest
	public void test_amountToDollars_moreThanOneNoCents() {
		int amount = 2000;
		String expected = "$20.00";
		String actual = Utilities.amountToCurrency(amount);
		assertEquals(expected, actual);
	}

	/**
	 * Tests our amount to dollars for an amount higher than
	 * 2 dollar digits with cents.
	 */
	@SmallTest
	public void test_amountToDollars_moreThanOneWithCents() {
		int amount = 2019;
		String expected = "$20.19";
		String actual = Utilities.amountToCurrency(amount);
		assertEquals(expected, actual);
	}

	/**
	 * Tests our amount to dollars for an amount higher than
	 * 2 dollar digits with less than 10 cents.
	 */
	@SmallTest
	public void test_amountToDollars_moreThanOneWithLessThanTenCents() {
		int amount = 2009;
		String expected = "$20.09";
		String actual = Utilities.amountToCurrency(amount);
		assertEquals(expected, actual);
	}
}
