package com.example.budgetmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;

import java.util.List;

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
		ApiInterface.getInstance().checkLoginStatus(new ApiCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				Log.d(TAG, "check login in on ApiInteface is success");

				// fetch budgets and entries
				Budget.clearBudgets();
				ApiInterface.getInstance().fetchBudgetsAndEntries(
						new ApiCallback<List<Budget>>() {
							@Override
							public void onSuccess(List<Budget> result) {
								startActivity(new Intent(LogoActivity.this, MainActivity.class));
								finish();
							}

							@Override
							public void onFailure(String errorMessage) {
								Log.d(TAG, "fetch data on ApiInteface is failure");
								if (errorMessage != null) {
									Toast.makeText(LogoActivity.this,
											errorMessage, Toast.LENGTH_LONG).show();
								}
								startActivity(new Intent(LogoActivity.this,	MainActivity.class));
								finish();
							}
						});
			}

			@Override
			public void onFailure(String errorMessage) {
				Log.d(TAG, "check login in on ApiInteface is failure");
				if (errorMessage != null) {
					Toast.makeText(LogoActivity.this,
							errorMessage, Toast.LENGTH_LONG).show();
				}
				startActivity(new Intent(LogoActivity.this,
						LoginActivity.class));
				finish();
			}
		});
	}
}
