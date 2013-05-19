package com.example.budgetmanager;

import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays list of entries screen to the user, offering add entry
 * and add budget as well
 *
 * @author Chi Ho coldstar96
 */

public class EntryLogsActivity extends Activity {
	public final String TAG = "EntrylogsActivity";

	// UI references
	private ListView listView;
	private TextView userEmailView;

	private EntryLogAdapter adapter;

	UBudgetApp app;
	Spinner sortSpinner;

	@Override
	protected void onResume() {
		super.onResume();
		adapter.clear();

		for (Budget b : app.getBudgetList()) {
			adapter.addEntriesFromBudget(b);
		}

		int position = sortSpinner.getSelectedItemPosition();
		sortBySortSpinnerIndex(position);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry_logs);

		app = (UBudgetApp)getApplication();
		Log.d(TAG, "Just got the app, about to make the adapter");
		adapter = new EntryLogAdapter(this, R.layout.list_entry_layout,
				app.getBudgetList());

		// The initial sort will be by date.
		adapter.sort(new EntryLogAdapter.EntryDateComparator());
		Log.d(TAG, "Made the adapter!");

		// set up Entry Logs screen
		listView = (ListView) findViewById(R.id.entry_list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener(){
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

		sortSpinner = (Spinner)findViewById(R.id.spinner_logs_sort);

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

		userEmailView = (TextView) findViewById(R.id.text_user_email);
		userEmailView.setText(app.getEmail());
	}

	public void onAddBudgetClicked(View view) {
		Intent intent = new Intent(EntryLogsActivity.this,
				AddBudgetActivity.class);
		startActivity(intent);
	}

	public void onAddEntryClicked(View view) {
		// if there is no created budget, notify user that
		// they need to create budget before they add an entry
		List<Budget> budgets = ((UBudgetApp) getApplication()).
				getBudgetList();
		if(budgets.isEmpty()){
			Toast.makeText(EntryLogsActivity.this,
					R.string.dialog_add_budget_first,
					Toast.LENGTH_LONG).show();
		}else{
			Intent intent = new Intent(EntryLogsActivity.this,
					AddEntryActivity.class);
			startActivity(intent);
		}
	}

	public void sortBySortSpinnerIndex(int position) {
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
