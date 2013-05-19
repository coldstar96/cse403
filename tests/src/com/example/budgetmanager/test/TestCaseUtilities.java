package com.example.budgetmanager.test;

import com.example.budgetmanager.Utilities;

import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.TestCase;

public class TestCaseUtilities extends TestCase {

	@SmallTest
	public void test_amountToDollars_zeroCents() {
		int amount = 0;
		String expected = "$0.00";
		String actual = Utilities.amountToDollars(amount);
		assertEquals(expected, actual);
	}

	@SmallTest
	public void test_amountToDollars_lessThanOneDollar() {
		int amount = 19;
		String expected = "$0.19";
		String actual = Utilities.amountToDollars(amount);
		assertEquals(expected, actual);
	}

	@SmallTest
	public void test_amountToDollars_lessThanTenCents() {
		int amount = 9;
		String expected = "$0.09";
		String actual = Utilities.amountToDollars(amount);
		assertEquals(expected, actual);
	}

	@SmallTest
	public void test_amountToDollars_moreThanOneNoCents() {
		int amount = 2000;
		String expected = "$20.00";
		String actual = Utilities.amountToDollars(amount);
		assertEquals(expected, actual);
	}

	@SmallTest
	public void test_amountToDollars_moreThanOneWithCents() {
		int amount = 2019;
		String expected = "$20.19";
		String actual = Utilities.amountToDollars(amount);
		assertEquals(expected, actual);
	}

	@SmallTest
	public void test_amountToDollars_moreThanOneWithLessThanTenCents() {
		int amount = 2009;
		String expected = "$20.09";
		String actual = Utilities.amountToDollars(amount);
		assertEquals(expected, actual);
	}
}