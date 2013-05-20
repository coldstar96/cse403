package com.example.budgetmanager.test;

import org.joda.time.LocalDate;

import com.example.budgetmanager.AddBudgetActivity;
import com.example.budgetmanager.Budget;
import com.example.budgetmanager.UBudgetApp;
import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.test.FlakyTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class TestCaseAddBudgetActivity
	extends ActivityInstrumentationTestCase2<AddBudgetActivity> {

	private Solo solo;
	private EditText nameField;
	private EditText amountField;
	private Button createButton;
	private Button clearButton;
	private CheckBox recurCheckBox;

	private UBudgetApp app;

	public TestCaseAddBudgetActivity() {
		super(AddBudgetActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());

		nameField = (EditText) getActivity().findViewById(com.example.budgetmanager.R.id.budget_name);
		amountField = (EditText) getActivity().findViewById(com.example.budgetmanager.R.id.budget_amount);
		createButton = (Button) getActivity().findViewById(com.example.budgetmanager.R.id.create_budget_button);
		clearButton = (Button) getActivity().findViewById(com.example.budgetmanager.R.id.clear_budget_button);
		recurCheckBox = (CheckBox) getActivity().findViewById(com.example.budgetmanager.R.id.budget_recur);

		app = (UBudgetApp) getActivity().getApplication();
		app.getBudgetList().clear();
	}

	@MediumTest
	@FlakyTest(tolerance=3)
	public void test_zeroAmountDollarsValidName_shouldNotAllowIt() {
		solo.enterText(nameField, "Budget with 0 Amount");
		solo.enterText(amountField, "0");

		solo.clickOnView(createButton);
		solo.sleep(500);

		String expectedError = "Amount must be greater than $0";
		String foundError = (String) amountField.getError();

		assertNotNull("There was no error on the amount field", foundError);
		assertEquals(expectedError, foundError);

		// There should be no error on nameField
		assertNull(nameField.getError());
	}

	@MediumTest
	@FlakyTest(tolerance=3)
	public void test_zeroAmountDollarsAndCentsValidName_shouldNotAllowIt() {
		solo.enterText(nameField, "Budget with 0.00 Amount");
		solo.enterText(amountField, "0.00");

		solo.clickOnView(createButton);
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
	@MediumTest
	@FlakyTest(tolerance=3)
	public void test_emptyAmountValidName_shouldNotAllowIt() {
		solo.enterText(nameField, "Empty Amount Budget");

		solo.clickOnView(createButton);
		solo.sleep(500);

		String expectedError = "Please specify amount";
		String foundError = (String) amountField.getError();

		assertNotNull("There was no error on the amount field", foundError);
		assertEquals(expectedError, foundError);

		// There should be no error on nameField
		assertNull(nameField.getError());
	}

	@MediumTest
	@FlakyTest(tolerance=3)
	public void test_emptyNameValidAmount_shouldNotAllowIt() {
		solo.enterText(amountField, "1.00");

		solo.clickOnView(createButton);
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
	@MediumTest
	@FlakyTest(tolerance=3)
	public void test_takenNameValidAmount_shouldNotAllowIt() {
		String budgetName = "Duplicate Budget Name";
		Budget b = new Budget(budgetName, 0, false, LocalDate.now(), Budget.Duration.WEEK);
		app.getBudgetList().add(b);

		solo.enterText(nameField, budgetName);
		solo.enterText(amountField, "1.00");

		solo.clickOnView(createButton);
		solo.sleep(500);

		String expectedError = "Budget name already in use";
		String foundError = (String) nameField.getError();

		assertNotNull("There was no error on the name field", foundError);
		assertEquals(expectedError, foundError);

		// There should be no error on amountField
		assertNull(amountField.getError());
	}

	@MediumTest
	@FlakyTest(tolerance=3)
	public void test_clear_shouldResetFields() {
		solo.enterText(nameField, "Clearing Budget");
		solo.enterText(amountField, "12345.00");
		solo.clickOnView(recurCheckBox, true);

		solo.clickOnView(clearButton);
		solo.sleep(800);

		String nameText = nameField.getText().toString();
		String amountText = amountField.getText().toString();
		boolean recurChecked = recurCheckBox.isChecked();

		assertEquals("Name field was not empty after clearing", "", nameText);
		assertEquals("Amount field was not empty after clearing", "", amountText);
		assertFalse("Recur CheckBox was checked after clearing", recurChecked);
	}

}
