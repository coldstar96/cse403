package com.example.budgetmanager;

import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;
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

	// The currently selected entry
	private Entry selectedEntry = null;

	@Override
	protected void onResume() {
		super.onResume();
		refreshList();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// set theme based on current preferences
		Utilities.setActivityTheme(this, getApplicationContext());

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

		// set up a context menu for the list items
		registerForContextMenu(listView);

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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		// Get the info on which item was selected
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

		// Retrieve the item that was clicked on
		selectedEntry = adapter.getItem(info.position);

		Log.d(TAG, "Amount: " + selectedEntry.getAmount() + ", " + selectedEntry.
				getBudget().getName());

		// inflate the context menu
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// using an if/else instead of a switch to avoid the bug of calling
		// both edit and delete at once
		if (item.getItemId() == R.id.menu_edit) {
			// tell AddEntryActivity to start an edit entry session
			Intent intent = new Intent(EntryLogsActivity.this, AddEntryActivity.class);
			intent.putExtra("Add", false);
			intent.putExtra("EntryId", selectedEntry.getEntryId());
			intent.putExtra("BudgetId", selectedEntry.getBudget().getId());

			startActivity(intent);
		}	else if (item.getItemId() == R.id.menu_delete) {
			Log.d(TAG, "Delete called.");

			ApiInterface.getInstance().remove(selectedEntry, new ApiCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
					Log.d(TAG, "Delete entry onSuccess entered.");
					// for testing purposes
					Toast.makeText(EntryLogsActivity.this,
							R.string.success_delete_entry,
							Toast.LENGTH_LONG).show();

					// remove the Entry from the Budget it is included in
					selectedEntry.getBudget().removeEntry(selectedEntry);

					// refresh the view upon change
					refreshList();
				}

				@Override
				public void onFailure(String errorMessage) {
					Log.d(TAG, "Delete entry onFailure entered.");
					// if the request fails, do nothing (the toast is for testing purposes)
					Toast.makeText(EntryLogsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
				}
			});
		}

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// inflate the menu
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.items, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()) {
		case R.id.menu_settings:
			// take the user to the Settings screen
			Intent settingsIntent = new Intent(EntryLogsActivity.this,
					SettingsActivity.class);

			// these extras allow SettingsActivity to skip the 'headers'
			// layer, which is unnecessary since we have very few settings
			settingsIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
					SettingsFragment.class.getName());
			settingsIntent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);

			startActivity(settingsIntent);

			return false;

		case R.id.menu_signout:
			// sign the user out
			ApiInterface.getInstance().logOut();
			Intent logOut = new Intent(EntryLogsActivity.this, LoginActivity.class);
			// Clear the back stack so when you press the back button you will exit the app
			logOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			// Goes to the login page
			startActivity(logOut);
			return false;
		}
		return true;
	}

	/* Helper method to refresh the list of ListView of Entries. */
	private void refreshList() {
		adapter.clear();

		for (Budget b : Budget.getBudgets()) {
			adapter.addEntriesFromBudget(b);
		}

		int position = sortSpinner.getSelectedItemPosition();
		sortBySortSpinnerIndex(position);

		adapter.notifyDataSetChanged();
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

			// tell AddEntryActivity that it should be an add entry session
			intent.putExtra("Add", true);
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
