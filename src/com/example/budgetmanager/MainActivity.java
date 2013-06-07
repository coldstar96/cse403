package com.example.budgetmanager;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.example.budgetmanager.preference.SettingsFragment;

import java.util.List;

/**
 * Main Activity holding the Entry Logs and Summary fragments.
 *
 * @author Ji jiwpark90
 */
public class MainActivity extends UBudgetActivity {

	/* reference to the ActionBar */
	private ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// set theme based on current preferences
		Utilities.setActivityTheme(this, getApplicationContext());

		super.onCreate(savedInstanceState);

		// use tabs
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// add the EntryLogs and Summary tabs
		String entryLogsLabel = getResources().getString(R.string.
				title_fragment_entry_logs);
		Tab tab = actionBar.newTab();
		tab.setText(entryLogsLabel);
		TabListener<EntryLogsTab> entryLogsTab = new
				TabListener<EntryLogsTab>(this, entryLogsLabel,
						EntryLogsTab.class);
		tab.setTabListener(entryLogsTab);
		actionBar.addTab(tab);

		String summaryLabel = getResources().getString(R.string.
				title_fragment_summary);
		tab = actionBar.newTab();
		tab.setText(summaryLabel);
		TabListener<SummaryTab> summaryTab = new
				TabListener<SummaryTab>(this, summaryLabel, SummaryTab.class);
		tab.setTabListener(summaryTab);
		actionBar.addTab(tab);

		// check for a self-invoked activity to decide which tab to display
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			// not self-invoked; display the tab specified in preferences
			String startingScreen = PreferenceManager.
					getDefaultSharedPreferences(this).
					getString(SettingsFragment.KEY_PREF_STARTING_SCREEN, "");
			if (startingScreen.equals(SettingsFragment.STARTING_SCREEN_LOG)) {
				actionBar.setSelectedNavigationItem(SettingsFragment.
						STARTING_SCREEN_LOG_INT);
			} else {
				actionBar.setSelectedNavigationItem(SettingsFragment.
						STARTING_SCREEN_SUMMARY_INT);
			}
		} else {
			// self-invoked; display the tab the user was on
			actionBar.setSelectedNavigationItem(extras.
					getInt("TabIndex"));
		}

		// trick to prevent infinite looping when onResume() is called
		getIntent().setAction("Already created");
	}


	@Override
	protected void onResume() {
		String action = getIntent().getAction();
		if (action == null || !action.equals("Already created")) {
			// don't restart if action is present
			Intent intent = new Intent(this, MainActivity.class);
			// pass the current selected tab information
			intent.putExtra("TabIndex"
					, actionBar.getSelectedNavigationIndex());
			startActivity(intent);
			finish();
		} else {
			// remove the unique action so the next time onResume
			// call will force restart
			getIntent().setAction(null);
		}

		super.onResume();
	}

	/**
	 * Takes the user to the Add Budget screen.
	 *
	 * @param view The reference to the Add Budget button.
	 */
	public void onAddBudgetClicked(View view) {
		Intent intent = new Intent(MainActivity.this,
				AddBudgetActivity.class);
		startActivity(intent);
	}

	/**
	 * Takes the user to the Add Entry screen.
	 *
	 * @param view The reference to the Add Entry button.
	 */
	public void onAddEntryClicked(View view) {
		// if there is no created budget, notify user that
		// they need to create budget before they add an entry
		List<Budget> budgets = Budget.getBudgets();
		if (budgets.isEmpty()) {
			Toast.makeText(MainActivity.this,
					R.string.dialog_add_budget_first,
					Toast.LENGTH_LONG).show();
		} else {
			Intent intent = new Intent(MainActivity.this,
					AddEntryActivity.class);
			startActivity(intent);
		}
	}

	// Listener for Tab selections
	private class TabListener<T extends Fragment> implements
	ActionBar.TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		/**
		 * Creates a new tab.
		 *
		 * @param activity The host Activity.
		 * @param tag Identifier for the fragment.
		 * @param clz The fragment's Class.
		 */
		public TabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// user chose an already selected tab; do nothing
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (mFragment == null) {
				// if the fragment has not been initialized, instantiate it and
				// add it to the activity
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				// if the fragment has already been initialized, simply attach
				// the fragment
				ft.attach(mFragment);
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				// detach the fragment so that another fragment can be attached
				ft.detach(mFragment);
			}
		}
	}
}