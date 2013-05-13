package com.example.budgetmanager;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;

/**
 *
 * @author Chi Ho coldstar96
 *
 */
public class LogoActivity extends Activity {
	public static final String TAG = "LogoActivity";
	long timer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo);

		timer = System.currentTimeMillis();

		ApiCallback<Object> callback = new ApiCallback<Object>(){

			@Override
			public void onSuccess(Object result) {
				Log.d(TAG, "check login in on ApiInteface is success");
				procrastinate(true);
				startActivity(new Intent(LogoActivity.this, LogsActivity.class));
				finish();
			}

			@Override
			public void onFailure(String errorMessage) {
				Log.d(TAG, "check login in on ApiInteface is failure");
				procrastinate(false);
				startActivity(new Intent(LogoActivity.this, LoginActivity.class));
				finish();
			}			
		};

		ApiInterface.getInstance().checkLoginStatus(callback);

	}

	private void procrastinate(boolean oldUser){
		if(oldUser){
			ApiInterface.getInstance().fetchBudgets(new ApiCallback<List<Budget>>() {
				@Override
				public void onSuccess(List<Budget> result) {

					Log.d(TAG, "Success on login callback");

					UBudgetApp app = (UBudgetApp)getApplication();

					// Add these budgets to the application state
					List<Budget> budgetList = app.getBudgetList();
					budgetList.clear();
					budgetList.addAll(result);

					for (Budget b : budgetList) {
						Log.d(TAG, b.getName());
					}
				}

				@Override
				public void onFailure(String errorMessage) {
					Log.d(TAG, "fail on log in callback");
					Toast.makeText(getBaseContext(), "Couldn't get a list of budgets", Toast.LENGTH_LONG).show();
				}

			});
		}
		timer = System.currentTimeMillis() - timer;
		if(timer < 3500){
			Thread logoTimer = new Thread(){
				@Override
				public void run(){
					try{
						sleep(3500 - timer);	//sleep for 3.5 seconds
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
			};
			logoTimer.start();
			try {
				logoTimer.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.logo, menu);
		return true;
	}

}
