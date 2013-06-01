package com.example.budgetmanager.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.EntryLogAdapter;

import java.util.ArrayList;
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
	 * Ensure that an EntryLogAdaptor created with a Budget with two
	 * Entries creates a list with two Entries.
	 *
	 * This is a blackbox test.
	 */
	@SmallTest
	public void test_singleBudgetConstructor_hasEntries_shouldListEntries() {
		Budget budget = Factory.budgetFactory(0, 2);

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budget);

		List<Entry> logEntryList = log.getEntryList();

		// Should have a total of 2 entries
		assertEquals(2, logEntryList.size());
	}

	/**
	 * Ensure that an EntryLogAdaptor created with a Budget without any
	 * Entries creates an empty list of Entries.
	 *
	 * This is a blackbox test.
	 */
	@SmallTest
	public void test_singleBudgetConstructor_hasNoEntries_shouldListNoEntries() {
		Budget budgets = Factory.budgetFactory(0, 0);

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		List<Entry> logEntryList = log.getEntryList();

		// Should have no entries
		assertTrue(logEntryList.isEmpty());
	}

	/**
	 * Ensure that an EntryLogAdaptor created with a list of Budgets containing
	 * a total of six entries returns a list with six entries.
	 *
	 * This is a blackbox test.
	 */
	@SmallTest
	public void test_listBudgetConstructor_hasEntries_shouldListAllEntries() {
		List<Budget> budgets = Factory.budgetListFactory(1, 2, 3);

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		List<Entry> logEntryList = log.getEntryList();

		// Should have a total of 6 entries
		assertEquals(6, logEntryList.size());
	}

	/**
	 * Ensure that an EntryLogAdaptor created with multiple Budgets without any
	 * Entries creates an empty list of Entries.
	 *
	 * This is a blackbox test.
	 */
	@SmallTest
	public void test_listBudgetConstructor_hasNoEntries_shouldListNoEntries() {
		List<Budget> budgets = Factory.budgetListFactory(0, 0, 0);

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		List<Entry> logEntryList = log.getEntryList();

		// Should have no entries
		assertTrue(logEntryList.isEmpty());
	}

	/**
	 * Ensure that an EntryLogAdaptor created with multiple Budgets that contain
	 * a total of four Entries, some with and some without Entries, creates a
	 * list with four Entries.
	 *
	 * This is a blackbox test.
	 */
	@SmallTest
	public void test_listBudgetConstructor_hasMixedEntries_shouldListAllEntries() {
		List<Budget> budgets = Factory.budgetListFactory(1, 0, 3);

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		List<Entry> logEntryList = log.getEntryList();

		// Should have a total of 4 entries
		assertEquals(4, logEntryList.size());
	}

	/**
	 * Ensure that an EntryLogAdaptor created with an empty list of Budgets
	 * creates an empty list of Entries.
	 *
	 * This is a blackbox test.
	 */
	@SmallTest
	public void test_listBudgetConstructor_emptyList_shouldListNoEntries() {
		List<Budget> budgets = new ArrayList<Budget>();

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		List<Entry> logEntryList = log.getEntryList();

		// Should have no entries
		assertTrue(logEntryList.isEmpty());
	}

	/**
	 * Ensure that an EntryLogAdaptor that gets a Budget with three Entries
	 * added to it with no prior Budgets creates a list of three Entries.
	 *
	 * This is a blackbox test.
	 */
	@SmallTest
	public void test_addEntriesFromBudget_hasEntriesNoPriorBudgets_shouldListAllEntries() {
		List<Budget> budgets = new ArrayList<Budget>();

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		log.addEntriesFromBudget(Factory.budgetFactory(0, 3));

		List<Entry> logEntryList = log.getEntryList();

		// Should have a total of 3 entries
		assertEquals(3, logEntryList.size());
	}

	/**
	 * Ensure that an EntryLogAdaptor already containing three Budgets
	 * with a total of six Entries returns a list of ten Entries after
	 * a Budget with four Entries gets added to it
	 *
	 * This is a blackbox test.
	 */
	@SmallTest
	public void test_addEntriesFromBudget_hasEntriesPriorBudgets_shouldListAllEntries() {
		List<Budget> budgets = Factory.budgetListFactory(1, 2, 3);

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		log.addEntriesFromBudget(Factory.budgetFactory(0, 4));

		List<Entry> logEntryList = log.getEntryList();

		// Should have a total of 10 entries
		assertEquals(10, logEntryList.size());
	}

	/**
	 * Ensure that an EntryLogAdaptor that had been created with an empty
	 * list of Budgets returns an empty list of Entries after a Budget with
	 * no Entries gets added to it.
	 *
	 * This is a blackbox test.
	 */
	@SmallTest
	public void test_addEntriesFromBudget_hasNoEntriesNoPriorBudgets_shouldListNoEntries() {
		List<Budget> budgets = new ArrayList<Budget>();

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		log.addEntriesFromBudget(Factory.budgetFactory(0, 0));

		List<Entry> logEntryList = log.getEntryList();

		// Should have no entries
		assertTrue(logEntryList.isEmpty());
	}

	/**
	 * Ensure that an EntryLogAdaptor created without a list of Budgets
	 * containing a total of six Entries returns a list of six Entries
	 * after a Budget with no Entries gets added to it.
	 *
	 * This is a blackbox test.
	 */
	@SmallTest
	public void test_addEntriesFromBudget_hasNoEntriesPriorBudgets_shouldListAllEntries() {
		List<Budget> budgets = Factory.budgetListFactory(1, 2, 3);

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		log.addEntriesFromBudget(Factory.budgetFactory(0, 0));

		List<Entry> logEntryList = log.getEntryList();

		// Should have a total of 6 entries
		assertEquals(6, logEntryList.size());
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

	/**
	 * Ensure that an EntryLogAdaptor created with a list of three Budgets
	 * with a total of six Entries returns an empty list of Entries after
	 * calling clear() on it.
	 *
	 * This is a blackbox test.
	 */
	@SmallTest
	public void test_clear_wasNotEmpty_shouldBeEmpty() {
		List<Budget> budgets = Factory.budgetListFactory(1, 2, 3);

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		log.clear();

		List<Entry> logEntryList = log.getEntryList();

		// Should have no entries after clearing
		assertTrue(logEntryList.isEmpty());
	}
}
