package com.example.budgetmanager;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.text.InputFilter;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.budgetmanager.preference.SettingsActivity;
import com.example.budgetmanager.preference.SettingsFragment;

/**
 * Activity which allows users to add entries.
 *
 * @author Ji jiwpark90
 */
public class AddEntryActivity extends Activity {
	public final static int CENTS = 100;

	// tag for logging
	private final static String TAG = "AddEntryActivity";

	// List of Budgets to choose from
	private Spinner mBudgetView;

	// Number field for entering the Entry amount
	private EditText mAmountView;

	// Enables the user to pick the start date of the Budget
	private DatePicker mDateView;

	// Text field for entering notes for the Entry
	private EditText mNotesView;
	private Button mAddButtonView;

	private boolean addMode;

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

		// Set all the add/edit specific fields.
		Bundle bundle = getIntent().getExtras();
		addMode = bundle == null ? true : bundle.getBoolean("Add", true);
		if (addMode) {
			setTitle(MessageFormat.format(getTitle().toString(),
					getString(R.string.title_entry_add)));
			mAddButtonView.setText(getString(R.string.entry_activity_button_add));
		} else {
			setTitle(MessageFormat.format(getTitle().toString(),
					getString(R.string.title_entry_edit)));
			mAddButtonView.setText(getString(R.string.entry_activity_button_edit));

			// Set fields to saved entry's fields.
			Budget b = Budget.getBudgetById(bundle.getLong("BudgetId"));
			Entry e = b.getEntryById(bundle.getLong("EntryId"));

			mAmountView.setText(Utilities.amountToDollarsNoDollarSign(e.getAmount()));

			LocalDate date = e.getDate();
			// subtract 1 from month to adjust to 0-based indexing
			mDateView.updateDate(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());

			mNotesView.setText(e.getNotes());

			final List<Budget> budgetList = Budget.getBudgets();
			for (int i = 0; i < budgetList.size(); i ++) {
				if(budgetList.get(i).equals(b)) {
					mBudgetView.setSelection(i);
					break;
				}
			}
		}

		// trick to prevent infinite looping when onResume() is called
		getIntent().setAction("Already created");
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
			Intent settingsIntent = new Intent(AddEntryActivity.this,
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
			Intent logOut = new Intent(AddEntryActivity.this, LoginActivity.class);
			// Clear the back stack so when you press the back button you will exit the app
			logOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			// Goes to the login page
			startActivity(logOut);
			return false;
		}
		return true;
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

		final Entry newEntry = createEntry();

		if (newEntry == null) {
			// do nothing until add Budget activity is up
			return;
		}
		mAddButtonView.setClickable(false);

		if (addMode) {
			ApiInterface.getInstance().create(newEntry, new ApiCallback<Long>() {
				@Override
				public void onSuccess(Long result) {
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
					Toast.makeText(AddEntryActivity.this, errorMessage, Toast.LENGTH_LONG).show();
					mAddButtonView.setClickable(true);
				}
			});
		} else {
			Bundle bundle = getIntent().getExtras();
			final Budget oldBudget = Budget.getBudgetById(bundle.getLong("BudgetId"));
			final Entry actualEntry = oldBudget.getEntryById(bundle.getLong("EntryId"));
			final Budget newBudget = newEntry.getBudget();

			// We need to send a separate entry, so we don't have to save
			// old values if the request fails.
			newEntry.setEntryId(actualEntry.getEntryId());
			newEntry.setCreatedAt(actualEntry.getCreatedAt());
			newEntry.setUpdatedAt(actualEntry.getUpdatedAt());

			ApiInterface.getInstance().update(newEntry, new ApiCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
					// Add the entry to the new budget and remove the old one from the old budget
					newBudget.addEntry(newEntry);
					oldBudget.removeEntry(actualEntry);

					// go back to the Entry log
					finish();
				}

				@Override
				public void onFailure(String errorMessage) {
					// if the request fails, do nothing (the toast is for testing purposes)
					Toast.makeText(AddEntryActivity.this, errorMessage, Toast.LENGTH_LONG).show();
					// Remove the temporary entry
					newBudget.removeEntry(newEntry);
					mAddButtonView.setClickable(true);
				}
			});
		}
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
		final List<Budget> budgetList = Budget.getBudgets();
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
