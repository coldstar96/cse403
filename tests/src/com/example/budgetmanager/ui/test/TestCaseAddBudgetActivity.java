package com.example.budgetmanager.ui.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.budgetmanager.AddBudgetActivity;
import com.example.budgetmanager.Budget;
import com.example.budgetmanager.api.test.AsyncHttpClientStub;
import com.example.budgetmanager.test.TestUtilities;
import com.jayway.android.robotium.solo.Solo;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Test the add budget activity.
 *
 * @author chris brucec5
 *
 */
public class TestCaseAddBudgetActivity
extends ActivityInstrumentationTestCase2<AddBudgetActivity> {

	private Solo solo;
	private EditText nameField;
	private EditText amountField;
	private CheckBox recurCheckBox;

	private AsyncHttpClientStub testClient;

	public TestCaseAddBudgetActivity() {
		super(AddBudgetActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());

		// Get all of the UI elements to interact with, avoiding the use of
		// finding by text because Jenkins really doesn't like that.
		nameField = (EditText) getActivity().findViewById(
				com.example.budgetmanager.R.id.budget_name);
		amountField = (EditText) getActivity().findViewById(
				com.example.budgetmanager.R.id.budget_amount);
		recurCheckBox = (CheckBox) getActivity().findViewById(
				com.example.budgetmanager.R.id.budget_recur);

		// Set up the stubbed test client
		testClient = new AsyncHttpClientStub();
		TestUtilities.getStubbedApiInterface(testClient);

		Budget.clearBudgets();
	}

	@Override
	protected void tearDown() {
		solo.finishOpenedActivities();
	}

	/**
	 * Ensure that an amount of 0 is question.
	 *
	 * This is a Black Box test.
	 */
	@MediumTest
	public void test_zeroAmountDollarsValidName_shouldNotAllowIt() {
		// Enter in UI text: 0 amount should fail
		solo.typeText(amountField, "0");
		solo.typeText(nameField, "Budget with 0 Amount");
		solo.clickOnButton("Add");
		solo.sleep(500);

		// We should have an error on amount where you must have a positive
		// amount
		String expectedError = "Amount must be greater than $0";
		String foundError = (String) amountField.getError();

		assertNotNull("There was no error on the amount field", foundError);
		assertEquals(expectedError, foundError);

		// There should be no error on nameField
		assertNull(nameField.getError());
	}

	/**
	 * Ensure that an amount of 0.00 is out of the question.
	 *
	 * This is a black box test.
	 */
	@MediumTest
	public void test_zeroAmountDollarsAndCentsValidName_shouldNotAllowIt() {
		// Enter in UI text: 0.00 amount should fail
		solo.typeText(amountField, "0.00");
		solo.typeText(nameField, "Budget with 0.00 Amount");
		solo.clickOnButton("Add");
		solo.sleep(500);

		// We should have an error on amount where you must have a positive
		// amount
		String expectedError = "Amount must be greater than $0";
		String foundError = (String) amountField.getError();

		assertNotNull("There was no error on the amount field", foundError);
		assertEquals(expectedError, foundError);

		// There should be no error on nameField
		assertNull(nameField.getError());
	}

	// Test written to fix issue #66
	// https://github.com/coldstar96/cse403/issues/66
	/**
	 * Ensure an empty amount results in a reported error.
	 *
	 * This is a Black Box test.
	 */
	@MediumTest
	public void test_emptyAmountValidName_shouldNotAllowIt() {
		// Leave the amount field empty
		solo.typeText(nameField, "Empty Amount Budget");
		solo.clickOnButton("Add");
		solo.sleep(500);

		// Amount field should complain about being empty
		String expectedError = "Please specify amount";
		String foundError = (String) amountField.getError();

		assertNotNull("There was no error on the amount field", foundError);
		assertEquals(expectedError, foundError);

		// There should be no error on nameField
		assertNull(nameField.getError());
	}

	/**
	 * Ensure an empty name results in a reported error.
	 *
	 * This is a Black Box test.
	 */
	@MediumTest
	public void test_emptyNameValidAmount_shouldNotAllowIt() {
		// Neglect to enter in a name
		solo.typeText(amountField, "1.00");
		solo.clickOnButton("Add");
		solo.sleep(500);

		// Name field should complain about being empty
		String expectedError = "Please specify name";
		String foundError = (String) nameField.getError();

		assertNotNull("There was no error on the name field", foundError);
		assertEquals(expectedError, foundError);

		// There should be no error on amountField
		assertNull(amountField.getError());
	}

	// Test written to fix issue #68
	// https://github.com/coldstar96/cse403/issues/68
	/**
	 * Ensure a budget name that is already in use for you can't
	 * get used again.
	 *
	 * This is a Black Box test.
	 */
	@MediumTest
	public void test_takenNameValidAmount_shouldNotAllowIt() {
		// Add a budget with this name to the budget list
		// Enter a name that is the same as the other budget, and valid amount.
		String budgetName = "Duplicate Budget Name";

		@SuppressWarnings("unused")
		Budget b = new Budget(budgetName, 0, false, LocalDate.now(),
				Budget.Duration.WEEK);

		solo.typeText(nameField, budgetName);
		solo.typeText(amountField, "1.00");
		solo.clickOnButton("Add");
		solo.sleep(500);

		// We expect the budget field complain about duplicate names
		String expectedError = "Budget name already in use";
		String foundError = (String) nameField.getError();

		assertNotNull("There was no error on the name field", foundError);
		assertEquals(expectedError, foundError);

		// There should be no error on amountField
		assertNull(amountField.getError());
	}

	/**
	 * Ensure fields are reset upon clicking the clear button.
	 *
	 * This is a black box test.
	 */
	@MediumTest
	public void test_clear_shouldResetFields() {
		// Set up some data in the fields
		solo.typeText(nameField, "Clearing Budget");
		solo.typeText(amountField, "12345.00");
		solo.clickOnCheckBox(0);
		solo.sleep(500);

		// Clear the fields and wait for animations
		solo.clickOnButton("Clear");
		solo.sleep(500);

		// The fields should be back to their defaults.
		String nameText = nameField.getText().toString();
		String amountText = amountField.getText().toString();
		boolean recurChecked = recurCheckBox.isChecked();

		assertEquals("Name field was not empty after clearing", "", nameText);
		assertEquals("Amount field was not empty after clearing", "", amountText);
		assertFalse("Recur CheckBox was checked after clearing", recurChecked);
	}

	/**
	 * Ensure that, on server failure, the newly added budget is
	 * removed from the budget list.
	 *
	 * This is a black-box test of the AddBudgetActivity.
	 */
	@MediumTest
	public void test_addValidBudget_apiError() {
		// Set up some data in the fields
		solo.enterText(nameField, "Valid Budget");
		solo.enterText(amountField, "12345.00");
		solo.clickOnCheckBox(0);
		solo.sleep(500);

		testClient.setNextResponse(new JSONObject(), false);

		solo.clickOnButton("Add");
		solo.sleep(500);

		// make sure no budget with ID -1 is in budget list
		// AKA the network failure was acknowledged and actions
		// were taken to reverse the addition of the budget.
		Budget budget = Budget.getBudgetById(-1);
		assertNull("No budget should exist with ID -1 after failure.", budget);
	}

	/**
	 * Ensure that, on server success, the newly added budget is
	 * present in the budget list and has valid fields.
	 *
	 * This is a black-box test of the AddBudgetActivity.
	 */
	@MediumTest
	public void test_addValidBudget_newBudgetIsAdded() throws JSONException {
		final String BUDGET_NAME = "Valid Budget";
		final String BUDGET_AMOUNT = "12345.00";
		final long BUDGET_ID = -2;

		// Set up some data in the fields
		solo.enterText(nameField, BUDGET_NAME);
		solo.enterText(amountField, BUDGET_AMOUNT);
		solo.sleep(500);

		// Set the server response.
		JSONObject serverResponse = new JSONObject();
		serverResponse.put("id", BUDGET_ID);

		testClient.setNextResponse(serverResponse, true);

		solo.clickOnButton("Add");
		solo.sleep(500);

		// Make sure there is a budget with ID BUDGET_ID in the list
		// of budgets, as the server returned saying it had been added.
		Budget b = Budget.getBudgetById(BUDGET_ID);
		assertNotNull("There should be a budget with ID -2.", b);
		assertEquals("The name of the budget was wrong.", BUDGET_NAME, b.getName());
		assertEquals("The amount of the budget was wrong.",
				(int) (Double.parseDouble(BUDGET_AMOUNT) * 100), b.getBudgetAmount());
	}

}
