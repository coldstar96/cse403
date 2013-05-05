package com.example.budgetmanager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class LogoActivity extends Activity {
	boolean sleep;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo);
		
		sleep = true;
		
		Thread logoTimer = new Thread(){
			public void run(){
				try{
					sleep(3500);	//sleep for 5 seconds
				}catch(InterruptedException e){
				}finally{
					startActivity(new Intent(LogoActivity.this, LoginActivity.class));
					finish();
				}
			}
		};
		logoTimer.start();	// start the logoTimer
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.logo, menu);
		return true;
	}

}
