package com.example.budgetmanager.ui.test;
import android.app.Activity;
import android.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.budgetmanager.AddBudgetActivity;
import com.example.budgetmanager.AddEntryActivity;
import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Budget.Duration;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.MainActivity;
import com.example.budgetmanager.R;
import com.example.budgetmanager.api.ApiInterface;
import com.example.budgetmanager.api.test.AsyncHttpClientStub;
import com.example.budgetmanager.test.TestUtilities;
import com.jayway.android.robotium.solo.Solo;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tests that the MainActivity initializes views used by the EntryLogsTab, and
 * that the "Add Budget" and "Add Entry" buttons start or do not start
 * AddBudgetActivity and AddEntryActivity as expected.
 *
 * Also tests that the Edit and Delete options for entries in the log work
 * as expected. Checks that Edit takes users to the edit screen with information
 * correctly autofilled, and that Delete removes entries from the log correctly,
 * on the client side.
 *
 * @author James PushaKi
 */
public class TestCaseMainActivity
extends ActivityInstrumentationTestCase2<MainActivity> {

	private final String TEST_BUDGET_NAME_1 = "Test Budget 1";
	private final String TEST_ENTRY_NAME_1 = "Test Entry 1, Budget 1";
	private final String TEST_ENTRY_NAME_2 = "Test Entry 2, Budget 1";

	Activity mainActivity;
	Fragment entryLogsTab;

	private Solo solo;
	private ListView listView;
	private Spinner sortSpinner;

	@SuppressWarnings("unused")
	private ApiInterface api;
	private AsyncHttpClientStub testClient;

	public TestCaseMainActivity() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());

		// Get the Activity
		mainActivity = getActivity();

		// Get the Fragment
		String entryLogsTabString = getActivity().getResources().getString(
				R.string.title_fragment_entry_logs);
		entryLogsTab = getActivity().getFragmentManager().findFragmentByTag(
				entryLogsTabString);

		listView = (ListView) getActivity().findViewById(
				com.example.budgetmanager.R.id.entry_list);
		sortSpinner = (Spinner) getActivity().findViewById(
				com.example.budgetmanager.R.id.spinner_logs_sort);

		// Should tear down budgets
		Budget.clearBudgets();

		testClient = new AsyncHttpClientStub();
		api = TestUtilities.getStubbedApiInterface(testClient);
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	@MediumTest
	public void test_activityAndFragment_notNull() {
		// Test that the Activity and Fragment are found
		assertNotNull(mainActivity);
		assertNotNull(entryLogsTab);
	}

	@MediumTest
	public void test_onCreate_viewsNotNull() {
		// Ensure all of the views are present
		assertNotNull(listView);
		assertNotNull(sortSpinner);
		assertNotNull(solo.getButton("Add Budget"));
		assertNotNull(solo.getButton("Add Entry"));
	}

	@MediumTest
	public void test_onCreate_checkSpinnerListenerAndAdapter() {
		// Ensure that the "Sort" spinner has a listener and adapter
		assertNotNull(sortSpinner.getOnItemSelectedListener());
		assertNotNull(sortSpinner.getAdapter());
	}

	@MediumTest
	public void test_onAddBudgetClicked_startsActivityCorrectly() {
		// Check that the "Add Budget" button starts the AddBudgetActivity
		solo.clickOnButton("Add Budget");
		solo.sleep(500);

		boolean addBudgetActivityStarted =
				solo.waitForActivity(AddBudgetActivity.class, 2000);
		// Activity should be started
		assertTrue(addBudgetActivityStarted);
	}

	@MediumTest
	public void test_onAddEntryClicked_doesNotStartActivityIfNoCreatedBudgets() {
		// Check that AddEntryActivity is NOT started when the user presses
		// the "Add Entry" button if there are no created budgets
		Budget.clearBudgets();

		solo.clickOnButton("Add Entry");
		solo.sleep(500);

		boolean addEntryActivityStarted =
				solo.waitForActivity(AddEntryActivity.class, 2000);
		// Activity should not be started
		assertFalse(addEntryActivityStarted);
	}

	@MediumTest
	public void test_entryLongClickEdit_startsActivityCorrectly() {
		// Tests that the "Edit" option, when long-clicking on an entry, will
		// take the user to the AddEntryActivity in "Edit" mode, and that the
		// selected entry's information has been autofilled into the input
		// fields

		createTestEntriesAndRefresh();
		solo.sleep(500);

		// Long click on the first test entry and press the first option that
		// appears, which is the option for "Edit"
		solo.clickLongOnTextAndPress(TEST_ENTRY_NAME_1, 0);

		// Check that the AddEntryActivity was started
		solo.assertCurrentActivity("AddEntryActivity was not started",
				AddEntryActivity.class);

		// Make sure that the activity was started in "Edit" mode, and not in
		// "Add" mode
		assertTrue(solo.searchText("Edit"));
		assertFalse(solo.searchText("Add"));

		// Check that the selected entry's information has been autofilled into
		// the edit window's fields
		assertTrue(solo.searchText(TEST_ENTRY_NAME_1));
		assertTrue(solo.searchText("May"));
		assertTrue(solo.searchText("31"));
		assertTrue(solo.searchText("2011"));
	}

	@MediumTest
	public void test_entryLongClickDelete_deletesCorrectly() throws JSONException {
		// Tests that the "Delete" option, when long-clicking on an entry, will
		// remove the entry from the list of available entries, and that it will
		// not delete any other entries

		createTestEntriesAndRefresh();
		solo.sleep(500);

		// Use the stubbed HTTP client to set up a result from the server
		// without ever hitting the network.
		// Set the "response" that the delete operation was successful on the
		// server
		testClient.setNextResponse(new JSONObject().put("destroyed", true), true);

		// Long click on the first test entry and press the first option that
		// appears, which is the option for "Edit"
		solo.clickLongOnTextAndPress(TEST_ENTRY_NAME_1, 1);

		// The user should be taken back to the same screen
		solo.assertCurrentActivity("The activity was changed unexpectedly",
				MainActivity.class);

		// Check that the entry can no longer be found in the list, and that
		// the other test budget has not been inadvertently deleted
		assertFalse(solo.searchText(TEST_ENTRY_NAME_1));
		assertTrue(solo.searchText(TEST_ENTRY_NAME_2));
	}

	/**
	 * Creates one test budget with two test entries and refreshes the
	 * ListView of entries
	 */
	private void createTestEntriesAndRefresh() {
		// Some dates for the test budgets and test entries
		LocalDate date1 = new LocalDate(2011, 5, 31);
		LocalDate date2 = new LocalDate(2012, 3, 20);

		// Make a test budget
		Budget testBudget1 = new Budget(TEST_BUDGET_NAME_1, 300, false,
				date1, Duration.WEEK);

		// Make two test entries
		Entry testEntry1_budget1 = new Entry(500, testBudget1,
				TEST_ENTRY_NAME_1, date1);
		Entry testEntry2_budget1 = new Entry(1000, testBudget1,
				TEST_ENTRY_NAME_2, date2);

		// And put the test entries into the budgets
		testBudget1.addEntry(testEntry1_budget1);
		testBudget1.addEntry(testEntry2_budget1);

		// And call onResume(), which will refresh the list of entries
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				entryLogsTab.onResume();
			}
		});
	}
}
