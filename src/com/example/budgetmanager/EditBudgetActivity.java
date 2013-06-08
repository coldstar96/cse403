package com.example.budgetmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;

public class EditBudgetActivity extends AbstractBudgetEditorActivity {

	private String mPreviousBudgetName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAddButtonView.setText(getString(R.string.budget_activity_button_edit));

		Bundle bundle = getIntent().getExtras();

		// Populate the fields with the current budget data
		Budget b = Budget.getBudgetById(bundle.getLong("BudgetId"));

		mPreviousBudgetName = b.getName();
		mBudgetNameView.setText(mPreviousBudgetName);

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

		// trick to prevent infinite looping when onResume() is called
		getIntent().setAction("Already created");
	}

	public void attemptAddBudget(View view) {
		boolean ok = commonValidations();

		// Ensure unique names (aside from the previous name)
		if (!mPreviousBudgetName.equals(mBudgetName)) {
			ok = ok && nameIsUnique();
		}

		// If the fields don't validate, return.
		if (!ok) {
			return;
		}

		Bundle bundle = getIntent().getExtras();
		final Budget actualBudget = Budget.getBudgetById(bundle.getLong("BudgetId"));
		// In case the request fails
		final Budget newBudget = createBudget();
		newBudget.setId(actualBudget.getId());

		mAddButtonView.setClickable(false);

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
				Toast.makeText(EditBudgetActivity.this, errorMessage, Toast.LENGTH_LONG).show();
				// Remove the temporary budget
				Budget.removeBudget(newBudget);
				mAddButtonView.setClickable(true);
			}
		});
	}
}
