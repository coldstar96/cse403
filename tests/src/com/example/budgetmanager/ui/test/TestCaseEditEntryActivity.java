package com.example.budgetmanager.ui.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.EditText;

import com.example.budgetmanager.AddEntryActivity;
import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Budget.Duration;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.api.test.AsyncHttpClientStub;
import com.example.budgetmanager.test.TestUtilities;
import com.jayway.android.robotium.solo.Solo;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.json.JSONException;
import org.json.JSONObject;

public class TestCaseEditEntryActivity
extends ActivityInstrumentationTestCase2<AddEntryActivity> {

	private Solo solo;
	private EditText amountView;
	private EditText notesView;

	private AsyncHttpClientStub testClient;

	public TestCaseEditEntryActivity() {
		super(AddEntryActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setUpBudgetList();

		Intent intent = new Intent();
		intent.putExtra("Add", false);
		intent.putExtra("BudgetId", TEST_BUDGET_ID);
		intent.putExtra("EntryId", TEST_ENTRY_ID);
		setActivityIntent(intent);

		solo = new Solo(getInstrumentation(), getActivity());

		// Get all of the UI elements to interact with, avoiding the use of
		// finding by text because Jenkins really doesn't like that.
		solo = new Solo(getInstrumentation(), getActivity());

		amountView = (EditText) getActivity().findViewById(
				com.example.budgetmanager.R.id.entry_amount);
		notesView = (EditText) getActivity().findViewById(
				com.example.budgetmanager.R.id.entry_notes);

		// Have to call addItemsToBudgetSpinner() manually to get the spinner
		// to refresh and show the newly created budget, testBudget.
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getActivity().addItemsToBudgetSpinner();
			}
		});
		solo.sleep(500);

		// Set up the stubbed test client
		testClient = new AsyncHttpClientStub();
		TestUtilities.getStubbedApiInterface(testClient);
	}

	private static final String TEST_BUDGET_NAME = "Test Budget";
	private static final String TEST_ENTRY_NAME = "Test Entry";
	private static final long TEST_BUDGET_ID = -2;
	private static final long TEST_ENTRY_ID = -3;
	private static final int TEST_ENTRY_AMOUNT = 10000;
	private static final LocalDateTime TEST_ENTRY_UPDATE_TIME =
			new LocalDateTime(2013, 11, 14, 0, 0);
	private static final LocalDateTime TEST_ENTRY_CREATE_TIME =
			new LocalDateTime(1991, 11, 14, 0, 0);

	private static Entry TEST_ENTRY;

	private void setUpBudgetList() {
		Budget.clearBudgets();
		Budget testBudget = new Budget(TEST_BUDGET_NAME, 1234500,
				false, LocalDate.now(), Duration.DAY);
		testBudget.setId(TEST_BUDGET_ID);
		Entry testEntry = new Entry(TEST_ENTRY_ID, TEST_ENTRY_AMOUNT,
				testBudget, TEST_ENTRY_NAME, LocalDate.now());
		testEntry.setUpdatedAt(TEST_ENTRY_UPDATE_TIME);
		testEntry.setCreatedAt(TEST_ENTRY_CREATE_TIME);
		testBudget.addEntry(testEntry);

		TEST_ENTRY = testEntry;
	}

	/**
	 * Ensure that, on server failure, the edited entry
	 * remains unchanged.
	 * 
	 * This is a black-box test of the AddEntryActivity.
	 */
	@MediumTest
	public void test_editValidEntry_apiError() {
		String ENTRY_NAME = "Edited Entry - API Error";
		String ENTRY_AMOUNT = "125.00";

		// Update some fields.
		solo.clearEditText(amountView);
		solo.enterText(amountView, ENTRY_AMOUNT);
		solo.clearEditText(notesView);
		solo.enterText(notesView, ENTRY_NAME);
		solo.sleep(1000);

		// Set the test client to send back an API failure.
		testClient.setNextResponse(new JSONObject(), false);

		solo.clickOnButton("Submit");
		solo.sleep(1000);

		// Check that the entry remains unchanged.
		assertEquals("Entry's update time should be the same.",
				TEST_ENTRY_UPDATE_TIME, TEST_ENTRY.getUpdatedAt());
		assertEquals("Entry's name should not have changed.",
				TEST_ENTRY_NAME, TEST_ENTRY.getNotes());
		assertEquals("Entry's amount should not have changed.",
				TEST_ENTRY_AMOUNT, TEST_ENTRY.getAmount());
	}

	/**
	 * Ensure that, on server success, the edited entry
	 * exhibits the changes made.
	 * 
	 * This is a black-box test of the AddEntryActivity.
	 */
	@MediumTest
	public void test_editValidEntry_newEntryIsAdded() throws JSONException {
		String ENTRY_NAME = "Edited Entry - API Success";
		String ENTRY_AMOUNT = "125.00";
		LocalDateTime ENTRY_UPDATE_TIME = TEST_ENTRY_UPDATE_TIME.plusDays(1);

		// Update some fields.
		solo.clearEditText(amountView);
		solo.enterText(amountView, ENTRY_AMOUNT);
		solo.clearEditText(notesView);
		solo.enterText(notesView, ENTRY_NAME);
		solo.sleep(1000);

		// Set the test client to send back an API Success.
		JSONObject response = new JSONObject();
		response.put("updated_at", ENTRY_UPDATE_TIME.toString("yyyy-MM-dd HH:mm:ss"));
		testClient.setNextResponse(response, true);

		solo.clickOnButton("Submit");
		solo.sleep(1000);

		// Check that the entry updated with the right values.
		assertEquals("Entry's update time should be updated.",
				ENTRY_UPDATE_TIME, TEST_ENTRY.getUpdatedAt());
		assertEquals("Entry's name should have changed.",
				ENTRY_NAME, TEST_ENTRY.getNotes());
		assertEquals("Entry's amount shouldhave changed.",
				(int) (Double.parseDouble(ENTRY_AMOUNT) * 100), TEST_ENTRY.getAmount());
	}

}
