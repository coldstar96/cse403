package com.example.budgetmanager;

import java.util.Set;

public class Budget {
	private long budgetId;
	
	private String name;
	private int amount; 		// Amount allocated for the budget, in cents
	private int currentAmount;	// Memorized amount spent so far, in cents
	private boolean recur;		// true for recurring budget
	private long startDate;
	private int duration;
	
	private Set<Entry> entries;
	
	public void addEntry(int amount, String name, String note) {
		
	}
	
	public void removeEntry(Entry e) {
		
	}
}
