package com.example.budgetmanager.test;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Entry;

public class Factory {
	/**
	 * Base date used by the Entry/Budget factories
	 */
	private static final LocalDate BASE_DATE = new LocalDate(2013, 05, 05);

	/**
	 * Base amount used by the Entry/Budget factories
	 */
	private static final int BASE_AMOUNT = 100;

	/**
	 * Constructs an entry based on a seed and a budget.
	 *
	 * @param seed Controls the date and amount of this budget.
	 * The date is <code>seed</code> days past <code>BASE_DATE</code>, and
	 * the amount is <code>seed * BASE_AMOUNT</code>
	 * @param budget The budget this entry is associated with
	 *
	 * @return A new entry with the above attributes.
	 */
	public static Entry entryFactory(int seed, Budget budget) {
		int amount = BASE_AMOUNT * seed;
		LocalDate date = BASE_DATE.plusDays(seed);
		LocalDateTime creationAndUpdateTime =
				date.toLocalDateTime(new LocalTime(seed, seed));
		String notes = "Entry " + seed;

		Entry entry = new Entry(amount, budget, notes, date);
		entry.setCreatedAt(creationAndUpdateTime);
		entry.setUpdatedAt(creationAndUpdateTime);

		return entry;
	}

	/**
	 * Constructs a factory based on a number and the number of entries it
	 * should have.
	 *
	 * @param num The "number" of this budget. Causes this budget to have the
	 * name "Budget &lt;num&gt;".
	 * @param entryCount The number of entries to create within this budget
	 *
	 * @return a new budget with the above attributes.
	 */
	public static Budget budgetFactory(int num, int entryCount) {
		String name = "Budget " + num;

		Budget b = new Budget(name, BASE_AMOUNT, true,
				BASE_DATE, Budget.Duration.MONTH);

		for (int i = 0; i < entryCount; ++i) {
			b.addEntry(entryFactory(i + 1, b));
		}

		return b;
	}

	/**
	 * Creates a list of Budgets with specified numbers of Entries in each
	 *
	 * @param entryCount A vararg array of entry counts. entryCount.length is
	 * the number of Budgets that are to be created, and the individual counts
	 * controls the number of Entries each individual Budget will have
	 *
	 * @return A list of Budgets with the appropriate Entries created.
	 */
	public static List<Budget> budgetListFactory(int... entryCount) {
		List<Budget> budgets = new ArrayList<Budget>();

		for (int i = 0; i < entryCount.length; ++i) {
			Budget b = budgetFactory(i, entryCount[i]);
			budgets.add(b);
		}

		return budgets;
	}
}
