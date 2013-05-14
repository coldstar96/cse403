package com.example.budgetmanager;

import java.util.Calendar;
import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;

import android.os.Bundle;
import android.app.Activity;
import android.text.format.Time;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.CheckBox;

/**
 *
 * @author Andrew theclinger
 * @author Joseph josephs2
 *
 */
public class AddBudgetActivity extends Activity {


	private EditText mBudgetNameView;
	private EditText mBudgetAmountView;
	private DatePicker mBudgetDateView;
	private Spinner mBudgetDurationView;
	private CheckBox mRecurringView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_budget);

		mBudgetNameView = (EditText) findViewById(R.id.budget_name);
		mBudgetAmountView = (EditText) findViewById(R.id.budget_amount);
		mBudgetDateView = (DatePicker) findViewById(R.id.budget_date);
		mRecurringView = (CheckBox) findViewById(R.id.budget_recur);

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


	public void attemptAddBudget(){
		// check input validity
		boolean cancel = false;
		View focusView = null;
		
		// checks whether amount is not empty
		if (mBudgetAmountView.getText().toString().isEmpty()) {
			mBudgetAmountView.setError(getString(R.string.error_invalid_amount));
			focusView = mBudgetAmountView;
			cancel = true;
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
				// if the request fails, do nothing (the toast is for testing purposes)
				Toast.makeText(AddBudgetActivity.this, "FAILED", Toast.LENGTH_LONG).show();
			}
		});
	}

	private Budget createBudget() {
		String name = mBudgetNameView.getText().toString();
		int amount =  (int) Math.round(Double.parseDouble(mBudgetAmountView.getText().toString()) * 100);
		int currentAmount = 0;
		boolean recur = mRecurringView.isChecked();

		Calendar cal = Calendar.getInstance();
		cal.set(mBudgetDateView.getYear(), mBudgetDateView.getMonth(), mBudgetDateView.getDayOfMonth());

		String duration = mBudgetDurationView.getSelectedItem().toString().toUpperCase();
		int otherDuration = 0;

		return new Budget(name, amount, currentAmount, recur,
				cal.getTimeInMillis(), duration, otherDuration);
	}
	
	/**
	 * Resets the add budget view.
	 *
	 * @param view The reference to the clear button.
	 */
	public void clearEntry(View view) {
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