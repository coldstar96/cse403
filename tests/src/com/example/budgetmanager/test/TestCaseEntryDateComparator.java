package com.example.budgetmanager.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.EntryLogAdapter;

import junit.framework.TestCase;

/**
 * TDD-style tests for the EntryLogAdapter.EntryDateComparator class
 */
public class TestCaseEntryDateComparator extends TestCase {
	@SmallTest
	public void test_compare_laterToEarlier_shouldBeNegative() {
		Budget budget = Factory.budgetFactory(1, 0);

		Entry earlier = Factory.entryFactory(1, budget);
		Entry later = Factory.entryFactory(2, budget);

		EntryLogAdapter.EntryDateComparator comp =
				new EntryLogAdapter.EntryDateComparator();

		String msg = "Entry " + earlier.toString() +
				" should come after " + later.toString() +
				" in ordering by date";
		assertTrue(msg, comp.compare(later, earlier) < 0);
	}

	@SmallTest
	public void test_compare_same_shouldBeZero() {
		Budget budget = Factory.budgetFactory(1, 0);

		Entry rhsEntry = Factory.entryFactory(1, budget);
		Entry lhsEntry = Factory.entryFactory(1, budget);

		EntryLogAdapter.EntryDateComparator comp =
				new EntryLogAdapter.EntryDateComparator();

		String msg = "Entry " + rhsEntry.toString() +
				" should come equally " + lhsEntry.toString() +
				" in ordering by date";
		assertEquals(msg, 0, comp.compare(lhsEntry, rhsEntry));
	}

	@SmallTest
	public void test_compare_earlierToLater_shouldBePositive() {
		Budget budget = Factory.budgetFactory(1, 0);

		Entry earlier = Factory.entryFactory(1, budget);
		Entry later = Factory.entryFactory(2, budget);

		EntryLogAdapter.EntryDateComparator comp =
				new EntryLogAdapter.EntryDateComparator();

		String msg = "Entry " + later.toString() +
				" should come before " + earlier.toString() +
				" in ordering by date";
		assertTrue(msg, comp.compare(earlier, later) > 0);
	}
}
