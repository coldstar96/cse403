package com.example.budgetmanager;

import java.util.Calendar;
import java.util.Set;

/**
 * This is the budget object. It keeps track of all the entries
 * in a specific budget and stores the information related to
 * budget amounts and time restrictions.
 * 
 * @author uBudget
 */
public class Budget {
	public static void main(String[] args) throws InterruptedException {
		Budget budget = new Budget();
		budget.startDate = Calendar.getInstance();
		budget.startDate.set(Calendar.MONTH, budget.startDate.get(Calendar.MONTH) - 1);
		budget.durationType = Duration.MONTHLY;
		Thread.sleep(1000);
		System.out.println(budget.getCurrentCycle());
	}
	
	
	public static final long NEW_ID = -1;
	
	private long budgetId;
	
	public static enum Duration {
		DAILY, WEEKLY, FORTNIGHTLY, MONTHLY, YEARLY, OTHER
	}
	
	private String name;
	private int amount; 		// Amount allocated for the budget, in cents
	private int currentAmount;	// Memorized amount spent so far, in cents
	private boolean recur;		// true for recurring budget

	private Calendar startDate;
	private Duration durationType;	// Duration type.
	private int otherDuration;		// Duration in days, for duration type OTHER.
	
	private Set<Entry> entries;
	
	/**
	 * Adds a new entry with the <code>amount</code>, <code>name</code>,
	 * <code>note</code> and a specified <code>date</code> to this budget.
	 * 
	 * @param amount The value of the entry.
	 * @param name The name of the entry.
	 * @param note The optional note for the entry.
	 * @param date The date that the entry occurred. 
	 */
	public void addEntry(int amount, String name, String note, long date) {
		// TODO: Implement this.
	}
	
	/**
	 * Removes a specified <code>entry</code> from this budget.
	 * 
	 * @param entry The entry to remove.
	 * @throws IllegalArgumentException if <code>entry</code> is not an
	 * 									entry of this budget.
	 */
	public void removeEntry(Entry entry) {
		// TODO: Implement this.
	}
	
	public String getName() { return name; }
	
	public int getBudgetAmount() { return amount; }
	
	public int getCurrentAmount() { return currentAmount; }
	
	public boolean doesRecur() { return recur; }
	
	public int getCurrentCycle() {
		Calendar now = Calendar.getInstance();
		
		switch (durationType) {
		case DAILY:
			return (int) Math.ceil((now.getTimeInMillis() - startDate.getTimeInMillis()) / (24*60*60*1000.0));
		case WEEKLY:
			return (int) Math.ceil((now.getTimeInMillis() - startDate.getTimeInMillis()) / 7*24*60*60*1000.0);
		case FORTNIGHTLY:
			return (int) Math.ceil((now.getTimeInMillis() - startDate.getTimeInMillis()) / 14*24*60*60*1000.0);
		case MONTHLY:
			return 	((now.get(Calendar.YEAR) - startDate.get(Calendar.YEAR)) * 12) + 
					(now.get(Calendar.MONTH) - startDate.get(Calendar.MONTH)) +
					(now.get(Calendar.DAY_OF_MONTH) < startDate.get(Calendar.DAY_OF_MONTH) ? 0 : 1);
		case YEARLY:
			int startYear = startDate.get(Calendar.YEAR);
			int thisYear = now.get(Calendar.YEAR);
			
			if ((startYear % 4 == 0) && (startYear % 100 != 0) || (startYear % 400 == 0)) {
				// Is leap year. Now check to see if it is not currently leap year AND if the start
				// day was after leap day.
				if (startDate.get(Calendar.DAY_OF_YEAR) > 60 &&
						!((thisYear % 4 == 0) && (thisYear % 100 != 0) || (thisYear % 400 == 0)))
					return thisYear - startYear + (now.get(Calendar.DAY_OF_YEAR) < startDate.get(Calendar.DAY_OF_YEAR) + 1 ? 0 : 1);
			}
			
			return thisYear - startYear + (now.get(Calendar.DAY_OF_YEAR) < startDate.get(Calendar.DAY_OF_YEAR) ? 0 : 1);
		case OTHER:
			// TODO: Implement.
			return 0;
		}
		
		throw new IllegalArgumentException("Budget must have a duration type specified.");
	}
	
}
