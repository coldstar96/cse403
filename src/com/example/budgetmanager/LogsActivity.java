package com.example.budgetmanager;

import java.util.List;
import java.util.Locale;

import com.example.budgetmanager.preference.PreferencesFragment;
import com.example.budgetmanager.preference.SettingsActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class LogsActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	private static final String TAG = "LogsAtivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logs);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		findViewById(R.id.add_entry_button).setOnClickListener(
				new View.OnClickListener() {
					@SuppressLint("ShowToast")
					@Override
					public void onClick(View view) {
						// if there is no created budget, notify user that they need
						// to create budget before they add an entry
						List<Budget> budgets = ((UBudgetApp) getApplication()).getBudgetList();
						if(budgets.isEmpty()){
							Toast.makeText(LogsActivity.this, R.string.dialog_add_budget_first, Toast.LENGTH_LONG).show();
						}else{
							Intent intent = new Intent(LogsActivity.this, AddEntryActivity.class);
							startActivity(intent);
						}
					}
				});
		

		findViewById(R.id.add_budget_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(LogsActivity.this, AddBudgetActivity.class);
						startActivity(intent);
					}
				});
		
	}
	
//	/** Called when the activity is first created to specify option menu. */
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//	    MenuInflater inflater = getMenuInflater();
//	    // Inflate the menu; this adds items to the action bar if it is present.
//	    inflater.inflate(R.menu.options_menu_item_settings, menu);
//	    inflater.inflate(R.menu.options_menu_item_signout, menu);
//	    return true;
//	}
//	
//	/** Called when an item in the options menu have been selected. */
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//	    // Handle item selection
//		if (item.getItemId() == R.id.settings) {
//			Toast.makeText(LogsActivity.this, "Successfully handled Settings selection"
//					, Toast.LENGTH_LONG).show();
//			return true;
//		} else if (item.getItemId() == R.id.signout) {
//			Toast.makeText(LogsActivity.this, "Successfully handled Sign out selection"
//					, Toast.LENGTH_LONG).show();
//			return true;
//		} else {
//			return super.onOptionsItemSelected(item);
//		}
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuItem buttonSettings = menu.add(R.string.title_settings); // This is a hardcoded string. When you get around to it, switch it to a localized String resource.
	    buttonSettings.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER); // This forces it to go in the overflow menu, which is preferred.
	    buttonSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

	        public boolean onMenuItemClick(MenuItem item) {
	        	Intent settingsIntent = new Intent(LogsActivity.this, SettingsActivity.class); // Change YourActivity to.. well, your activity. Change Preferences to the name of your Settings activity.
	            settingsIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, PreferencesFragment.class.getName());
	            settingsIntent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);	
	            LogsActivity.this.startActivity(settingsIntent);
	            
	            return false; // I honestly don't know why this should return false, but every example I've seen has it do so. So I'd leave it in.
	        }
	    });
	    
	    MenuItem buttonSignout = menu.add(R.string.title_signout); // This is a hardcoded string. When you get around to it, switch it to a localized String resource.
	    buttonSignout.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER); // This forces it to go in the overflow menu, which is preferred.
	    buttonSignout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

	        public boolean onMenuItemClick(MenuItem item) {
	        	Toast.makeText(LogsActivity.this, "Successfully handled Sign out selection"
						, Toast.LENGTH_LONG).show();
	            return false; // I honestly don't know why this should return false, but every example I've seen has it do so. So I'd leave it in.
	        }
	    });
	    return true;
	}


	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new FragmentEntryLogs();
			Bundle args = new Bundle();
			args.putInt(FragmentEntryLogs.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A log fragment that shows all of the logs of added entries.
	 */
	public static class FragmentEntryLogs extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public FragmentEntryLogs() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_entry_logs,
					container, false);
			
			return rootView;
		}
	}

	/**
	 * A summary fragment that shows summary of spendings by budget/time.
	 */
	public static class FragmentSummaryEntry extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public FragmentSummaryEntry() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_entry_logs,
					container, false);
			return rootView;
		}
	}
}
