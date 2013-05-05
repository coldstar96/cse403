package com.example.budgetmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Activity which allows users to add entries.
 */
public class AddEntryActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.budgetmanager.MESSAGE";

	private final static String TAG = "AddEntryActivity";

	// views to extract information from
	private Spinner mBudgetView;
	private EditText mAmountView;
	private DatePicker mDateView;
	private EditText mNotesView;

	@Override
	/** Called when the activity is first created. */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// inflate view
		setContentView(R.layout.activity_add_entry);

		mBudgetView = (Spinner) findViewById(R.id.spinner_budget);
		mAmountView = (EditText) findViewById(R.id.edit_amount);
		mDateView = (DatePicker) findViewById(R.id.date_picker);
		mNotesView = (EditText) findViewById(R.id.edit_notes);

		// populate list items for the budget selector
		addItemsToBudgetSpinner();
	}

	// Populate the spinner with the current list of Budgets.
	private void addItemsToBudgetSpinner() {
		// TODO: populate with real Budget objects once that is available.

		UBudgetApp appData = (UBudgetApp)getApplication();
		final List<Budget> budgetList = appData.getBudgetList();
		List<String> budgetNameList = new ArrayList<String>();

		for (Budget b : budgetList) {
			Log.d(TAG, b.getName());
			budgetNameList.add(b.getName());
		}

		budgetNameList.add(getResources().getString(R.string.new_budget));

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_layout, budgetNameList);
		dataAdapter.setDropDownViewResource(R.layout.spinner_entry_layout);
		mBudgetView.setAdapter(dataAdapter);

		// set the spinner to display selection upon selecting an item
		mBudgetView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos,
					long id) {
				if (pos == budgetList.size()) {
					// TODO: switch to add budget activity when ready.
					Toast.makeText(parent.getContext(), "new budget!", Toast.LENGTH_LONG).show();
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
	 * @param view The current state of the add Entry view.
	 */
	public void addEntry(View view) {
		// TODO: finish this once Budgets are available
//		Intent intent = new Intent(this, DisplayMessageActivity.class);
//		EditText editText = (EditText) findViewById(R.id.edit_message);
//		String message = editText.getText().toString();
//		intent.putExtra(EXTRA_MESSAGE, message);
//		startActivity(intent);
		Toast.makeText(this, "Added $" + mAmountView.getText().toString() + " to the " + mBudgetView.getSelectedItem().toString() + " budget "
				+ "with the date of: " + (mDateView.getMonth() + 1) + "/" + mDateView.getDayOfMonth() + "/" + mDateView.getYear() + " with a note of: "
				+ mNotesView.getText().toString()
				, Toast.LENGTH_LONG).show();
	}

	/**
	 * Resets the add Entry view.
	 *
	 * @param view The current state of add Entry view.
	 */
	public void clearEntry(View view) {
		//get current time
		Time now = new Time();
		now.setToNow();
		//update the DatePicker
		mDateView.updateDate(now.year, now.month, now.monthDay);

		// clear the EditText fields
		mAmountView.setText("");
		mNotesView.setText("");
	}
}
