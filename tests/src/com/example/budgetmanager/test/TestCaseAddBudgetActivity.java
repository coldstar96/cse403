package com.example.budgetmanager.test;

import org.joda.time.LocalDate;

import com.example.budgetmanager.AddBudgetActivity;
import com.example.budgetmanager.Budget;
import com.example.budgetmanager.UBudgetApp;
import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

public class TestCaseAddBudgetActivity
	extends ActivityInstrumentationTestCase2<AddBudgetActivity> {

	private Solo solo;
	private EditText nameField;
	private EditText amountField;

	private UBudgetApp app;

	public TestCaseAddBudgetActivity() {
		super(AddBudgetActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());

		nameField = solo.getEditText("Budget Name");
		amountField = solo.getEditText("Amount");

		app = (UBudgetApp) getActivity().getApplication();
		app.getBudgetList().clear();
	}

	public void test_zeroAmountDollarsValidName_shouldNotAllowIt() {
		solo.enterText(nameField, "Budget with 0 Amount");
		solo.enterText(amountField, "0");

		solo.clickOnButton("Create");
		solo.sleep(500);

		String expectedError = "Amount must be greater than $0";
		String foundError = (String) amountField.getError();

		assertNotNull("There was no error on the amount field", foundError);
		assertEquals(expectedError, foundError);

		// There should be no error on nameField
		assertNull(nameField.getError());
	}

	public void test_zeroAmountDollarsAndCentsValidName_shouldNotAllowIt() {
		solo.enterText(nameField, "Budget with 0.00 Amount");
		solo.enterText(amountField, "0.00");

		solo.clickOnButton("Create");
		solo.sleep(500);

		String expectedError = "Amount must be greater than $0";
		String foundError = (String) amountField.getError();

		assertNotNull("There was no error on the amount field", foundError);
		assertEquals(expectedError, foundError);

		// There should be no error on nameField
		assertNull(nameField.getError());
	}

	// Test written to fix issue #66
	// https://github.com/coldstar96/cse403/issues/66
	public void test_emptyAmountValidName_shouldNotAllowIt() {
		solo.enterText(nameField, "Empty Amount Budget");

		solo.clickOnButton("Create");
		solo.sleep(500);

		String expectedError = "Please specify amount";
		String foundError = (String) amountField.getError();

		assertNotNull("There was no error on the amount field", foundError);
		assertEquals(expectedError, foundError);

		// There should be no error on nameField
		assertNull(nameField.getError());
	}

	public void test_emptyNameValidAmount_shouldNotAllowIt() {
		solo.enterText(amountField, "1.00");

		solo.clickOnButton("Create");
		solo.sleep(500);

		String expectedError = "Please specify name";
		String foundError = (String) nameField.getError();

		assertNotNull("There was no error on the name field", foundError);
		assertEquals(expectedError, foundError);

		// There should be no error on amountField
		assertNull(amountField.getError());
	}

	// Test written to fix issue #68
	// https://github.com/coldstar96/cse403/issues/68
	public void test_takenNameValidAmount_shouldNotAllowIt() {
		String budgetName = "Duplicate Budget Name";
		Budget b = new Budget(budgetName, 0, false, LocalDate.now(), Budget.Duration.WEEK);
		app.getBudgetList().add(b);

		solo.enterText(nameField, budgetName);
		solo.enterText(amountField, "1.00");

		solo.clickOnButton("Create");
		solo.sleep(500);

		String expectedError = "Budget name already in use";
		String foundError = (String) nameField.getError();

		assertNotNull("There was no error on the name field", foundError);
		assertEquals(expectedError, foundError);

		// There should be no error on amountField
		assertNull(amountField.getError());
	}

	public void test_clear_shouldResetAllFields() {
		solo.enterText(nameField, "Clearing Budget");
		solo.enterText(amountField, "12345.00");

		// Since we only have one checkbox, this clicks it to set it.
		solo.clickOnCheckBox(0);

		// Similarly, since we only have one spinner, this sets its selected index.
		solo.pressSpinnerItem(0, 3);

		solo.clickOnButton("Clear");
		solo.sleep(500);

		String nameText = nameField.getText().toString();
		String amountText = amountField.getText().toString();
		boolean recurChecked = solo.isCheckBoxChecked(0);
		boolean correctDuration = solo.isSpinnerTextSelected("Day");

		assertEquals("Name field was not empty after clearing", "", nameText);
		assertEquals("Amount field was not empty after clearing", "", amountText);
		assertFalse("Recur CheckBox was checked after clearing", recurChecked);
		assertTrue("Spinner text after clear should be 'Day'", correctDuration);
	}

}
