package com.example.budgetmanager;

import java.util.Locale;

import org.joda.time.LocalDate;

import com.example.budgetmanager.Budget.Duration;
import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;
import com.example.budgetmanager.preference.SettingsFragment;
import com.example.budgetmanager.preference.SettingsActivity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 *
 * @author Andrew theclinger
 * @author Joseph josephs2
 *
 */
public class AddBudgetActivity extends Activity {
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
		
		// set default values for settings (if never done before)
		PreferenceManager.setDefaultValues(this, R.xml.fragment_settings, false);
		
		// check the preference to see which theme to set
		String startingScreen = PreferenceManager.
				getDefaultSharedPreferences(this).getString(SettingsFragment
				.KEY_PREF_APP_THEME, "");

		if (startingScreen.equals(SettingsFragment
				.APP_THEME_LIGHT)) {
			setTheme(android.R.style.Theme_Holo_Light);
		} else {
			setTheme(android.R.style.Theme_Holo);
		}
		
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuItem buttonSettings = menu.add(R.string.title_settings);
	    // this forces it to go in the overflow menu, which is preferred.
	    buttonSettings.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
	    buttonSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
	    	/** Take the users to the Settings activity upon clicking the button. */
	        public boolean onMenuItemClick(MenuItem item) {
	        	Intent settingsIntent = new Intent(AddBudgetActivity.this, SettingsActivity.class);
	            settingsIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsFragment.class.getName());
	            settingsIntent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);	
	            AddBudgetActivity.this.startActivity(settingsIntent);
	            
	            return false;
	        }
	    });
	    
	    MenuItem buttonSignout = menu.add(R.string.title_signout);
	    buttonSignout.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
	    buttonSignout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
	    	/** Sign out the user upon clicking the button. */
	        public boolean onMenuItemClick(MenuItem item) {
	        	Toast.makeText(AddBudgetActivity.this, "Successfully handled Sign out selection"
						, Toast.LENGTH_LONG).show();
	            return false;
	        }
	    });
	    return true;
	}

	/**
	 * Attempts to push the <code>Budget</code> created by the user to the API.
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
		
		mBudgetAmount = mBudgetAmountView.getText().toString();
		mBudgetName = mBudgetNameView.getText().toString();

		// checks whether amount is not empty
		if (mBudgetAmount.isEmpty()) {
			mBudgetAmountView.setError(getString(R.string.error_invalid_amount));
			focusView = mBudgetAmountView;
			cancel = true;
		}
		
		double amount = Double.parseDouble(mBudgetAmountView.getText().toString());
		
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
