package com.example.budgetmanager;

import java.util.Set;

/**
 * This is the budget object. It keeps track of all the entries
 * in a specific budget and stores the information related to
 * budget amounts and time restrictions.
 * 
 * @author uBudget
 */
public class Budget {
	private long budgetId;
	
	private String name;
	private int amount; 		// Amount allocated for the budget, in cents
	private int currentAmount;	// Memorized amount spent so far, in cents
	private boolean recur;		// true for recurring budget
	private long startDate;
	private int duration;
	
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
}
