package com.example.budgetmanager;

import com.example.budgetmanager.preference.SettingsActivity;
import com.example.budgetmanager.preference.SettingsFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

// TODO skeleton
public class SummaryActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// inflate view
		setContentView(R.layout.activity_summary);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuItem buttonSettings = menu.add(R.string.title_settings);
	    // this forces it to go in the overflow menu, which is preferred.
	    buttonSettings.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
	    buttonSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
	    	/** Take the users to the Settings activity upon clicking the button. */
	        public boolean onMenuItemClick(MenuItem item) {
	        	Intent settingsIntent = new Intent(SummaryActivity.this, SettingsActivity.class);
	            settingsIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsFragment.class.getName());
	            settingsIntent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);	
	            SummaryActivity.this.startActivity(settingsIntent);
	            
	            return false;
	        }
	    });
	    
	    MenuItem buttonSignout = menu.add(R.string.title_signout);
	    buttonSignout.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
	    buttonSignout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
	    	/** Sign out the user upon clicking the button. */
	        public boolean onMenuItemClick(MenuItem item) {
	        	Toast.makeText(SummaryActivity.this, "Successfully handled Sign out selection"
						, Toast.LENGTH_LONG).show();
	            return false;
	        }
	    });
	    return true;
	}
}
