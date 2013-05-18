package com.example.budgetmanager.test;

import java.util.ArrayList;
import java.util.List;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.EntryLogAdapter;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * TDD-style tests for the EntryLogAdapter class
 */
public class TestCaseEntryLogAdapter extends AndroidTestCase {

	@SmallTest
	public void test_singleBudgetConstructor_hasEntries_shouldListEntries() {
		Budget budgets = Factory.budgetFactory(0, 2);

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		List<Entry> logEntryList = log.getEntryList();

		// Should have a total of 2 entries
		assertEquals(2, logEntryList.size());
	}

	@SmallTest
	public void test_singleBudgetConstructor_hasNoEntries_shouldListNoEntries() {
		Budget budgets = Factory.budgetFactory(0, 0);

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		List<Entry> logEntryList = log.getEntryList();

		// Should have no entries
		assertTrue(logEntryList.isEmpty());
	}

	@SmallTest
	public void test_listBudgetConstructor_hasEntries_shouldListAllEntries() {
		List<Budget> budgets = Factory.budgetListFactory(1, 2, 3);

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		List<Entry> logEntryList = log.getEntryList();

		// Should have a total of 6 entries
		assertEquals(6, logEntryList.size());
	}

	@SmallTest
	public void test_listBudgetConstructor_hasNoEntries_shouldListNoEntries() {
		List<Budget> budgets = Factory.budgetListFactory(0, 0, 0);

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		List<Entry> logEntryList = log.getEntryList();

		// Should have no entries
		assertTrue(logEntryList.isEmpty());
	}

	@SmallTest
	public void test_listBudgetConstructor_hasMixedEntries_shouldListAllEntries() {
		List<Budget> budgets = Factory.budgetListFactory(1, 0, 3);

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		List<Entry> logEntryList = log.getEntryList();

		// Should have a total of 4 entries
		assertEquals(4, logEntryList.size());
	}

	@SmallTest
	public void test_listBudgetConstructor_emptyList_shouldListNoEntries() {
		List<Budget> budgets = new ArrayList<Budget>();

		EntryLogAdapter log = new EntryLogAdapter(getContext(),
				R.layout.entry_log_adapter_test_layout, budgets);

		List<Entry> logEntryList = log.getEntryList();

		// Should have no entries
		assertTrue(logEntryList.isEmpty());
	}

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
}
