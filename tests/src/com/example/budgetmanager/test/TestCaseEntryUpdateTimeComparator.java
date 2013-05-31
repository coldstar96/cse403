package com.example.budgetmanager.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.EntryLogAdapter;

import junit.framework.TestCase;

/**
 * TDD-style tests for the EntryLogAdapter.EntryUpdateTimeComparator class
 *
 * Black-box tests.
 *
 * @author Chris brucec5
 */
public class TestCaseEntryUpdateTimeComparator extends TestCase {

	/**
	 * Perform preliminary set-up of the tests.
	 * Namely, clears out all cached budgets.
	 */
	protected void setUp() {
		Budget.clearBudgets();
	}

	/**
	 * Checks that comparing an entry with a later update time
	 * to an entry with an earlier update time produces a negative
	 * result from the comparator. Black-box test.
	 */
	@SmallTest
	public void test_compare_laterToEarlier_shouldBeNegative() {
		Budget budget = Factory.budgetFactory(1, 0);

		Entry earlier = Factory.entryFactory(1, budget);
		Entry later = Factory.entryFactory(2, budget);

		EntryLogAdapter.EntryUpdateTimeComparator comp =
				new EntryLogAdapter.EntryUpdateTimeComparator();

		String msg = "Entry " + earlier.getUpdatedAt().toString() +
				" should come after " + later.getUpdatedAt().toString() +
				" in ordering by update time";
		assertTrue(msg, comp.compare(later, earlier) < 0);
	}

	/**
	 * Checks that the comparator returns an equal (zero) value from
	 * two entries with an identical update time. Black-box test.
	 */
	@SmallTest
	public void test_compare_same_shouldBeZero() {
		Budget budget = Factory.budgetFactory(1, 0);

		Entry earlier = Factory.entryFactory(1, budget);
		Entry later = Factory.entryFactory(1, budget);

		EntryLogAdapter.EntryUpdateTimeComparator comp =
				new EntryLogAdapter.EntryUpdateTimeComparator();

		String msg = "Entry " + earlier.getUpdatedAt().toString() +
				" should come equally " + later.getUpdatedAt().toString() +
				" in ordering by update time";
		assertEquals(msg, 0, comp.compare(later, earlier));
	}

	/**
	 * Checks that comparing an entry with an earlier update time
	 * to an entry with a later update time produces a positive
	 * result from the comparator. Black-box test.
	 */
	@SmallTest
	public void test_compare_earlierToLater_shouldBePositive() {
		Budget budget = Factory.budgetFactory(1, 0);

		Entry earlier = Factory.entryFactory(1, budget);
		Entry later = Factory.entryFactory(2, budget);

		EntryLogAdapter.EntryUpdateTimeComparator comp =
				new EntryLogAdapter.EntryUpdateTimeComparator();

		String msg = "Entry " + later.getUpdatedAt().toString() +
				" should come before " + earlier.getUpdatedAt().toString() +
				" in ordering by update time";
		assertTrue(msg, comp.compare(earlier, later) > 0);
	}
}
