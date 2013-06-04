package com.example.budgetmanager;

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

import org.joda.time.LocalDate;

import java.text.MessageFormat;
import java.util.Locale;

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
	private Button mAddButtonView;

	private boolean addMode;

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
		mAddButtonView = (Button) findViewById(R.id.create_budget_button);

		// Sets up the duration dropdown
		mBudgetDurationView = (Spinner) findViewById(R.id.budget_duration);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.duration_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mBudgetDurationView.setAdapter(adapter);

		Bundle bundle = getIntent().getExtras();
		addMode = bundle == null ? true : bundle.getBoolean("Add", true);

		// Set the title and add button
		if (addMode) {
			setTitle(MessageFormat.format(getTitle().toString(),
					getString(R.string.title_budget_add)));
			mAddButtonView.setText(getString(R.string.budget_activity_button_add));
		} else {
			setTitle(MessageFormat.format(getTitle().toString(),
					getString(R.string.title_budget_edit)));
			mAddButtonView.setText(getString(R.string.budget_activity_button_edit));

			// Populate the fields with the current budget data
			Budget b = Budget.getBudgetById(bundle.getLong("BudgetId"));
			mBudgetNameView.setText(b.getName());
			mBudgetAmountView.setText(Utilities.amountToDollarsNoDollarSign(b.getBudgetAmount()));
			mRecurringView.setChecked(b.isRecurring());
			// subtract 1 from month to adjust to 0-based indexing
			mBudgetDateView.updateDate(b.getStartDate().getYear(),
					b.getStartDate().getMonthOfYear() - 1, b.getStartDate().getDayOfMonth());
			switch (b.getDuration()) {
			case DAY:
				mBudgetDurationView.setSelection(0);
				break;
			case WEEK:
				mBudgetDurationView.setSelection(1);
				break;
			case FORTNIGHT:
				mBudgetDurationView.setSelection(2);
				break;
			case MONTH:
				mBudgetDurationView.setSelection(3);
				break;
			case YEAR:
				mBudgetDurationView.setSelection(4);
				break;
			default:
				throw new IllegalArgumentException("Invaid duration argument");
			}
		}
		// trick to prevent infinite looping when onResume() is called
		getIntent().setAction("Already created");
	}

	@Override
	protected void onResume() {
		super.onResume();

		String action = getIntent().getAction();
		if (action == null || !action.equals("Already created")) {
			// don't restart if action is present
			Intent intent = new Intent(this, AddBudgetActivity.class);
			startActivity(intent);
			finish();
		} else {
			// remove the unique action so the next time onResume
			// call will force restart
			getIntent().setAction(null);
		}
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
			ApiInterface.getInstance().logOut();
			Intent logOut = new Intent(AddBudgetActivity.this, LoginActivity.class);
			// Clear the back stack so when you press the back button you will exit the app
			logOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			// Goes to the login page
			startActivity(logOut);
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

		Bundle bundle = getIntent().getExtras();

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

		// checks whether name is not empty
		if (mBudgetName.isEmpty()) {
			mBudgetNameView.setError(getString(R.string.error_invalid_budget_name));
			focusView = mBudgetNameView;
			cancel = true;
		} else {
			// Check to see if there's a budget with that name already
			String previousBudgetName = Budget.getBudgetById(bundle.getLong("BudgetId")).getName();
			for (Budget budget : Budget.getBudgets()) {
				boolean addNameCheck = addMode && budget.getName().equals(mBudgetName);
				boolean editNameCheck = false;
				if (!addMode) {
					// If the budget name is not the one we're editing and exists already, then throw error
					editNameCheck = budget.getName().equals(mBudgetName) && !previousBudgetName.equals(mBudgetName);
				}
				if (addNameCheck || editNameCheck) {
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


		// disable button while calling api
		mAddButtonView.setClickable(false);

		// create the Budget object to add to the list of Budgets
		final Budget newBudget = createBudget();

		if (addMode) {
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
					// Remove the budget from the budget list, as it wasn't added.
					Budget.removeBudget(newBudget);
					mAddButtonView.setClickable(true);
				}
			});
		} else {
			final Budget actualBudget = Budget.getBudgetById(bundle.getLong("BudgetId"));
			// In case the request fails
			newBudget.setId(actualBudget.getId());

			ApiInterface.getInstance().update(newBudget, new ApiCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
					actualBudget.setId(newBudget.getId());
					actualBudget.setName(newBudget.getName());
					actualBudget.setBudgetAmount(newBudget.getBudgetAmount());
					actualBudget.setRecurring(newBudget.isRecurring());
					actualBudget.setDuration(newBudget.getDuration());
					actualBudget.setStartDate(newBudget.getStartDate());
					// Remove the temporary budget
					Budget.removeBudget(newBudget);

					finish();
				}

				@Override
				public void onFailure(String errorMessage) {
					// if the request fails, do nothing (the toast is for testing purposes)
					Toast.makeText(AddBudgetActivity.this, errorMessage, Toast.LENGTH_LONG).show();
					// Remove the temporary budget
					Budget.removeBudget(newBudget);
					mAddButtonView.setClickable(true);
				}
			});
		}


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
