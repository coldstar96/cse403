package com.example.budgetmanager.test;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.EntryLogAdapter;

/**
 * TDD-style tests for the EntryLogAdapter.EntryBudgetComparator class
 */
public class TestCaseEntryBudgetComparator extends TestCase {
	@SmallTest
	public void test_compare_moreToLess_shouldBeNegative() {
		Budget lessBudget = Factory.budgetFactory(1, 0);
		Budget moreBudget = Factory.budgetFactory(2, 0);

		Entry less = Factory.entryFactory(1, lessBudget);
		Entry more = Factory.entryFactory(1, moreBudget);

		EntryLogAdapter.EntryBudgetComparator comp =
				new EntryLogAdapter.EntryBudgetComparator();

		String msg = "Entry with " + lessBudget.getName() +
				" should come after " + moreBudget.getName() +
				" in ordering by budget name";
		assertTrue(msg, comp.compare(more, less) < 0);
	}

	@SmallTest
	public void test_compare_same_shouldBeZero() {
		Budget rhsBudget = Factory.budgetFactory(1, 0);
		Budget lhsBudget = Factory.budgetFactory(1, 0);

		Entry rhsEntry = Factory.entryFactory(1, rhsBudget);
		Entry lhsEntry = Factory.entryFactory(1, lhsBudget);

		EntryLogAdapter.EntryBudgetComparator comp =
				new EntryLogAdapter.EntryBudgetComparator();

		String msg = "Entry with " + rhsBudget.getName() +
				" should come equally " + lhsBudget.getName() +
				" in ordering by budget name";
		assertEquals(msg, 0, comp.compare(lhsEntry, rhsEntry));
	}

	@SmallTest
	public void test_compare_lessToMore_shouldBePositive() {
		Budget lessBudget = Factory.budgetFactory(1, 0);
		Budget moreBudget = Factory.budgetFactory(2, 0);

		Entry less = Factory.entryFactory(1, lessBudget);
		Entry more = Factory.entryFactory(1, moreBudget);

		EntryLogAdapter.EntryBudgetComparator comp =
				new EntryLogAdapter.EntryBudgetComparator();

		String msg = "Entry with " + moreBudget.getName() +
				" should come before " + lessBudget.getName() +
				" in ordering by budget name";
		assertTrue(msg, comp.compare(less, more) > 0);
	}
}
