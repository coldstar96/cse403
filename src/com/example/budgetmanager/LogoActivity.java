package com.example.budgetmanager;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;

/**
 * Activity which shows the Husky logo and fetches all budgets and entries
 *
 * @author Chi Ho coldstar96
 *
 */
public class LogoActivity extends Activity {
	public static final String TAG = "LogoActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo);

		// check login status
		ApiInterface.getInstance().checkLoginStatus(new ApiCallback<Object>(){
			@Override
			public void onSuccess(Object result) {
				Log.d(TAG, "check login in on ApiInteface is success");

				// fetch budgets and entries
				ApiInterface.getInstance().fetchBudgetsAndEntries(
						new ApiCallback<List<Budget>>(){
					@Override
					public void onSuccess(List<Budget> result) {
						UBudgetApp app = (UBudgetApp)getApplication();

						// Add these budgets to the application state
						List<Budget> budgetList = app.getBudgetList();
						budgetList.clear();
						budgetList.addAll(result);

						// Add entries to the application state
						List<Entry> entryList = app.getEntryList();
						entryList.clear();

						for (Budget b : budgetList) {
							Log.d(TAG, b.getName() + " budget fetched");
							entryList.addAll(b.getEntries());
							for(Entry e: b.getEntries()){
								Log.d(TAG, e.getAmount() + " entry fetched");
							}
						}
						Log.d(TAG, "fetch data on ApiInteface is success");
						startActivity(new Intent(LogoActivity.this,
								EntryLogsActivity.class));
						finish();
					}

					@Override
					public void onFailure(String errorMessage) {
						Log.d(TAG, "fetch data on ApiInteface is failure");
						startActivity(new Intent(LogoActivity.this,
								EntryLogsActivity.class));
						finish();
					}
				});
			}

			@Override
			public void onFailure(String errorMessage) {
				Log.d(TAG, "check login in on ApiInteface is failure");
				startActivity(new Intent(LogoActivity.this,
						LoginActivity.class));
				finish();
			}
		});
	}
}