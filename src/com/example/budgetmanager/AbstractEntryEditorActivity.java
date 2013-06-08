package com.example.budgetmanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
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

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a template Activity for the {@link AddEntryActivity} and
 * {@link EditEntryActivity} activities.
 *
 * @author Graham grahamb5
 */
public abstract class AbstractEntryEditorActivity extends UBudgetActivity {
	public final static int CENTS = 100;

	// tag for logging
	private final static String TAG = "AddEntryActivity";

	// List of Budgets to choose from
	protected Spinner mBudgetView;

	// Number field for entering the Entry amount
	protected EditText mAmountView;

	// Enables the user to pick the start date of the Budget
	protected DatePicker mDateView;

	// Text field for entering notes for the Entry
	protected EditText mNotesView;
	protected Button mAddButtonView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// set theme based on current preferences
		Utilities.setActivityTheme(this, getApplicationContext());

		super.onCreate(savedInstanceState);

		// inflate view
		setContentView(R.layout.activity_add_entry);

		mAmountView = (EditText) findViewById(R.id.entry_amount);
		// set currency filter
		mAmountView.setFilters(new InputFilter[] { new CurrencyInputFilter() });
		mBudgetView = (Spinner) findViewById(R.id.spinner_budget);
		mDateView = (DatePicker) findViewById(R.id.entry_date_picker);
		mNotesView = (EditText) findViewById(R.id.entry_notes);
		mAddButtonView = (Button) findViewById(R.id.add_entry_button);

		Log.d(TAG, "After views");

		// populate list items for the budget selector
		addItemsToBudgetSpinner();

		Log.d(TAG, "After budget spinner");

		// trick to prevent infinite looping when onResume() is called
		getIntent().setAction("Already created");
	}

	@Override
	protected void onResume() {
		super.onResume();

		String action = getIntent().getAction();
		if (action == null || !action.equals("Already created")) {
			// don't restart if action is present
			Intent intent = new Intent(this, AddEntryActivity.class);
			startActivity(intent);
			finish();
		} else {
			// remove the unique action so the next time onResume
			// call will force restart
			getIntent().setAction(null);
		}
	}

	// Populates the spinner with the current list of Budgets.
	public void addItemsToBudgetSpinner() {
		// get the actual Budget objects
		final List<Budget> budgetList = Budget.getBudgets();
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
				android.R.layout.simple_spinner_item, budgetNameList);
		// specify the layout to use when the list of choices appears
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// apply the adapter to the spinner
		mBudgetView.setAdapter(dataAdapter);

		// set the spinner to display selection upon selecting an item
		mBudgetView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos,
					long id) {
				// handle the case when user selects 'Create New Budget...'
				if (pos == budgetList.size() && !budgetList.isEmpty()) {
					startActivity(new Intent(AbstractEntryEditorActivity.this, AddBudgetActivity.class));
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
	public abstract void addEntry(View view);

	// Helper method to create the new <code>Entry</code> object to be added.
	protected Entry createEntry() {
		mAmountView.setError(null);

		// checks whether the amount is empty
		if (mAmountView.getText().toString().isEmpty()) {
			mAmountView.setError(getString(R.string.error_invalid_amount));
			mAmountView.requestFocus();
			return null;
		}

		// checks whether the amount is non-zero
		if (Double.parseDouble(mAmountView.getText().toString()) == 0.0) {
			mAmountView.setError(getString(R.string.error_zero_amount));
			mAmountView.requestFocus();
			return null;
		}

		// extract the amount information from its text field
		double doubleAmount = Double.parseDouble(mAmountView.getText().toString());

		// amount will be stored in cents
		int intAmount = (int) (doubleAmount * CENTS);
		Log.d("TAG", "createEntry: amount = " + intAmount);

		String notes = mNotesView.getText().toString();

		// Need to add 1 to the month because the DatePicker
		// has zero-based months.
		LocalDate date = new LocalDate(mDateView.getYear(),
				mDateView.getMonth() + 1, mDateView.getDayOfMonth());
		return new Entry(intAmount, null, notes, date);
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
