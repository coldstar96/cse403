package com.example.budgetmanager.test;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.EntryLogAdapter;

import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.TestCase;

/**
 * TDD-style tests for the EntryLogAdapter.EntryUpdateTimeComparator class
 */
public class TestCaseEntryUpdateTimeComparator extends TestCase {
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
