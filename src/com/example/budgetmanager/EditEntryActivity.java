package com.example.budgetmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;

import org.joda.time.LocalDate;

import java.util.List;

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

		setTitle(getString(R.string.title_activity_edit_entry));
		mAddButtonView.setText(getString(R.string.entry_activity_button_edit));

		// Set fields to saved entry's fields.
		Bundle bundle = getIntent().getExtras();
		Budget b = Budget.getBudgetById(bundle.getLong("BudgetId"));
		Entry e = b.getEntryById(bundle.getLong("EntryId"));

		mAmountView.setText(Utilities.amountToCurrencyNoCurrencySign(e.getAmount()));

		LocalDate date = e.getDate();
		// subtract 1 from month to adjust to 0-based indexing
		mDateView.updateDate(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());

		mNotesView.setText(e.getNotes());

		final List<Budget> budgetList = Budget.getBudgets();
		for (int i = 0; i < budgetList.size(); i ++) {
			if(budgetList.get(i).equals(b)) {
				mBudgetView.setSelection(i);
				break;
			}
		}
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
		final Budget oldBudget = Budget.getBudgetById(bundle.getLong("BudgetId"));
		final Entry actualEntry = oldBudget.getEntryById(bundle.getLong("EntryId"));
		final Budget newBudget = newEntry.getBudget();

		// We need to send a separate entry, so we don't have to save
		// old values if the request fails.
		newEntry.setEntryId(actualEntry.getEntryId());
		newEntry.setCreatedAt(actualEntry.getCreatedAt());
		newEntry.setUpdatedAt(actualEntry.getUpdatedAt());

		ApiInterface.getInstance().update(newEntry, new ApiCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				// Add the entry to the new budget and remove the old one from the old budget
				newBudget.addEntry(newEntry);
				oldBudget.removeEntry(actualEntry);

				// go back to the Entry log
				finish();
			}

			@Override
			public void onFailure(String errorMessage) {
				// if the request fails, do nothing (the toast is for testing purposes)
				Toast.makeText(EditEntryActivity.this, errorMessage, Toast.LENGTH_LONG).show();
				// Remove the temporary entry
				newBudget.removeEntry(newEntry);
				mAddButtonView.setClickable(true);
			}
		});
	}

	// Helper method to create the new <code>Entry</code> object to be added.
	@Override
	protected Entry createEntry() {
		return super.createEntry();
	}
}
