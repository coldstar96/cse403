package com.example.budgetmanager;

import java.util.Comparator;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;

/**
 * Fragment which displays list of entries screen to the user, offering add
 * entry and add budget as well
 *
 * @author Chi Ho coldstar96
 */
public class EntryLogsTab extends Fragment {
	// UI reference
	private ListView listView;
	private Spinner sortSpinner;

	// The adapter for displaying/sorting the Entries
	private EntryLogAdapter adapter;

	// The currently selected entry
	private Entry selectedEntry = null;

	// tag for logging
	private final static String TAG = "AddEntryActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout layout =
				(RelativeLayout) inflater.inflate(R.layout.activity_entry_logs,
				container, false);

		// set adapter
		adapter = new EntryLogAdapter(getActivity(),
				R.layout.list_entry_layout, Budget.getBudgets());

		// The initial sort will be by date.
		adapter.sort(new EntryLogAdapter.EntryDateComparator());

		// set up Entry Logs screen
		listView = (ListView) layout.findViewById(R.id.entry_list);
		listView.setAdapter(adapter);

		// set up a context menu for the list items
		registerForContextMenu(listView);

		sortSpinner = (Spinner) layout.findViewById(R.id.spinner_logs_sort);

		sortSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				sortBySortSpinnerIndex(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// do nothing if nothing is selected
			}
		});
		return layout;
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
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// using an if/else instead of a switch to avoid the bug of calling
		// both edit and delete at once
		if (item.getItemId() == R.id.menu_edit) {
			Log.d(TAG, "Edit called.");

			// tell AddEntryActivity to start an edit entry session
			Intent intent = new Intent(getActivity(), AddEntryActivity.class);
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

					// remove the Entry from the Budget it is included in
					selectedEntry.getBudget().removeEntry(selectedEntry);
					selectedEntry = null;

					// refresh the view upon change
					refreshList();
				}

				@Override
				public void onFailure(String errorMessage) {
					Log.d(TAG, "Delete entry onFailure entered.");
					// if the request fails, do nothing (the toast is for testing purposes)
					Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
				}
			});
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