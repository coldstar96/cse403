package com.example.budgetmanager;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;
import com.example.budgetmanager.preference.SettingsFragment;

/**
 * Activity which shows the Husky logo and fetches all budgets and entries
 *
 * @author Chi Ho coldstar96
 *
 */
public class LogoActivity extends Activity {
	public static final String TAG = "LogoActivity";

	/* reference to the preferences of the app */
	private SharedPreferences spref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo);

		// save a reference to the preferences for the app
		spref = PreferenceManager.getDefaultSharedPreferences(this);

		// check login status
		ApiInterface.getInstance().checkLoginStatus(new ApiCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				Log.d(TAG, "check login in on ApiInteface is success");

				// fetch budgets and entries
				ApiInterface.getInstance().fetchBudgetsAndEntries(
						new ApiCallback<List<Budget>>() {
							@Override
							public void onSuccess(List<Budget> result) {
								// check the preference to see which activity to launch into
								String startingScreen = spref.getString(SettingsFragment
										.KEY_PREF_STARTING_SCREEN, "");

								if (startingScreen.equals(SettingsFragment
										.STARTING_SCREEN_LOG)) {
									startActivity(new Intent(LogoActivity.this, EntryLogsActivity.class));
								} else {
									startActivity(new Intent(LogoActivity.this, SummaryActivity.class));
								}
								finish();
							}

							@Override
							public void onFailure(String errorMessage) {
								Log.d(TAG, "fetch data on ApiInteface is failure");
								if (errorMessage != null) {
									Toast.makeText(LogoActivity.this,
											errorMessage, Toast.LENGTH_LONG).show();
								}
								startActivity(new Intent(LogoActivity.this,
										EntryLogsActivity.class));
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
