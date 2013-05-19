package com.example.budgetmanager;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

	// List of Budgets to choose from
	private Spinner mBudgetView;
	
	// Number field for entering the Entry amount
	private EditText mAmountView;
	
	// Enables the user to pick the start date of the Budget
	private DatePicker mDateView;
	
	// Text field for entering notes for the Entry
	private EditText mNotesView;
	private Button addButtonView;

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
		addButtonView = (Button) findViewById(R.id.add_entry_button);
	}

	/** Called whenever the activity is brought back to the foreground */
	@Override
	protected void onResume() {
		super.onResume();

		// populate list items for the budget selector
		addItemsToBudgetSpinner();
	}

	// Populates the spinner with the current list of Budgets.
	private void addItemsToBudgetSpinner() {
		// get the actual Budget objects
		final List<Budget> budgetList = appData.getBudgetList();
		// list for the String names for each Budget object
		List<String> budgetNameList = new ArrayList<String>();
		
		// build the list of Budget names
		for (Budget b : budgetList) {
			Log.d(TAG, b.getName());
			budgetNameList.add(b.getName());
		}

		// last entry of the list of Budget is for adding a new Budget
		budgetNameList.add(getResources().getString(R.string.new_budget));
		// create an ArrayAdapter using the names of the Budgets
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_layout, budgetNameList);
		// specify the layout to use when the list of choices appears
		dataAdapter.setDropDownViewResource(R.layout.spinner_entry_layout);
		// apply the adapter to the spinner
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
		mAmountView.setError(null);

		// checks whether the amount is empty
		if (mAmountView.getText().toString().isEmpty()) {
			mAmountView.setError(getString(R.string.error_invalid_amount));
			mAmountView.requestFocus();
			return;
		}
		
		// checks whether the amount is non-zero
		if (Double.parseDouble(mAmountView.getText().toString()) == 0.0) {
			mAmountView.setError(getString(R.string.error_zero_amount));
			mAmountView.requestFocus();
			return;
		}

		// create the Entry object to add to the Budget
		final Entry newEntry = createEntry();

		if (newEntry == null) {
			// do nothing until add Budget activity is up
			return;
		}
		addButtonView.setClickable(false);
		
		ApiInterface.getInstance().create(newEntry, new ApiCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				UBudgetApp app = (UBudgetApp) getApplication();
				app.getEntryList().add(0, newEntry);
				
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

				// goto logs screen
				finish();
			}

			@Override
			public void onFailure(String errorMessage) {
				// if the request fails, do nothing (the toast is for testing purposes)
				Toast.makeText(AddEntryActivity.this, "FAILED", Toast.LENGTH_LONG).show();
				addButtonView.setClickable(true);
			}
		});
	}

	// Helper method to create the new <code>Entry</code> object to be added.
	private Entry createEntry() {
		// extract the amount information from its text field
		double doubleAmount = Double.parseDouble(mAmountView.getText().toString());

		// amount will be stored in cents
		int intAmount = (int) (doubleAmount * CENTS);
		Log.d("TAG", "createEntry: amount = " + intAmount);

		String notes = mNotesView.getText().toString();

		// retrieve selected budget
		final List<Budget> budgetList = appData.getBudgetList();
		Budget budget = budgetList.get(mBudgetView.getSelectedItemPosition());

		// Need to add 1 to the month because the DatePicker
		// has zero-based months.
		LocalDate date = new LocalDate(mDateView.getYear(),
				mDateView.getMonth() + 1, mDateView.getDayOfMonth());
		return new Entry(intAmount, budget, notes, date);
	}

	/**
	 * Resets the add Entry view.
	 *
	 * @param view The reference to the clear button.
	 */
	public void clearEntry(View view) {
		mAmountView.setError(null);

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
