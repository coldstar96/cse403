package com.example.budgetmanager;

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

		ApiCallback<Object> callback = new ApiCallback<Object>(){

			@Override
			public void onSuccess(Object result) {
				Log.d(TAG, "check login in on ApiInteface is success");

				// check the preference to see which activity to launch into
				String startingScreen = spref.getString(SettingsFragment
							.KEY_PREF_STARTING_SCREEN, "");
				
				if (startingScreen.equals(SettingsFragment
						.STARTING_SCREEN_LOG)) {
					startActivity(new Intent(LogoActivity.this, EntryLogsActivity.class));
				} else if (startingScreen.equals(SettingsFragment
						.STARTING_SCREEN_SUMMARY)) {
					startActivity(new Intent(LogoActivity.this, SummaryActivity.class));
				} else if (startingScreen.equals(SettingsFragment
						.STARTING_SCREEN_ADD_ENTRY)) {
					startActivity(new Intent(LogoActivity.this, AddEntryActivity.class));
				} else {
					startActivity(new Intent(LogoActivity.this, AddBudgetActivity.class));
				}
				
				finish();
			}

			@Override
			public void onFailure(String errorMessage) {
				Log.d(TAG, "check login in on ApiInteface is failure");
				startActivity(new Intent(LogoActivity.this, LoginActivity.class));
				finish();
			}
		};

		ApiInterface.getInstance().checkLoginStatus(callback);

	}
}
