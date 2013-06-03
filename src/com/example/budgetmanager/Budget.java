package com.example.budgetmanager;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is the budget object. It keeps track of all the entries
 * in a specific budget and stores the information related to
 * budget amounts and time restrictions.
 *
 * @author Graham grahamb5
 */
public class Budget {
	/**
	 * The possible types durations that can be chosen for a budget
	 * @author Graham grahamb5
	 *
	 */
	public static enum Duration {
		DAY, WEEK, FORTNIGHT, MONTH, YEAR
	}

	public static final long NEW_ID = -1;

	// ID that identifies the budget
	private long budgetId;

	// Name of the budget
	private String name;

	// Amount allocated for the budget, in cents
	private int amount;

	// true for recurring Budget
	private boolean recur;

	// The start date of the first cycle
	private LocalDate startDate;

	// Duration type for this Budget
	private Duration duration;

	// Actual period of the budget (length of one cycle)
	private Period budgetDuration;

	// List of entries associated with this Budget
	private final List<Entry> entries;

	// Hold the list of all loaded budgets
	private static final List<Budget> BUDGET_LIST;

	static {
		BUDGET_LIST = new ArrayList<Budget>();
	}

	/**
	 * Create a new <code>Budget</code>. This also adds the created budget in
	 * the list of all budgets.
	 *
	 * @param name The name of the <code>Budget</code>.
	 * @param amount The amount in cents allowed in this <code>Budget</code>.
	 * @param recur Whether this <code>Budget</code> recurs.
	 * @param startDate The start date of this <code>Budget</code>.
	 * @param duration The type of duration in this <code>Budget</code>.
	 */
	public Budget(String name, int amount, boolean recur,
			LocalDate startDate, Duration duration) {
		this.name = name;
		this.amount = amount;
		this.recur = recur;
		this.startDate = startDate;
		this.duration = duration;
		this.entries = new ArrayList<Entry>();
		this.budgetId = NEW_ID;

		// Set up a period based on the requested duration type
		switch(this.duration) {
		case DAY:
			this.budgetDuration = Period.days(1);
			break;
		case WEEK:
			this.budgetDuration = Period.weeks(1);
			break;
		case FORTNIGHT:
			this.budgetDuration = Period.weeks(2);
			break;
		case MONTH:
			this.budgetDuration = Period.months(1);
			break;
		case YEAR:
			this.budgetDuration = Period.years(1);
			break;
		default:
			throw new IllegalArgumentException("Invaid duration argument");
		}

		// Throw it into the main Budget list at the beginning of the list.
		BUDGET_LIST.add(0, this);
	}

	/**
	 * Returns a list of all Budgets tracked by the current user
	 *
	 * @return An unmodifiable list of all budgets
	 */
	public static List<Budget> getBudgets() {
		return Collections.unmodifiableList(BUDGET_LIST);
	}

	/** Search through the user's Budgets for the budget who's ID matches
	 * <code>id</code>.
	 *
	 * @param id The ID of the budget in question.
	 * @return The budget with <code>id</code>
	 */
	public static Budget getBudgetById(long id) {
		for (Budget b : BUDGET_LIST) {
			if (b.getId() == id) {
				return b;
			}
		}
		return null;
	}

	/**
	 * Clears the internal list of Budgets, mainly used for testing.
	 */
	public static void clearBudgets() {
		BUDGET_LIST.clear();
	}

	/**
	 * Remove a budget from the budget list (for use when deleting)
	 *
	 * @param budget the Budget to be removed
	 *
	 * @return true if the Budget list was modified (ie. the budget was
	 * removed), false otherwise.
	 */
	public static boolean removeBudget(Budget budget) {
		return BUDGET_LIST.remove(budget);
	}

	/**
	 * Sets this <code>Budget</code> ID to be <code>budgetId</code>.
	 *
	 * @param budgetId The ID of this <code>Budget</code>.
	 */
	public void setId(long budgetId) {
		this.budgetId = budgetId;
	}

	/**
	 * Gets the <code>Budget</code> ID.
	 *
	 * @return The <code>Budget</code> ID.
	 */
	public long getId() {
		return budgetId;
	}

	/**
	 * Adds a new <code>entry</code> to the <code>Budget</code>.
	 *
	 * @param entry The entry to add.
	 * @throws IllegalArgumentException if <code>entry</code> already
	 * exists in this budget, or if <code>entry</code> is
	 * <code>null</code>
	 */
	public void addEntry(Entry entry) {
		if (entry == null) {
			throw new IllegalArgumentException("Tried to add a null Entry");
		}
		if (entries.contains(entry)) {
			throw new IllegalArgumentException("Tried to add entry to "
					+ "budget that already contained it.");
		}
		entries.add(entry);
	}

	/**
	 * Removes a specified <code>entry</code> from this budget.
	 *
	 * @param entry The entry to remove.
	 * @throws IllegalArgumentException if <code>entry</code> is not an
	 * 									entry of this budget.
	 */
	public void removeEntry(Entry entry) {
		if (entry == null) {
			throw new IllegalArgumentException("Tried to remove a null Entry");
		}
		if (!entries.contains(entry)) {
			throw new IllegalArgumentException("Tried to remove entry from "
					+ "budget that did not contain it.");
		}
		entries.remove(entry);
	}

	/**
	 * Get the list of entries associated with this Budget
	 * @return the (unmodifiable) list of entries in this Budget
	 */
	public List<Entry> getEntries() {
		return Collections.unmodifiableList(entries);
	}

	/**
	 * Search through the budget's entries for the entry who's ID matches
	 * <code>id</code>.
	 *
	 * @param id The ID of the Entry in question.
	 * @return The entry with <code>id</code>
	 * @throws IllegalArgumentException if no entry with <code>id</code> exists.
	 */
	public Entry getEntryById(long id) {
		for (Entry e : entries) {
			if (id == e.getEntryId()) {
				return e;
			}
		}
		throw new IllegalArgumentException("No entry with id, " + id + ", found.");
	}

	/**
	 * Gets and returns the user-specified name of this <code>Budget</code>.
	 *
	 * @return The name of this <code>Budget</code>.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this budget
	 *
	 * @param name The new name of this budget
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets and returns the user-specified total budget amount of this
	 * <code>Budget</code>. This number is the maximum amount of cents
	 * the user can spend without going over-budget.
	 *
	 * @return The amount of money allowed in this budget, in cents.
	 */
	public int getBudgetAmount() {
		return amount;
	}

	/**
	 * Sets the amount of this budget
	 *
	 * @param amount The new amount of this budget
	 */
	public void setBudgetAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * Determines if this <code>Budget</code> recurs or not. If it
	 * does recur, it will return <code>true</code>. If not, it will
	 * return <code>false</code>.
	 *
	 * @return Whether this <code>Budget</code> recurs or not.
	 */
	public boolean isRecurring() {
		return recur;
	}

	/**
	 * Sets whether this budget is recurring or not
	 *
	 * @param recur if it is recurring or not
	 */
	public void setRecurring(boolean recur) {
		this.recur = recur;
	}

	/**
	 * Returns the duration type of this budget
	 * @return the duration of this budget
	 */
	public Duration getDuration() {
		return duration;
	}

	/**
	 * Sets the duration of this budget
	 *
	 * @param duration the new duration of this budget
	 */
	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	/**
	 * Calculates the current cycle count of this <code>Budget</code>.
	 *
	 * @return The current cycle count, where the first cycle is 0, or -1 if
	 * it is currently before the start of the budget.
	 */
	public int getCurrentCycle() {
		LocalDate now = LocalDate.now();
		if (startDate.isAfter(now)) {
			return -1;
		} else {
			int cycle = -1;
			LocalDate startOfPeriod = startDate;
			while (startOfPeriod.isBefore(now) || startOfPeriod.equals(now)) {
				++cycle;
				startOfPeriod = startOfPeriod.plus(budgetDuration);
			}
			return cycle;
		}
	}

	/**
	 * Determines if this <code>Budget</code> is still active or not.
	 *
	 * @return <code>true</code> if the <code>Budget</code> is active,
	 *         <code>false</code> otherwise.
	 */
	public boolean isActive() {
		int currentCycle = getCurrentCycle();
		return currentCycle == 0 || (recur && currentCycle >= 0);
	}

	/**
	 * Gets the start date of the given cycle
	 * @param cycle the cycle number to get the start date from.
	 * @return The start date of the cycle
	 * @throws IllealArgumentException If the cycle is negative
	 */
	public LocalDate getStartDate(int cycle) {
		if (cycle < 0) {
			throw new IllegalArgumentException("Cycle was negative: " + cycle);
		}
		return startDate.withPeriodAdded(budgetDuration, cycle);
	}

	/**
	 * Returns the absolute start date of this budget, on its first iteration.
	 * @return the start date of this budget
	 */
	public LocalDate getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date of this budget
	 *
	 * @param startDate the new start date of this budget
	 */
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	/**
	 * Returns the end date of the given cycle.
	 * @param cycle The cycle to calculate the end time of.
	 * @return The end time, in milliseconds, of the <code>cycle</code>.
	 * @throws IllegalArgumentException If the cycle is negative
	 */
	public LocalDate getEndDate(int cycle) {
		if (cycle < 0) {
			throw new IllegalArgumentException("Cycle was negative: " + cycle);
		}
		return startDate.withPeriodAdded(budgetDuration,
				cycle + 1).minusDays(1);
	}

	/**
	 * Returns the amount spent within the given cycle
	 *
	 * @param cycle The cycle to calculate the amount
	 * @return the cumulative sum of amount spent in cents
	 */
	public int getAmountSpent(int cycle) {
		int amount = 0;
		LocalDate startDate = getStartDate(cycle).minusDays(1);
		LocalDate endDate = getEndDate(cycle).plusDays(1);
		for (Entry e : entries) {
			LocalDate usedDate = e.getDate();
			if (usedDate.isAfter(startDate) && usedDate.isBefore(endDate)) {
				amount += e.getAmount();
			}
		}
		return amount;
	}

	/**
	 * Returns the amount spent within the current cycle
	 *
	 * @return the cumulative sum of amount spent in cents
	 */
	public int getAmountSpent() {
		return getAmountSpent(getCurrentCycle());
	}
}