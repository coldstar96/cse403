package com.example.budgetmanager.ui.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.EditText;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Budget.Duration;
import com.example.budgetmanager.EditBudgetActivity;
import com.example.budgetmanager.api.test.AsyncHttpClientStub;
import com.example.budgetmanager.test.TestUtilities;
import com.jayway.android.robotium.solo.Solo;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tests for editing budgets. A lot of the tests from
 * {@link TestCaseAddBudgetActivity} are relevant and
 * identical, because it uses the same activity
 * underneath.
 * 
 * @author Graham grahamb5
 */
public class TestCaseEditBudgetActivity
extends ActivityInstrumentationTestCase2<EditBudgetActivity> {

	private Solo solo;
	private EditText nameField;

	private AsyncHttpClientStub testClient;

	public TestCaseEditBudgetActivity() {
		super(EditBudgetActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setUpBudgetList();

		Intent intent = new Intent();
		intent.putExtra("Add", false);
		intent.putExtra("BudgetId", TEST_BUDGET_ID);
		setActivityIntent(intent);

		solo = new Solo(getInstrumentation(), getActivity());

		// Get all of the UI elements to interact with, avoiding the use of
		// finding by text because Jenkins really doesn't like that.
		nameField = (EditText) getActivity().findViewById(
				com.example.budgetmanager.R.id.budget_name);

		// Set up the stubbed test client
		testClient = new AsyncHttpClientStub();
		TestUtilities.getStubbedApiInterface(testClient);
	}

	private static final String TEST_BUDGET_NAME = "Test Budget";
	private static final long TEST_BUDGET_ID = -2;

	/**
	 * Sets up the Budget list for this test.
	 */
	private void setUpBudgetList() {
		Budget.clearBudgets();
		Budget testBudget = new Budget(TEST_BUDGET_NAME, 1234500,
				false, LocalDate.now(), Duration.DAY);
		testBudget.setId(TEST_BUDGET_ID);
	}

	/**
	 * Ensure that, on server failure, the edited budget is
	 * reverted back to it's original state. Also ensures
	 * that no extra budget is in the budget list.
	 *
	 * This is a black-box test of the AddBudgetActivity.
	 */
	@MediumTest
	public void test_editValidBudget_apiError() {
		String BUDGET_NAME = "Edited Budget";

		// Update one field.
		solo.clearEditText(nameField);
		solo.enterText(nameField, BUDGET_NAME);
		solo.sleep(1000);

		// Set the next response.
		testClient.setNextResponse(new JSONObject(), false);

		assertEquals("Budget list should only have one inside.",
				1, Budget.getBudgets().size());

		Budget originalBudget = Budget.getBudgetById(TEST_BUDGET_ID);
		assertNotNull("Original budget should exist.", originalBudget);

		// Perform click.
		solo.clickOnButton("Submit");
		solo.sleep(1000);

		// Ensure that no updates persisted to the budget.
		assertEquals("Budget list should still have only one inside.",
				1, Budget.getBudgets().size());
		originalBudget = Budget.getBudgetById(TEST_BUDGET_ID);
		assertNotNull("Original budget should still exist.", originalBudget);
		assertEquals("Budget name should not have changed.",
				TEST_BUDGET_NAME, originalBudget.getName());
	}

	/**
	 * Ensure that, on server success, the edited budget exhibits
	 * the changes made and that no extra budget is in the list.
	 *
	 * This is a black-box test of the AddBudgetActivity.
	 */
	@MediumTest
	public void test_editValidBudget_newBudgetIsAdded() throws JSONException {
		String BUDGET_NAME = "Edited Budget";

		// Update one field.
		solo.clearEditText(nameField);
		solo.enterText(nameField, BUDGET_NAME);
		solo.sleep(1000);

		// Set the next response.
		testClient.setNextResponse(new JSONObject(), true);

		// Ensure that the budget list is correct.
		assertEquals("Budget list should only have one inside.",
				1, Budget.getBudgets().size());

		Budget originalBudget = Budget.getBudgetById(TEST_BUDGET_ID);
		assertNotNull("Original budget should exist.", originalBudget);

		// Perform click.
		solo.clickOnButton("Submit");
		solo.sleep(1000);

		// Ensure that updates occured to the budget.
		assertEquals("Budget list should still have only one inside.",
				1, Budget.getBudgets().size());
		originalBudget = Budget.getBudgetById(TEST_BUDGET_ID);
		assertNotNull("Original budget should still exist.", originalBudget);
		assertEquals("Budget name should have changed.",
				BUDGET_NAME, originalBudget.getName());
	}

}
