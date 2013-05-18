package com.example.budgetmanager.test;

import android.test.suitebuilder.annotation.SmallTest;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.EntryLogAdapter;

import junit.framework.TestCase;

/**
 * TDD-style tests for the EntryLogAdapter.EntryAmountComparator class
 */
public class TestCaseEntryAmountComparator extends TestCase {
	@SmallTest
	public void test_compare_moreToLess_shouldBeNegative() {
		Budget budget = Factory.budgetFactory(1, 0);

		Entry less = Factory.entryFactory(1, budget);
		Entry more = Factory.entryFactory(2, budget);

		EntryLogAdapter.EntryAmountComparator comp =
				new EntryLogAdapter.EntryAmountComparator();

		String msg = "Entry with " + less.getAmount() +
				" should come after " + more.getAmount() +
				" in ordering by amount";
		assertTrue(msg, comp.compare(more, less) < 0);
	}

	@SmallTest
	public void test_compare_same_shouldBeZero() {
		Budget budget = Factory.budgetFactory(1, 0);

		Entry rhsEntry = Factory.entryFactory(1, budget);
		Entry lhsEntry = Factory.entryFactory(1, budget);

		EntryLogAdapter.EntryAmountComparator comp =
				new EntryLogAdapter.EntryAmountComparator();

		String msg = "Entry " + rhsEntry.getAmount() +
				" should come equally " + lhsEntry.getAmount() +
				" in ordering by amount";
		assertEquals(msg, 0, comp.compare(lhsEntry, rhsEntry));
	}

	@SmallTest
	public void test_compare_lessToMore_shouldBePositive() {
		Budget budget = Factory.budgetFactory(1, 0);

		Entry less = Factory.entryFactory(1, budget);
		Entry more = Factory.entryFactory(2, budget);

		EntryLogAdapter.EntryAmountComparator comp =
				new EntryLogAdapter.EntryAmountComparator();

		String msg = "Entry " + more.getAmount() +
				" should come before " + less.getAmount() +
				" in ordering by amount";
		assertTrue(msg, comp.compare(less, more) > 0);
	}
}
