package com.example.budgetmanager.ui.test;

import org.joda.time.LocalDate;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.text.format.Time;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.example.budgetmanager.AddBudgetActivity;
import com.example.budgetmanager.AddEntryActivity;
import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Budget.Duration;
import com.jayway.android.robotium.solo.Solo;

/**
 * Tests that AddEntryActivity correctly initializes necessary views, validates
 * user input and throws appropriate errors, that the spinner correctly displays
 * the list of budgets, and that user interface controls for starting an
 * activity to add a new budget and for clearing input fields work correctly.
 * 
 * @author James PushaKi
 */
public class TestCaseAddEntryActivity
extends ActivityInstrumentationTestCase2<AddEntryActivity> {

	private static final String TEST_BUDGET_NAME = "Test budget";
	private static final String EXPECTED_ERROR_MESSAGE_INVALID_AMOUNT =
			"Please specify amount";
	private static final String EXPECTED_ERROR_MESSAGE_ZERO_AMOUNT =
			"Amount must be greater than $0";

	private Solo solo;
	private EditText amountView;
	private Spinner budgetView;
	private DatePicker dateView;
	private EditText notesView;
	private Button addButtonView;
	private SpinnerAdapter budgetSpinnerAdapter;

	public TestCaseAddEntryActivity() {
		super(AddEntryActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		// Set references to Views, and add a budget item
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());

		amountView = (EditText) getActivity().findViewById(
				com.example.budgetmanager.R.id.entry_amount);
		budgetView = (Spinner) getActivity().findViewById(
				com.example.budgetmanager.R.id.spinner_budget);
		dateView = (DatePicker) getActivity().findViewById(
				com.example.budgetmanager.R.id.entry_date_picker);
		notesView = (EditText) getActivity().findViewById(
				com.example.budgetmanager.R.id.entry_notes);
		addButtonView = (Button) getActivity().findViewById(
				com.example.budgetmanager.R.id.add_entry_button);
		budgetSpinnerAdapter = budgetView.getAdapter();

		// Should always tear down budgets
		Budget.clearBudgets();

		@SuppressWarnings("unused")
		Budget testBudget = new Budget(TEST_BUDGET_NAME, 200, false,
				LocalDate.now(), Duration.MONTH);

		// Have to call addItemsToBudgetSpinner() manually to get the spinner
		// to refresh and show the newly created budget, testBudget.
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getActivity().addItemsToBudgetSpinner();
			}
		});
		solo.sleep(500);
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	@MediumTest
	public void test_onCreate_viewsNotNull() {
		// Ensure all of the views are present
		assertNotNull(amountView);
		assertNotNull(budgetView);
		assertNotNull(dateView);
		assertNotNull(notesView);
		assertNotNull(addButtonView);
	}

	@MediumTest
	public void test_addItemsToBudgetSpinner_checkSpinnerListenerAndAdapter() {
		// Checks that the spinner containing budget selections has a listener,
		// and that the spinner's adapter is not null
		assertTrue(budgetView.getOnItemSelectedListener() != null);
		assertTrue(budgetSpinnerAdapter != null);
	}

	@MediumTest
	public void test_addItemsToBudgetSpinner_addsBudgetToSpinner() {
		// Checks that addItemsToBudgetSpinner() correctly adds budgets to the
		// spinner, and that they are able to be selected

		solo.pressSpinnerItem(0, 0);
		assertTrue(solo.isSpinnerTextSelected(TEST_BUDGET_NAME));
	}

	@MediumTest
	public void test_addItemsToBudgetSpinner_addNewBudgetOptionStartsActivityCorrectly() {
		// Tests that when a user clicks the spinner option to create a new
		// budget that AddBudgetActivity is started

		solo.pressSpinnerItem(0, 1);
		solo.sleep(500);

		boolean addBudgetActivityStarted =
				solo.waitForActivity(AddBudgetActivity.class, 5000);
		assertTrue(addBudgetActivityStarted);
	}

	@MediumTest
	public void test_emptyAmount_shouldNotAllowIt() {
		// An empty amount in the amount EditText should cause an error to be
		// thrown

		// Don't touch the "Amount" EditText, leaving it empty

		// Select a valid budget from the spinner
		solo.pressSpinnerItem(0, 0);
		assertTrue(solo.isSpinnerTextSelected(TEST_BUDGET_NAME));

		// Select a valid date in the DatePicker
		solo.setDatePicker(0, 2013, 5, 31);

		// Enter a valid note
		solo.enterText(notesView, "This is a test entry");

		solo.clickOnButton("Add");

		// Check that the expected error message is thrown
		String foundError = (String) amountView.getError();
		assertNotNull("There was no error on the amount field", foundError);
		assertEquals(EXPECTED_ERROR_MESSAGE_INVALID_AMOUNT, foundError);
	}

	@MediumTest
	public void test_zeroAmount_shouldNotAllowIt() {
		// A zero amount in the amount EditText should cause an error to be
		// thrown

		// Set the amount EditText to $0
		solo.enterText(amountView, "0");

		// Select a valid budget from the spinner
		solo.pressSpinnerItem(0, 0);
		assertTrue(solo.isSpinnerTextSelected(TEST_BUDGET_NAME));

		// Select a valid date in the DatePicker
		solo.setDatePicker(0, 2013, 5, 31);

		// Enter a valid note
		solo.enterText(notesView, "This is a test entry");

		solo.clickOnButton("Add");

		// Check that the expected error message is thrown
		String foundError = (String) amountView.getError();
		assertNotNull("There was no error on the amount field", foundError);
		assertEquals(EXPECTED_ERROR_MESSAGE_ZERO_AMOUNT, foundError);
	}

	@MediumTest
	public void test_clearEntry_clearsFieldsCorrectly() {
		// Tests that when the user clicks the "Clear" button, that there is
		// no error on the amount field, that the DatePicker has been reset,
		// and that all other fields have been cleared

		// Set input fields to some valid values, before pressing "Clear"
		solo.enterText(amountView, "220");
		solo.pressSpinnerItem(0, 0);
		assertTrue(solo.isSpinnerTextSelected(TEST_BUDGET_NAME));
		solo.setDatePicker(0, 2013, 5, 31);
		solo.enterText(notesView, "This is a test entry");

		solo.clickOnButton("Clear");

		// There should be no error on the amount field after pressing "Clear"
		String foundError = (String) amountView.getError();
		assertNull("There was an unexpected error on the amount field",
				foundError);

		// Check that the first entry in the spinner is selected
		assertTrue(solo.isSpinnerTextSelected(TEST_BUDGET_NAME));

		// Get the DatePicker's values,
		int year = dateView.getYear();
		int month = dateView.getMonth();
		int day = dateView.getDayOfMonth();

		// And get the expected date values by getting the current date,
		Time now = new Time();
		now.setToNow();

		// And check that they match.
		assertEquals("Year was not reset correctly", now.year, year);
		assertEquals("Month was not reset correctly", now.month, month);
		assertEquals("Day was not reset correctly", now.monthDay, day);

		// Check that the amount and note EditTexts were cleared
		assertEquals("The EditText for the amount was not cleared",
				"", amountView.getText().toString());
		assertEquals("The EditText for the note was not cleared",
				"", notesView.getText().toString());
	}
}
