package com.example.budgetmanager;

import java.util.Date;

public class Entry implements Comparable<Entry> {
	private long entryId; // TODO: what to do with this?

	private String label;	// optional label (needed?)
	private int amount;		// in cents
	private Budget budget;	// the Budget object this entry belongs to
	// private String note;	// additional notes
	private Date date;
	
	/**
	 * Returns a new Entry containing amount and Budget information.
	 * 
	 * @param amount The amount associated with the Entry in cents.
	 * @param budget The Budget object that contains the Entry.
	 */
	public Entry(int amount, Budget budget) {
		this(amount, budget, null, null);
	}
	
	/**
	 * Returns a new Entry containing amount, Budget, and label information.
	 * 
	 * @param amount The amount associated with the Entry in cents.
	 * @param budget The Budget object that contains the Entry.
	 * @param label String label associated with the Entry.
	 */
	public Entry(int amount, Budget budget, String label) {
		this(amount, budget, label, null);
	}
	
	/**
	 * Returns a new Entry containing amount, Budget, and Date information.
	 * 
	 * @param amount The amount associated with the Entry in cents.
	 * @param budget The Budget object that contains the Entry.
	 * @param date The Date associated with the Entry.
	 */
	public Entry(int amount, Budget budget, Date date) {
		this(amount, budget, null, date);
	}
	
	/**
	 * Returns a new Entry containing amount, Budget, label, and Date information.
	 * 
	 * @param amount The amount associated with the Entry in cents.
	 * @param budget The Budget object that contains the Entry.
	 * @param label String label associated with the Entry.
	 * @param date The Date associated with the Entry.
	 */
	public Entry(int amount, Budget budget, String label, Date date) {
		this.amount = amount;
		this.budget = budget;
		this.label = label;
		this.date = date;
	}

	/**
	 * Retrieve the amount (in cents) of the Entry.
	 * 
	 * @return amount (in cents) of the Entry.
	 */
	public int getAmount() { return amount; }

	/**
	 * Retrieve the Budget object that holds the Entry.
	 * 
	 * @return Budget object that holds the Entry.
	 */
	public Budget getBudget() { return budget; }

	/**
	 * Retrieve the String label associated with the Entry.
	 * 
	 * @return String label associated with the Entry.
	 */
	public String getLabel() { return label; }

	/**
	 * Retrieve the Date object associated with the Entry.
	 * 
	 * @return Date object associated with the Entry.
	 */
	public Date getDate() { return date; }

	/**
	 * Compares the other Entry to this Entry to see which precedes which.
	 * An Entry with the lower amount value comes first.
	 * 
	 * @param other The other Entry
	 * @return <code>i < 0</code> if this Entry comes before the other Entry,
	 * 		   <code>i == 0</code> if identical Entries,
	 * 		   <code>i > 0</code> if this Entry comes after the other Entry.
	 */
	@Override
	public int compareTo(Entry other) { return this.amount - other.amount; }
}
