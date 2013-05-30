package com.example.budgetmanager;

import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.budgetmanager.preference.SettingsActivity;
import com.example.budgetmanager.preference.SettingsFragment;

/**
 * Activity which displays list of entries screen to the user, offering add entry
 * and add budget as well
 *
 * @author Chi Ho coldstar96
 */
public class EntryLogsActivity extends Activity {
	private final String TAG = "EntrylogsActivity";

	// UI reference
	private ListView listView;
	private Spinner sortSpinner;

	// The adapter for displaying/sorting the Entries
	private EntryLogAdapter adapter;

	@Override
	protected void onResume() {
		super.onResume();
		adapter.clear();

		for (Budget b : Budget.getBudgets()) {
			adapter.addEntriesFromBudget(b);
		}

		int position = sortSpinner.getSelectedItemPosition();
		sortBySortSpinnerIndex(position);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// set default values for settings (if never done before)
		PreferenceManager.setDefaultValues(this, R.layout.fragment_settings, false);

		// set theme based on current preferences	
		Utilities.setThemeToActivity(this, getApplicationContext());

		super.onCreate(savedInstanceState);

		// inflate view
		setContentView(R.layout.activity_entry_logs);

		Log.d(TAG, "About to make the adapter");
		adapter = new EntryLogAdapter(this, R.layout.list_entry_layout,
				Budget.getBudgets());

		// The initial sort will be by date.
		adapter.sort(new EntryLogAdapter.EntryDateComparator());
		Log.d(TAG, "Made the adapter!");

		// set up Entry Logs screen
		listView = (ListView) findViewById(R.id.entry_list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v,
					int pos, long id) {
				Toast.makeText(EntryLogsActivity.this,
						"click not implemented yet", Toast.LENGTH_LONG).show();
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Toast.makeText(EntryLogsActivity.this,
						"Long click not implemented yet",
						Toast.LENGTH_LONG).show();
				return false;
			}
		});

		sortSpinner = (Spinner) findViewById(R.id.spinner_logs_sort);

		sortSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				sortBySortSpinnerIndex(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// set up the button that lead to the settings activity
		MenuItem buttonSettings = menu.add(R.string.title_settings);

		// this forces it to go in the overflow menu, which is preferred.
		buttonSettings.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		buttonSettings.setOnMenuItemClickListener(new MenuItem.
				OnMenuItemClickListener() {
			/**
			 * Take the users to the Settings activity upon clicking the button.
			 */
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent settingsIntent = new Intent(EntryLogsActivity.this,
						SettingsActivity.class);

				// these extras allow SettingsActivity to skip the 'headers'
				// layer, which is unnecessary since we have very few settings
				settingsIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
						SettingsFragment.class.getName());
				settingsIntent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);

				EntryLogsActivity.this.startActivity(settingsIntent);

				return false;
			}
		});

		// set up the button that lead to the signout activity
		MenuItem buttonSignout = menu.add(R.string.title_signout);

		// this forces it to go in the overflow menu, which is preferred.
		buttonSignout.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		buttonSignout.setOnMenuItemClickListener(new MenuItem.
				OnMenuItemClickListener() {
			/**
			 * Sign out the user upon clicking the button.
			 */
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO implement a signout functionality
				Toast.makeText(EntryLogsActivity.this,
						"Successfully handled Sign out selection",
						Toast.LENGTH_LONG).show();
				return false;
			}
		});
		return true;
	}

	public void onAddBudgetClicked(View view) {
		Intent intent = new Intent(EntryLogsActivity.this,
				AddBudgetActivity.class);
		startActivity(intent);
	}

	public void onAddEntryClicked(View view) {
		// if there is no created budget, notify user that
		// they need to create budget before they add an entry
		List<Budget> budgets = Budget.getBudgets();
		if (budgets.isEmpty()) {
			Toast.makeText(EntryLogsActivity.this,
					R.string.dialog_add_budget_first,
					Toast.LENGTH_LONG).show();
		} else {
			Intent intent = new Intent(EntryLogsActivity.this,
					AddEntryActivity.class);
			startActivity(intent);
		}
	}

	private void sortBySortSpinnerIndex(int position) {
		Comparator<Entry> comp;

		switch(position) {
		case 0:
			comp = new EntryLogAdapter.EntryDateComparator();
			break;
		case 1:
			comp = new EntryLogAdapter.EntryAmountComparator();
			break;
		case 2:
			comp = new EntryLogAdapter.EntryBudgetComparator();
			break;
		case 3:
			comp = new EntryLogAdapter.EntryCreationTimeComparator();
			break;
		case 4:
			comp = new EntryLogAdapter.EntryUpdateTimeComparator();
			break;
		default:
			comp = new EntryLogAdapter.EntryDateComparator();
		}

		adapter.sort(comp);
		adapter.notifyDataSetChanged();
	}
}
