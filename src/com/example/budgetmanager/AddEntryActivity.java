package com.example.budgetmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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

	@Override
	/** Called when the activity is first created. */
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
					// TODO: switch to add budget activity when ready.
					Toast.makeText(parent.getContext(), "new budget!", Toast.LENGTH_LONG).show();
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
	 * @param view The current state of the add Entry view.
	 */
	public void addEntry(View view) {
		if (mAmountView.getText().toString().equals("")) {
			// amount is a required field
			Toast.makeText(this, "Please specify an amount.", 
					Toast.LENGTH_LONG).show();
		} else {			
			// create the Entry object to add to the Budget
			final Entry newEntry = createEntry();

			ApiInterface.getInstance().create(newEntry, new ApiCallback<Long>() {
				@Override
				public void onSuccess(Long result) {
					// TODO Auto-generated method stub
					Toast.makeText(AddEntryActivity.this, "Added $" 
					+ ((double) newEntry.getAmount() / CENTS) + " to the " 
							+ newEntry.getBudget().getName() + " budget "
							+ "with the date of: " + newEntry.getDate() 
							+ " with a note of: " + newEntry.getNotes()
			   				, Toast.LENGTH_LONG).show();
					
					// clear the fields if the add was successful
					AddEntryActivity.this.clearEntry();
					
					// add the entry into the Budget object
					newEntry.getBudget().addEntry(newEntry);
				}

				@Override
				public void onFailure(String errorMessage) {
					// TODO Auto-generated method stub
					Toast.makeText(AddEntryActivity.this, "FAILED", Toast.LENGTH_LONG).show();
				}
			});
		}
	}
	
	private Entry createEntry() {
		// extract the amount information
		double doubleAmount = Double.parseDouble(mAmountView.getText().toString());
		// amount will be stored in cents
		int intAmount = (int) (doubleAmount * CENTS);
		
		// retrieve selected budget
		final List<Budget> budgetList = appData.getBudgetList();
		Budget budget = budgetList.get(mBudgetView.getSelectedItemPosition());

		String notes = mNotesView.getText().toString();
		
		// format the string so that the server will parse the date correctly
		String date = mDateView.getYear() + "-" + (mDateView.getMonth() + 1) + "-" + mDateView.getDayOfMonth();
		Log.d("createEntry", ""+intAmount);
		return new Entry(intAmount, budget, notes, date);
	}

	/**
	 * Resets the add Entry view.
	 */
	public void clearEntry() {
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
