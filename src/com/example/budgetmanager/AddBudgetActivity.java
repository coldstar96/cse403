package com.example.budgetmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;

/**
 *
 * @author Andrew theclinger
 * @author Joseph josephs2
 *
 */
public class AddBudgetActivity extends AbstractBudgetEditorActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// set theme based on current preferences
		Utilities.setActivityTheme(this, getApplicationContext());

		super.onCreate(savedInstanceState);

		// Set the title and add button
		mAddButtonView.setText(getString(R.string.budget_activity_button_add));
		// trick to prevent infinite looping when onResume() is called
		getIntent().setAction("Already created");
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
		boolean ok = commonValidations();

		// Ensure unique names
		ok = ok && nameIsUnique();

		// Don't proceed if there are any problems
		if (!ok) {
			return;
		}

		// disable button while calling api
		mAddButtonView.setClickable(false);

		// create the Budget object to add to the list of Budgets
		final Budget newBudget = createBudget();

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
	}
}
