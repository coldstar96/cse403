package com.example.budgetmanager.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.EntryLogAdapter;

import java.util.List;

/**
 * TDD-style tests for the EntryLogAdapter class
 *
 * @author chris brucec5
 */
public class TestCaseEntryLogAdapter extends AndroidTestCase {

	/**
	 * Perform preliminary set-up of the tests.
	 * Namely, clears out all cached budgets.
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Budget.clearBudgets();
	}

	/**
	 * Ensure that an EntryLogAdaptor created without a list of Budgets
	 * creates an empty list of Entries.
	 *
	 * This is a blackbox test.
	 */
	@SmallTest
	public void test_noBudgetConstructor_shouldBeEmpty() {
		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout);

		List<Entry> logEntryList = log.getEntryList();

		assertTrue(logEntryList.isEmpty());
	}


	/**
	 * Ensure that an EntryLogAdaptor created without a list of Budgets
	 * returns an empty list of Entries after calling clear() on it.
	 *
	 * This is a blackbox test.
	 */
	@SmallTest
	public void test_clear_wasEmpty_shouldBeEmpty() {
		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout);

		log.clear();

		List<Entry> logEntryList = log.getEntryList();

		// Should have no entries after clearing
		assertTrue(logEntryList.isEmpty());
	}
}
