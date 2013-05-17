package com.example.budgetmanager;

import java.util.Locale;

import org.joda.time.LocalDate;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
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

/**
 *
 * @author Andrew theclinger
 * @author Joseph josephs2
 *
 */
public class AddBudgetActivity extends Activity {
	// Text field for entering the Budget name
	private EditText mBudgetNameView;

	// Number field for entering the Budget amount
	private EditText mBudgetAmountView;

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
		super.onCreate(savedInstanceState);
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

		// Submit button activity
		findViewById(R.id.create_budget_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						attemptAddBudget();
					}
				});
		findViewById(R.id.clear_budget_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						AddBudgetActivity.this.clearEntry(null);
					}
				});
	}

	/**
	 * Attempts to push the Budget created by the user to the API
	 *
	 * If it succeeds, this activity is finished.
	 *
	 * If it fails, toast the error.
	 */
	public void attemptAddBudget(){
		// check input validity
		boolean cancel = false;
		View focusView = null;
		mBudgetAmountView.setError(null);
		mBudgetNameView.setError(null);

		// checks whether amount is not empty
		if (mBudgetAmountView.getText().toString().isEmpty()) {
			mBudgetAmountView.setError(getString(R.string.error_invalid_amount));
			focusView = mBudgetAmountView;
			cancel = true;
		}
		
		// checks whether the amount is non-zero
		if (!cancel&& Double.parseDouble(mBudgetAmountView.getText().toString()) == 0.0) {
			mBudgetAmountView.setError(getString(R.string.error_zero_amount));
			mBudgetAmountView.requestFocus();
		}

		// checks whether name is not emtpy
		if (mBudgetNameView.getText().toString().isEmpty()) {
			mBudgetNameView.setError(getString(R.string.error_invalid_budget_name));
			focusView = mBudgetNameView;
			cancel = true;
		}

		// cancel adding budget with invalid input
		if (cancel) {
			focusView.requestFocus();
			return;
		}


		// create the Entry object to add to the Budget

		final Budget newBudget = createBudget();
		
		// disable button while calling api
		createButtonView.setClickable(false);

		ApiInterface.getInstance().create(newBudget, new ApiCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				// add the entry into the Budget object
				UBudgetApp app = (UBudgetApp) getApplication();
				app.getBudgetList().add(0, newBudget);
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
	 * Creates a Budget based on the contents of the input fields
	 * @return a Budget with values specified by the input fields
	 */
	private Budget createBudget() {
		String name = mBudgetNameView.getText().toString();

		// Multiply by 100 in order to convert the amount to cents for storage
		String amountText = mBudgetAmountView.getText().toString();
		int amount =  (int) Math.round(Double.parseDouble(amountText) * 100);
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
		Time now = new Time();
		now.setToNow();
		// update the DatePicker
		mBudgetDateView.updateDate(now.year, now.month, now.monthDay);

		mRecurringView.setChecked(false);
	}
}
