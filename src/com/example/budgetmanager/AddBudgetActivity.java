package com.example.budgetmanager;

import java.util.Calendar;
import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.CheckBox;

/**
 *
 * @author andrew theclinger
 * @author joseph josephs2
 *
 */
public class AddBudgetActivity extends Activity {


	private EditText mBudgetName;
	private EditText mBudgetAmmount;
	private DatePicker mBudgetDate;
	private Spinner mBudgetDuration;
	private CheckBox mRecurring;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_budget);

		mBudgetName = (EditText) findViewById(R.id.budget_name);
		mBudgetAmmount = (EditText) findViewById(R.id.budget_amount);
		mBudgetDate = (DatePicker) findViewById(R.id.budget_date);
		mRecurring = (CheckBox) findViewById(R.id.budget_recur);

		// Sets up the duration dropdown
		mBudgetDuration = (Spinner) findViewById(R.id.budget_duration);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.duration_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mBudgetDuration.setAdapter(adapter);

		// Submit button activity
		findViewById(R.id.budget_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						attemptAddBudget();
					}
				});
	}


	public void attemptAddBudget(){
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
		String name = mBudgetName.getText().toString();
		int amount =  (int) Math.round(Double.parseDouble(mBudgetAmmount.getText().toString()) * 100);
		int currentAmount = 0;
		boolean recur = mRecurring.isChecked();

		Calendar cal = Calendar.getInstance();
		cal.set(mBudgetDate.getYear(), mBudgetDate.getMonth(), mBudgetDate.getDayOfMonth());

		String duration = mBudgetDuration.getSelectedItem().toString().toUpperCase();
		int otherDuration = 0;

		return new Budget(name, amount, currentAmount, recur,
				cal.getTimeInMillis(), duration, otherDuration);
	}
}