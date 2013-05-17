package com.example.budgetmanager;

import org.joda.time.LocalDate;

/**
 *
 * @author Ji jiwpark90
 *
 */
public class Entry implements Comparable<Entry> {
	/**
	 * The ID given to an Entry upon creation locally.
	 */
	public static final long NEW_ID = -1;

	// The ID of this entry on the API server
	private long entryId;

	// the Budget object this entry belongs to
	private Budget budget;

	// in cents
	private int amount;

	// The date that this entry's expenditure was on
	private LocalDate date;

	// additional notes
	private String notes;

	/**
	 * Constructs a new Entry containing id, amount, Budget, notes,
	 * and Date information.
	 *
	 * @param id The ID for this Entry in the API
	 * @param amount The amount associated with the Entry in cents.
	 * @param budget The Budget object that contains the Entry.
	 * @param notes More detailed notes associated with the Entry.
	 * @param date The expenditure date associated with the Entry.
	 */
	public Entry(long id, int amount, Budget budget, String notes,
			LocalDate date) {
		this.entryId = id;
		this.amount = amount;
		this.budget = budget;
		this.notes = notes;
		this.date = date;
	}

	/**
	 * Constructs a new Entry containing amount, Budget, notes,
	 * and Date information.
	 *
	 * @param amount The amount associated with the Entry in cents.
	 * @param budget The Budget object that contains the Entry.
	 * @param notes More detailed notes associated with the Entry.
	 * @param date The date associated with the Entry.
	 */
	public Entry(int amount, Budget budget, String notes, LocalDate date) {
		this(NEW_ID, amount, budget, notes, date);
	}

	/**
	 * Retrieve the amount (in cents) of the Entry.
	 *
	 * @return amount (in cents) of the Entry.
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Retrieve the Budget object that holds the Entry.
	 *
	 * @return object that holds the Entry.
	 */
	public Budget getBudget() {
		return budget;
	}

	/**
	 * Retrieve the name associated with the Entry.
	 *
	 * @return String name associated with the Entry.
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Retrieve the entryId of this Entry
	 *
	 * @return Entry.NEW_ID if this Entry was created locally, or a nonnegative
	 * id from the API if this entry was fetched from the API.
	 */
	public long getEntryId() {
		return entryId;
	}

	/**
	 * Sets the entryId of this Entry
	 *
	 * @param id The ID of this entry as represented on the server
	 */
	public void setEntryId(long id) {
		this.entryId = id;
	}

  /*
   * Retrieve the date associated with the Entry.
   *
   * @return the date on which this Entry's expenditure was made on
	 */
	public LocalDate getDate() {
		return date;
	}

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
	public int compareTo(Entry other) {
		return this.amount - other.amount;
	}
}
