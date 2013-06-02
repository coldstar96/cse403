package com.example.budgetmanager;

import java.util.Comparator;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Activity which displays list of entries screen to the user, offering add entry
 * and add budget as well
 *
 * @author Chi Ho coldstar96
 */
public class EntryLogsTab extends Fragment {
	private final String TAG = "EntrylogsActivity";

	// UI reference
	private ListView listView;
	private Spinner sortSpinner;

	// The adapter for displaying/sorting the Entries
	private EntryLogAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		adapter.clear();

		for (Budget b : Budget.getBudgets()) {
			adapter.addEntriesFromBudget(b);
		}

		int position = sortSpinner.getSelectedItemPosition();
		sortBySortSpinnerIndex(position);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.activity_entry_logs, container, false);
		Log.d(TAG, "About to make the adapter");
		adapter = new EntryLogAdapter(getActivity(), R.layout.list_entry_layout,
				Budget.getBudgets());

		// The initial sort will be by date.
		adapter.sort(new EntryLogAdapter.EntryDateComparator());
		Log.d(TAG, "Made the adapter!");

		// set up Entry Logs screen
		listView = (ListView) layout.findViewById(R.id.entry_list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v,
					int pos, long id) {
				Toast.makeText(getActivity(),
						"click not implemented yet", Toast.LENGTH_LONG).show();
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Toast.makeText(getActivity(),
						"Long click not implemented yet",
						Toast.LENGTH_LONG).show();
				return false;
			}
		});

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