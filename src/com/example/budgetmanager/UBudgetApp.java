package com.example.budgetmanager;

import android.app.Application;
import android.content.Context;

/**
 * Provides a way to access certain top level application state.
 * @author Chris brucec5
 *
 */
public class UBudgetApp extends Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		UBudgetApp.context = getApplicationContext();
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