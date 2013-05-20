package com.example.budgetmanager.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.EntryLogAdapter;

import junit.framework.TestCase;

/**
 * TDD-style tests for the EntryLogAdapter.EntryDateComparator class
 * 
 * Black-box tests.
 * 
 * @author Chris brucec5
 */
public class TestCaseEntryDateComparator extends TestCase {
	/**
	 * Checks that comparing an entry with a later date
	 * to an entry with an earlier date produces a negative
	 * result from the comparator. Black-box test.
	 */
	@SmallTest
	public void test_compare_laterToEarlier_shouldBeNegative() {
		Budget budget = Factory.budgetFactory(1, 0);

		Entry earlier = Factory.entryFactory(1, budget);
		Entry later = Factory.entryFactory(2, budget);

		EntryLogAdapter.EntryDateComparator comp =
				new EntryLogAdapter.EntryDateComparator();

		String msg = "Entry " + earlier.getDate().toString() +
				" should come after " + later.getDate().toString() +
				" in ordering by date";
		assertTrue(msg, comp.compare(later, earlier) < 0);
	}

	/**
	 * Checks that comparing two entries with the same date
	 * produces a equal (zero) outcome from the comparator.
	 * Black-box test.
	 */
	@SmallTest
	public void test_compare_same_shouldBeZero() {
		Budget budget = Factory.budgetFactory(1, 0);

		Entry rhsEntry = Factory.entryFactory(1, budget);
		Entry lhsEntry = Factory.entryFactory(1, budget);

		EntryLogAdapter.EntryDateComparator comp =
				new EntryLogAdapter.EntryDateComparator();

		String msg = "Entry " + rhsEntry.getDate().toString() +
				" should come equally " + lhsEntry.getDate().toString() +
				" in ordering by date";
		assertEquals(msg, 0, comp.compare(lhsEntry, rhsEntry));
	}

	/**
	 * Checks that comparing an entry with an earlier date
	 * to an entry with a later date produces a negative
	 * result from the comparator. Black-box test.
	 */
	@SmallTest
	public void test_compare_earlierToLater_shouldBePositive() {
		Budget budget = Factory.budgetFactory(1, 0);

		Entry earlier = Factory.entryFactory(1, budget);
		Entry later = Factory.entryFactory(2, budget);

		EntryLogAdapter.EntryDateComparator comp =
				new EntryLogAdapter.EntryDateComparator();

		String msg = "Entry " + later.getDate().toString() +
				" should come before " + earlier.getDate().toString() +
				" in ordering by date";
		assertTrue(msg, comp.compare(earlier, later) > 0);
	}
}
