package com.example.budgetmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;

/**
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

		ApiCallback<Object> callback = new ApiCallback<Object>(){

			@Override
			public void onSuccess(Object result) {
				Log.d(TAG, "check login in on ApiInteface is success");
				startActivity(new Intent(LogoActivity.this, EntryLogsActivity.class));
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
