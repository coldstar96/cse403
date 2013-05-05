package com.example.budgetmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
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
	final static String EXTRA_MESSAGE = "com.example.budgetmanager.MESSAGE";
	final static int CENTS = 100;
	
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
		List<String> list = new ArrayList<String>();
		list.add("Overall");
		list.add("Food");
		list.add("Clothes");
		list.add("Transportation");
		list.add("Drinks");
		list.add("Groceries");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_layout, list);
		dataAdapter.setDropDownViewResource(R.layout.spinner_entry_layout);
		mBudgetView.setAdapter(dataAdapter);
		
		// set the spinner to display selection upon selecting an item
		mBudgetView.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int pos,
					long id) {    
               Toast.makeText(parent.getContext(), parent.getItemAtPosition(pos).toString()
       				, Toast.LENGTH_LONG).show();
            }

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
		if (mAmountView.getText().toString().equals("")) {
			Toast.makeText(this, "Please specify an amount.", 
					Toast.LENGTH_LONG).show();
		} else {
			// create the Entry object to add to the Budget
			Entry newEntry = createEntry();
			Double doubleAmount = (double) newEntry.getAmount() / CENTS;
			Toast.makeText(this, "Added $" + doubleAmount + " to the " + mBudgetView.getSelectedItem().toString() + " budget "
					+ "with the date of: " + newEntry.getDate() + " with a note of: "
					+ newEntry.getNotes()
	   				, Toast.LENGTH_LONG).show();
		}
	}
	
	private Entry createEntry() {
		// extract the amount information
		double doubleAmount = Double.parseDouble(mAmountView.getText().toString());
		// amount will be stored in cents
		int intAmount = (int) doubleAmount * CENTS;
		
		// TODO: fix this
		Budget budget = null;

		String notes = mNotesView.getText().toString();
		
		// format the string so that the server will parse the date correctly
		String date = mDateView.getYear() + "-" + (mDateView.getMonth() + 1) + "-" + mDateView.getDayOfMonth();
		
		return new Entry(intAmount, budget, notes, date);
	}
	
	/**
	 * Resets the add Entry view.
	 * 
	 * @param view The current state of add Entry view.
	 */
	public void clearEntry(View view) {
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
