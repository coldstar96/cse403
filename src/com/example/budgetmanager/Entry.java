package com.example.budgetmanager;

import java.util.Date;

public class Entry implements Comparable<Entry> {
	long entryId;

	private String name; // needed?
	private int amount; // in cents
	private int budget;
	private String note;
	private Date date;
	
	
	public Entry(double amount, int budget) {
		this(amount, budget, null, null);
	}
	
	public Entry(double amount, int budget, String notes) {
		this(amount, budget, notes, null);
	}
	
	public Entry(double amount, int budget, Date date) {
		this(amount, budget, null, date);
	}
	
	public Entry(double amount, int budget, String notes, Date date) {
		
	}

	public double getAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getBudget() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getNotes() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(Entry arg0) {
		// TODO Auto-generated method stub
		return other.date - date;
	}

}
