package com.example.budgetmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;

/**
 * Provides a way to access certain top level application state.
 * @author Chris brucec5
 *
 */
public class UBudgetApp extends Application {
	private static Context context;
	private List<Budget> budgetList;
	private List<Entry> entryList;
	private String email;

	@Override
	public void onCreate() {
		super.onCreate();
		UBudgetApp.context = getApplicationContext();
		budgetList = new ArrayList<Budget>();
		entryList = new ArrayList<Entry>();
	}

	public List<Budget> getBudgetList(){
		return budgetList;
	}

	public List<Entry> getEntryList(){
		return entryList;
	}

	public void setEmail(String e){
		email = e;
	}

	public String getEmail(){
		return email;
	}

	/**
	 * Provides a static way for classes to access the main application
	 * Context.
	 *
	 * @return the main application Context.
	 */
	public static Context getAppContext() {
		return UBudgetApp.context;
	}
}