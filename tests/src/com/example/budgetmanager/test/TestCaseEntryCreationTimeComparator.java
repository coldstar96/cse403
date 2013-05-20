package com.example.budgetmanager.test;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.EntryLogAdapter;

import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.TestCase;

/**
 * TDD-style tests for the EntryLogAdapter.EntryCreationTimeComparator class
 * 
 * Black-box tests.
 * 
 * @author Chris brucec5
 */
public class TestCaseEntryCreationTimeComparator extends TestCase {
	/**
	 * Checks that comparing an entry with a later creation time
	 * to an entry with an earlier creation time produces a negative
	 * result from the comparator. Black-box test.
	 */
	@SmallTest
	public void test_compare_laterToEarlier_shouldBeNegative() {
		Budget budget = Factory.budgetFactory(1, 0);

		Entry earlier = Factory.entryFactory(1, budget);
		Entry later = Factory.entryFactory(2, budget);

		EntryLogAdapter.EntryCreationTimeComparator comp =
				new EntryLogAdapter.EntryCreationTimeComparator();

		String msg = "Entry " + earlier.getCreatedAt().toString() +
				" should come after " + later.getCreatedAt().toString() +
				" in ordering by creation time";
		assertTrue(msg, comp.compare(later, earlier) < 0);
	}

	/**
	 * Checks that the comparator returns an equal (zero) value from
	 * two entries with an identical creation time. Black-box test.
	 */
	@SmallTest
	public void test_compare_same_shouldBeZero() {
		Budget budget = Factory.budgetFactory(1, 0);

		Entry earlier = Factory.entryFactory(1, budget);
		Entry later = Factory.entryFactory(1, budget);

		EntryLogAdapter.EntryCreationTimeComparator comp =
				new EntryLogAdapter.EntryCreationTimeComparator();

		String msg = "Entry " + earlier.getCreatedAt().toString() +
				" should come equally " + later.getCreatedAt().toString() +
				" in ordering by creation time";
		assertEquals(msg, 0, comp.compare(later, earlier));
	}

	/**
	 * Checks that comparing an entry with an earlier creation time
	 * to an entry with a later creation time produces a positive
	 * result from the comparator. Black-box test.
	 */
	@SmallTest
	public void test_compare_earlierToLater_shouldBePositive() {
		Budget budget = Factory.budgetFactory(1, 0);

		Entry earlier = Factory.entryFactory(1, budget);
		Entry later = Factory.entryFactory(2, budget);

		EntryLogAdapter.EntryCreationTimeComparator comp =
				new EntryLogAdapter.EntryCreationTimeComparator();

		String msg = "Entry " + later.getCreatedAt().toString() +
				" should come before " + earlier.getCreatedAt().toString() +
				" in ordering by creation time";
		assertTrue(msg, comp.compare(earlier, later) > 0);
	}
}
