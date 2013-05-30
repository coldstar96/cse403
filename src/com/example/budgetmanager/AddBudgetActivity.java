package com.example.budgetmanager;

import java.util.Locale;

import org.joda.time.LocalDate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.budgetmanager.Budget.Duration;
import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;
import com.example.budgetmanager.preference.SettingsActivity;
import com.example.budgetmanager.preference.SettingsFragment;

/**
 *
 * @author Andrew theclinger
 * @author Joseph josephs2
 *
 */
public class AddBudgetActivity extends Activity {
	private final int DOLLAR_IN_CENTS = 100;

	// Text field for entering the Budget name
	private EditText mBudgetNameView;
	private String mBudgetName;

	// Number field for entering the Budget amount
	private EditText mBudgetAmountView;
	private String mBudgetAmount;

	// Enables the user to pick the start date of the Budget
	private DatePicker mBudgetDateView;

	// Enable the user to pick a number of different durations for the Budget
	private Spinner mBudgetDurationView;

	// Whether or not this Budget should recur after one cycle
	private CheckBox mRecurringView;

	// Create button
	private Button createButtonView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// set theme based on current preferences	
		Utilities.setActivityTheme(this, getApplicationContext());

		super.onCreate(savedInstanceState);

		// inflate view
		setContentView(R.layout.activity_add_budget);

		mBudgetNameView = (EditText) findViewById(R.id.budget_name);
		mBudgetAmountView = (EditText) findViewById(R.id.budget_amount);
		mBudgetDateView = (DatePicker) findViewById(R.id.budget_date);
		mRecurringView = (CheckBox) findViewById(R.id.budget_recur);
		createButtonView = (Button) findViewById(R.id.create_budget_button);

		// Sets up the duration dropdown
		mBudgetDurationView = (Spinner) findViewById(R.id.budget_duration);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.duration_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mBudgetDurationView.setAdapter(adapter);
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
			Intent settingsIntent = new Intent(AddBudgetActivity.this,
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
			// TODO implement a signout functionality
			Toast.makeText(AddBudgetActivity.this,
					"Successfully handled Sign out selection",
					Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}

	/**
	 * Attempts to push the <code>Budget</code> created by the user to the API.
	 *
	 * If it succeeds, this activity is finished.
	 *
	 * If it fails, toast the error.
	 */
	public void attemptAddBudget(View view) {
		// check input validity
		boolean cancel = false;
		View focusView = null;
		double amount = 0.0;

		mBudgetAmountView.setError(null);
		mBudgetNameView.setError(null);

		mBudgetAmount = mBudgetAmountView.getText().toString();
		mBudgetName = mBudgetNameView.getText().toString();

		// checks whether amount is not empty
		if (mBudgetAmount.isEmpty()) {
			mBudgetAmountView.setError(getString(R.string.error_invalid_amount));
			focusView = mBudgetAmountView;
			cancel = true;
		} else {
			// Only attempt to parse the amount if it's non-empty.
			amount = Double.parseDouble(mBudgetAmountView.getText().toString());
		}

		// checks whether the amount is non-zero
		if (!cancel && amount == 0.0) {
			mBudgetAmountView.setError(getString(R.string.error_zero_amount));
			mBudgetAmountView.requestFocus();
			focusView = mBudgetAmountView;
			cancel = true;
		}

		// checks whether name is not emtpy
		if (mBudgetName.isEmpty()) {
			mBudgetNameView.setError(getString(R.string.error_invalid_budget_name));
			focusView = mBudgetNameView;
			cancel = true;
		} else {
			// Check to see if there's a budget with that name already
			for (Budget budget : Budget.getBudgets()) {
				if (budget.getName().equals(mBudgetName)) {
					mBudgetNameView.setError(getString(
							R.string.error_name_already_exists));

					focusView = mBudgetNameView;
					cancel = true;
					break;
				}
			}
		}

		// cancel adding budget with invalid input
		if (cancel) {
			focusView.requestFocus();
			return;
		}

		// create the Budget object to add to the list of Budgets
		final Budget newBudget = createBudget();

		// disable button while calling api
		createButtonView.setClickable(false);

		ApiInterface.getInstance().create(newBudget, new ApiCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				finish();
			}

			@Override
			public void onFailure(String errorMessage) {
				// if the request fails, do nothing
				// (the toast is for testing and debug purposes)
				Toast.makeText(AddBudgetActivity.this, errorMessage,
						Toast.LENGTH_LONG).show();
				createButtonView.setClickable(true);
			}
		});
	}

	/**
	 * Creates a <code>Budget</code> based on the contents of the input fields.
	 *
	 * @return a <code>Budget</code> with values specified by the input fields.
	 */
	private Budget createBudget() {
		String name = mBudgetNameView.getText().toString();

		// Multiply by 100 in order to convert the amount to cents for storage
		String amountText = mBudgetAmountView.getText().toString();
		int amount =  (int) Math.round(Double.parseDouble(amountText) * DOLLAR_IN_CENTS);
		boolean recur = mRecurringView.isChecked();

		LocalDate startDate = new LocalDate(mBudgetDateView.getYear(),
				mBudgetDateView.getMonth() + 1, mBudgetDateView.getDayOfMonth());

		// Convert the selected entry in the duration spinner into a string
		// that can be converted into a Duration enum member.
		// Also, explicitly use the default locale to avoid warnings.
		String duration = mBudgetDurationView.getSelectedItem()
				.toString()
				.toUpperCase(Locale.getDefault());

		return new Budget(name, amount, recur,
				startDate, Duration.valueOf(duration));
	}

	/**
	 * Resets the add budget view.
	 *
	 * @param view The reference to the clear button.
	 */
	public void clearEntry(View view) {
		mBudgetAmountView.setError(null);
		mBudgetNameView.setError(null);

		// clear the EditText fields
		mBudgetAmountView.setText("");
		mBudgetNameView.setText("");

		// get current time
		LocalDate now = LocalDate.now();
		// update the DatePicker
		mBudgetDateView.updateDate(now.getYear(),
				now.getMonthOfYear() - 1,
				now.getDayOfMonth());

		mRecurringView.setChecked(false);

		mBudgetDurationView.setSelection(0);
	}
}
