package com.example.budgetmanager;

import android.app.Application;
import android.content.Context;

public class UBudgetApp extends Application {
	private static Context context;
	
	@Override
	public void onCreate() {
		super.onCreate();
		UBudgetApp.context = getApplicationContext();
	}
	
	public static Context getAppContext() {
		return UBudgetApp.context;
	}
}
