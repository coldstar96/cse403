package com.example.budgetmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;

/**
 * Activity which allows users to add entries.
 *
 * @author Ji jiwpark90
 */
public class AddEntryActivity extends Activity {
	public final static int CENTS = 100;

	// tag for logging
	private final static String TAG = "AddEntryActivity";

	// shared data across the app
	private UBudgetApp appData;

	// views to extract information from
	private Spinner mBudgetView;
	private EditText mAmountView;
	private DatePicker mDateView;
	private EditText mNotesView;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// inflate view
		setContentView(R.layout.activity_add_entry);

		// retrieve the application data
		appData = (UBudgetApp)getApplication();

		mBudgetView = (Spinner) findViewById(R.id.spinner_budget);
		mAmountView = (EditText) findViewById(R.id.edit_amount);
		mDateView = (DatePicker) findViewById(R.id.date_picker);
		mNotesView = (EditText) findViewById(R.id.edit_notes);
	}


	/** Called whenever the activity is brought back to the foregroud */
	@Override
	protected void onResume() {
		super.onResume();

		// populate list items for the budget selector
		addItemsToBudgetSpinner();
	}

	// Populate the spinner with the current list of Budgets.
	private void addItemsToBudgetSpinner() {
		final List<Budget> budgetList = appData.getBudgetList();
		List<String> budgetNameList = new ArrayList<String>();

		for (Budget b : budgetList) {
			Log.d(TAG, b.getName());
			budgetNameList.add(b.getName());
		}

		budgetNameList.add(getResources().getString(R.string.new_budget));

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_layout, budgetNameList);
		dataAdapter.setDropDownViewResource(R.layout.spinner_entry_layout);
		mBudgetView.setAdapter(dataAdapter);

		// set the spinner to display selection upon selecting an item
		mBudgetView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos,
					long id) {
				if (pos == budgetList.size()) {
					startActivity(new Intent(AddEntryActivity.this, AddBudgetActivity.class));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// do nothing
			}

		});
	}

	/**
	 * Adds a new <code>Entry</code> to the specified <code>Budget</code>.
	 *
	 * @param view The reference to the add button.
	 */
	public void addEntry(View view) {
		
		if (mAmountView.getText().toString().isEmpty()) {
			mAmountView.setError(getString(R.string.error_invalid_amount));
			mAmountView.requestFocus();
			return;
		}

		// create the Entry object to add to the Budget
		final Entry newEntry = createEntry();

		if (newEntry == null) {
			// do nothing until add Budget activity is up
			return;
		}

		ApiInterface.getInstance().create(newEntry, new ApiCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				// for testing purposes
				Toast.makeText(AddEntryActivity.this, "Added $"
						+ ((double) newEntry.getAmount() / CENTS) + " to the "
						+ newEntry.getBudget().getName() + " budget "
						+ "with the date of: " + newEntry.getDate()
						+ " with a note of: " + newEntry.getNotes()
						, Toast.LENGTH_LONG).show();

				// clear the fields if the add was successful.
				// passes a null since the method doesn't need
				// a reference to a view object to work.
				AddEntryActivity.this.clearEntry(null);

				// add the entry into the Budget object
				newEntry.getBudget().addEntry(newEntry);

				Intent intent = new Intent(AddEntryActivity.this, LogsActivity.class);
				finish();
				startActivity(intent);
			}

			@Override
			public void onFailure(String errorMessage) {
				// if the request fails, do nothing (the toast is for testing purposes)
				Toast.makeText(AddEntryActivity.this, "FAILED", Toast.LENGTH_LONG).show();
			}
		});
	}

	// Helper method to create the new <code>Entry</code> object to be added.
	private Entry createEntry() {
		// extract the amount information
		double doubleAmount = Double.parseDouble(mAmountView.getText().toString());

		// amount will be stored in cents
		int intAmount = (int) (doubleAmount * CENTS);

		// retrieve selected budget
		final List<Budget> budgetList = appData.getBudgetList();
		// temporary place holder until add Budget activity is up.
		if (mBudgetView.getSelectedItemPosition() == budgetList.size()) {
			Toast.makeText(this, "Add budget functionality doesn't exist yet.", Toast.LENGTH_LONG).show();
			return null;
		}
		Budget budget = budgetList.get(mBudgetView.getSelectedItemPosition());

		String notes = mNotesView.getText().toString();

		// format the string so that the server will parse the date correctly
		String date = mDateView.getYear() + "-" + (mDateView.getMonth() + 1) + "-" + mDateView.getDayOfMonth();
		Log.d("createEntry", ""+intAmount);
		return new Entry(intAmount, budget, notes, date);
	}

	/**
	 * Resets the add Entry view.
	 *
	 * @param view The reference to the clear button.
	 */
	public void clearEntry(View view) {
		// set the spinner to the first item on the list
		mBudgetView.setSelection(0);

		// get current time
		Time now = new Time();
		now.setToNow();
		// update the DatePicker
		mDateView.updateDate(now.year, now.month, now.monthDay);

		// clear the EditText fields
		mAmountView.setText("");
		mNotesView.setText("");
	}
}
