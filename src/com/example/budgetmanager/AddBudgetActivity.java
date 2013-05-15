package com.example.budgetmanager;

import java.util.Locale;

import org.joda.time.LocalDate;

import com.example.budgetmanager.Budget.Duration;
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
 * @author Andrew theclinger
 * @author Joseph josephs2
 *
 */
public class AddBudgetActivity extends Activity {
	// Text field for entering the Budget name
	private EditText mBudgetName;

	// Number field for entering the Budget amount
	private EditText mBudgetAmmount;

	// Enables the user to pick the start date of the Budget
	private DatePicker mBudgetDate;

	// Enable the user to pick a number of different durations for the Budget
	private Spinner mBudgetDuration;

	// Whether or not this Budget should recur after one cycle
	private CheckBox mRecurring;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// inflate view
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

	/**
	 * Attempts to push the <code>Budget</code> created by the user to the API.
	 *
	 * If it succeeds, this activity is finished.
	 *
	 * If it fails, toast the error.
	 */
	public void attemptAddBudget(){
		// create the Budget object to add to the list of Budgets
		final Budget newBudget = createBudget();

		ApiInterface.getInstance().create(newBudget, new ApiCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				// add the Budget object into the list Budgets
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
			}
		});
	}

	/**
	 * Creates a <code>Budget</code> based on the contents of the input fields.
	 * 
	 * @return a <code>Budget</code> with values specified by the input fields.
	 */
	private Budget createBudget() {
		String name = mBudgetName.getText().toString();

		// Multiply by 100 in order to convert the amount to cents for storage
		String amountText = mBudgetAmmount.getText().toString();
		int amount =  (int) Math.round(Double.parseDouble(amountText) * 100);
		boolean recur = mRecurring.isChecked();

		LocalDate startDate = new LocalDate(mBudgetDate.getYear(),
				mBudgetDate.getMonth() + 1, mBudgetDate.getDayOfMonth());

		// Convert the selected entry in the duration spinner into a string
		// that can be converted into a Duration enum member.
		// Also, explicitly use the default locale to avoid warnings.
		String duration = mBudgetDuration.getSelectedItem()
				.toString()
				.toUpperCase(Locale.getDefault());

		return new Budget(name, amount, recur,
				startDate, Duration.valueOf(duration));
	}
}