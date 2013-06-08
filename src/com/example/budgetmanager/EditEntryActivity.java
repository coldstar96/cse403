package com.example.budgetmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;

import org.joda.time.LocalDate;

import java.text.MessageFormat;

/**
 * Activity which allows users to edit entries.
 *
 * @author Ji jiwpark90
 * @author Graham grahamb5
 */
public class EditEntryActivity extends AbstractEntryEditorActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(MessageFormat.format(getTitle().toString(),
				getString(R.string.title_entry_edit)));
		mAddButtonView.setText(getString(R.string.entry_activity_button_edit));

		// Set fields to saved entry's fields.
		Bundle bundle = getIntent().getExtras();
		Budget b = Budget.getBudgetById(bundle.getLong("BudgetId"));
		Entry e = b.getEntryById(bundle.getLong("EntryId"));

		mAmountView.setText(Utilities.amountToDollarsNoDollarSign(e.getAmount()));

		LocalDate date = e.getDate();
		// subtract 1 from month to adjust to 0-based indexing
		mDateView.updateDate(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());

		mNotesView.setText(e.getNotes());

		ArrayAdapter<String> editAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[]{b.getName()});
		mBudgetView.setAdapter(editAdapter);
		mBudgetView.setEnabled(false);
	}

	/**
	 * Adds a new <code>Entry</code> to the specified <code>Budget</code>.
	 *
	 * @param view The reference to the add button.
	 */
	@Override
	public void addEntry(View view) {
		final Entry newEntry = createEntry();

		if (newEntry == null) {
			// do nothing until add Budget activity is up
			// or the user fixes their errors.
			return;
		}

		mAddButtonView.setClickable(false);

		Bundle bundle = getIntent().getExtras();
		Budget b = Budget.getBudgetById(bundle.getLong("BudgetId"));
		final Entry actualEntry = b.getEntryById(bundle.getLong("EntryId"));

		// We need to send a separate entry, so we don't have to save
		// old values if the request fails.
		newEntry.setEntryId(actualEntry.getEntryId());
		newEntry.setCreatedAt(actualEntry.getCreatedAt());
		newEntry.setUpdatedAt(actualEntry.getUpdatedAt());

		ApiInterface.getInstance().update(newEntry, new ApiCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				// Update all the actualEntry's fields.
				actualEntry.setUpdatedAt(newEntry.getUpdatedAt());
				actualEntry.setAmount(newEntry.getAmount());
				actualEntry.setDate(newEntry.getDate());
				actualEntry.setNotes(newEntry.getNotes());

				// go back to the Entry log
				finish();
			}

			@Override
			public void onFailure(String errorMessage) {
				// if the request fails, do nothing (the toast is for testing purposes)
				Toast.makeText(EditEntryActivity.this, errorMessage, Toast.LENGTH_LONG).show();
				mAddButtonView.setClickable(true);
			}
		});
	}

	// Helper method to create the new <code>Entry</code> object to be added.
	@Override
	protected Entry createEntry() {
		Entry e = super.createEntry();

		if (e == null) {
			return null;
		}

		Budget b = Budget.getBudgetById(getIntent().getExtras().getLong("BudgetId"));
		e.setBudget(b);

		return e;
	}
}
