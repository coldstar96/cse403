package com.example.budgetmanager;

import java.util.Calendar;
import java.util.List;

/**
 * This is the budget object. It keeps track of all the entries
 * in a specific budget and stores the information related to
 * budget amounts and time restrictions.
 *
 * @author uBudget
 */
public class Budget {
	public static enum Duration {
		DAY, WEEK, FORTNIGHT, MONTH, YEAR, OTHER
	}

	public static final long NEW_ID = -1;
	public static final long ONE_DAY = 24*60*60*1000;

	private long budgetId;

	private String name;
	private int amount; 		// Amount allocated for the budget, in cents
	private int currentAmount;	// Memorized amount spent so far, in cents
	private boolean recur;		// true for recurring budget

	private Calendar startDate;
	private Duration duration;	// Duration type.
	private int otherDuration;		// Duration in days, for duration type OTHER.

	private List<Entry> entries;

	/**
	 * Create a new <code>Budget</code>.
	 *
	 * @param name The name of the <code>Budget</code>.
	 * @param amount The total amount in cents allowed in this <code>Budget</code>.
	 * @param currentAmount The current amount spent in this <code>Budget</code>.
	 * @param recur Whether this <code>Budget</code> recurs.
	 * @param startTime The start time, in milliseconds, of this <code>Budget</code>.
	 * @param durationType The type of duration in this <code>Budget</code>.
	 */
	public Budget(String name, int amount, int currentAmount, boolean recur,
			long startTime, String durationType) {
		this(name, amount, currentAmount, recur, startTime, durationType, 0);
	}

	/**
	 * Create a new <code>Budget</code>.
	 *
	 * @param name The name of the <code>Budget</code>.
	 * @param amount The total amount in cents allowed in this <code>Budget</code>.
	 * @param currentAmount The current amount spent in this <code>Budget</code>.
	 * @param recur Whether this <code>Budget</code> recurs.
	 * @param startTime The start time, in milliseconds, of this <code>Budget</code>.
	 * @param duration The type of duration in this <code>Budget</code>.
	 * @param otherDuration The other duration length, in days, if
	 *                      <code>durationType</code> is <code>OTHER</code>.
	 */
	public Budget(String name, int amount, int currentAmount, boolean recur,
			long startTime, String duration, int otherDuration) {
		this.name = name;
		this.amount = amount;
		this.currentAmount = currentAmount;
		this.recur = recur;
		this.startDate = Calendar.getInstance();
		this.startDate.setTimeInMillis(startTime);
		this.duration = Duration.valueOf(duration);
		this.otherDuration = otherDuration;
	}

	/**
	 * Sets this <code>Budget</code> ID to be <code>budgetId</code>.
	 *
	 * @param budgetId The ID of this <code>Budget</code>.
	 */
	public void setId(long budgetId) { this.budgetId = budgetId; }

	/**
	 * Gets the <code>Budget</code> ID.
	 *
	 * @return The <code>Budget</code> ID.
	 */
	public long getId() { return budgetId; }

	/**
	 * Adds a new <code>entry</code> to the <code>Budget</code>.
	 *
	 * @param entry The entry to add.
	 * @throws IllegalArgumentException if <code>entry</code> already
	 *                                  exists in this budget.
	 */
	public void addEntry(Entry entry) {
		if (entries.contains(entry)) {
			throw new IllegalArgumentException("Tried to add entry to " +
					"budget that already contained it.");
		}
		currentAmount += entry.getAmount();
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
		if (!entries.contains(entry)) {
			throw new IllegalArgumentException("Tried to remove entry from " +
					"budget that did not contain it.");
		}
		currentAmount -= entry.getAmount();
		entries.remove(entry);
	}

	/**
	 * Gets and returns the user-specified name of this <code>Budget</code>.
	 *
	 * @return The name of this <code>Budget</code>.
	 */
	public String getName() { return name; }

	/**
	 * Gets and returns the user-specified total budget amount of this
	 * <code>Budget</code>. This number is the maximum amount of cents
	 * the user can spend without going over-budget.
	 *
	 * @return The amount of money allowed in this budget, in cents.
	 */
	public int getBudgetAmount() { return amount; }

	/**
	 * Gets and returns the current memorized total the user has spent
	 * in this <code>Budget</code> for the current cycle. This number
	 * is in cents.
	 *
	 * @return The amount of money spent on this <code>Budget</code>
	 *         this cycle, in cents.
	 */
	public int getCurrentAmount() {
		// TODO: Need to check to see if the memorized amount is out-of date
		//       i.e. new cycle
		return currentAmount;
	}

	/**
	 * Determines if this <code>Budget</code> recurs or not. If it
	 * does recur, it will return <code>true</code>. If not, it will
	 * return <code>false</code>.
	 *
	 * @return Whether this <code>Budget</code> recurs or not.
	 */
	public boolean doesRecur() { return recur; }

	/**
	 * Returns the duration type of this budget
	 * @return the duration of this budget
	 */
	public Duration getDuration() { return duration; }

	/**
	 * Returns the otherDuration, which is used if this budget's
	 * Duration is set to Duration.OTHER.
	 * @return the duration of this Budget in days, or 0 if
	 * getDuration() doesn't return Duration.OTHER.
	 */
	public int getOtherDuration() { return otherDuration; }

	/**
	 * Calculates the current cycle count of this <code>Budget</code>.
	 *
	 * @return The current cycle count.
	 * @throws IllegalStateException if the <code>Duration</code> type is
	 *         not set appropriately.
	 */
	public int getCurrentCycle() {
		Calendar now = Calendar.getInstance();

		long timeDiffInMillis = now.getTimeInMillis() - startDate.getTimeInMillis();
		switch (duration) {
		case DAY:
			return (int) Math.ceil(timeDiffInMillis / (double) ONE_DAY);
		case WEEK:
			return (int) Math.ceil(timeDiffInMillis / (double) 7 * ONE_DAY);
		case FORTNIGHT:
			return (int) Math.ceil(timeDiffInMillis / (double) 14 * ONE_DAY);
		case MONTH:
			return 	((now.get(Calendar.YEAR) - startDate.get(Calendar.YEAR)) * 12) +
					(now.get(Calendar.MONTH) - startDate.get(Calendar.MONTH)) +
					(now.get(Calendar.DAY_OF_MONTH) < startDate.get(Calendar.DAY_OF_MONTH) ? 0 : 1);
		case YEAR:
			int startYear = startDate.get(Calendar.YEAR);
			int thisYear = now.get(Calendar.YEAR);

			if ((startYear % 4 == 0) && (startYear % 100 != 0) || (startYear % 400 == 0)) {
				// Is leap year. Now check to see if it is not currently leap year AND if the start
				// day was after leap day.
				if (startDate.get(Calendar.DAY_OF_YEAR) > 60 &&
						!((thisYear % 4 == 0) && (thisYear % 100 != 0) || (thisYear % 400 == 0)))
					return thisYear - startYear + (now.get(Calendar.DAY_OF_YEAR)
							< startDate.get(Calendar.DAY_OF_YEAR) + 1 ? 0 : 1);
			}

			return thisYear - startYear + (now.get(Calendar.DAY_OF_YEAR)
					< startDate.get(Calendar.DAY_OF_YEAR) ? 0 : 1);
		case OTHER:
			return (int) Math.ceil(timeDiffInMillis / (double) otherDuration * ONE_DAY);
		}

		throw new IllegalStateException("Budget must have a duration type specified.");
	}

	/**
	 * Determines if this <code>Budget</code> is still active or not.
	 *
	 * @return <code>true</code> if the <code>Budget</code> is active,
	 *         <code>false</code> otherwise.
	 */
	public boolean isActive() {
		return recur || getCurrentCycle() == 1;
	}

	/**
	 * Returns the start time, in milliseconds, of the given
	 * <code>cycle</code>. This time is on the start day at midnight.
	 *
	 * @param cycle The cycle to calculate the start time of.
	 * @return The start time, in milliseconds, of the <code>cycle</code>.
	 * @throws IllegalArgumentException If the cycle is inappropriate.
	 * @throws IllegalStateException if the <code>Duration</code> type is
	 *         not set appropriately.
	 */
	public long startTimeMillis(int cycle) {
		if (!recur && cycle > 1) {
			throw new IllegalArgumentException("Cannot get the cycle > 1 of non-recurring Budget.");
		}

		switch (duration) {
		case DAY:
			return startDate.getTimeInMillis() + (cycle - 1) * ONE_DAY;
		case WEEK:
			return startDate.getTimeInMillis() + (cycle - 1) * 7 * ONE_DAY;
		case FORTNIGHT:
			return startDate.getTimeInMillis() + (cycle - 1) * 14 * ONE_DAY;
		case MONTH:
			Calendar monthCycleStart = (Calendar) startDate.clone();

			monthCycleStart.add(Calendar.MONTH, cycle - 1);

			if (monthCycleStart.get(Calendar.DAY_OF_MONTH) < startDate.get(Calendar.DAY_OF_MONTH)) {
				// The start date was truncated by varying month lengths. We want the start date to
				// be on or after the matching day in a given month, not before.
				monthCycleStart.add(Calendar.DAY_OF_YEAR, 1);
			}

			return monthCycleStart.getTimeInMillis();
		case YEAR:
			Calendar yearCycleStart = (Calendar) startDate.clone();

			yearCycleStart.add(Calendar.YEAR, cycle - 1);

			if (yearCycleStart.get(Calendar.DAY_OF_MONTH) < startDate.get(Calendar.DAY_OF_MONTH)) {
				// The start date was truncated by leap day. We want the start date to
				// be on or after the matching day in a given month, not before.
				yearCycleStart.add(Calendar.DAY_OF_YEAR, 1);
			}

			return yearCycleStart.getTimeInMillis();
		case OTHER:
			return startDate.getTimeInMillis() + (cycle - 1) * otherDuration * ONE_DAY;
		}

		throw new IllegalStateException("Budget must have a duration type specified.");
	}

	/**
	 * Returns the start time, in milliseconds, of this budget.
	 * @return the start time, in milliseconds, of this budget.
	 */
	public long startTimeMillis() {
		return startDate.getTimeInMillis();
	}

	/**
	 * Returns the start time, in milliseconds, of the given
	 * <code>cycle</code>. This time is a millisecond before
	 * the start time of the next cycle.
	 *
	 * @param cycle The cycle to calculate the end time of.
	 * @return The end time, in milliseconds, of the <code>cycle</code>.
	 * @throws IllegalArgumentException If the cycle is inappropriate.
	 * @throws IllegalStateException if the <code>Duration</code> type is
	 *         not set appropriately.
	 */
	public long endTimeMillis(int cycle) {
		return startTimeMillis(cycle + 1) - 1;
	}

}
