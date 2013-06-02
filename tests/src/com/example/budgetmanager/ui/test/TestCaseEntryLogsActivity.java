package com.example.budgetmanager.ui.test;

import java.util.ArrayList;

import org.joda.time.LocalDate;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.budgetmanager.AddBudgetActivity;
import com.example.budgetmanager.AddEntryActivity;
import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Budget.Duration;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.EntryLogsActivity;
import com.jayway.android.robotium.solo.Solo;

/**
 * Tests that EntryLogsActivity initializes necessary views, starts activities
 * correctly upon button presses, and that the spinner correctly displays
 * entries based on the user-specified sort order.
 * 
 * @author James PushaKi
 */
public class TestCaseEntryLogsActivity
extends ActivityInstrumentationTestCase2<EntryLogsActivity> {

	private final String TEST_BUDGET_NAME_1 = "Test Budget 1";
	private final String TEST_BUDGET_NAME_2 = "Test Budget 2";
	private final String TEST_BUDGET_NAME_3 = "Test Budget 3";
	private final String TEST_ENTRY_NAME_1 = "Test Entry 1, Budget 1";
	private final String TEST_ENTRY_NAME_2 = "Test Entry 2, Budget 1";
	private final String TEST_ENTRY_NAME_3 = "Test Entry 3, Budget 2";
	private final String TEST_ENTRY_NAME_4 = "Test Entry 4, Budget 2";
	private final String TEST_ENTRY_NAME_5 = "Test Entry 5, Budget 3";
	private final String TEST_ENTRY_NAME_6 = "Test Entry 6, Budget 3";

	private Solo solo;
	private ListView listView;
	private Spinner sortSpinner;

	public TestCaseEntryLogsActivity() {
		super(EntryLogsActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		// Set references to Views, and add some test budgets
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());
		listView = (ListView) getActivity().findViewById(
				com.example.budgetmanager.R.id.entry_list);
		sortSpinner = (Spinner) getActivity().findViewById(
				com.example.budgetmanager.R.id.spinner_logs_sort);

		// Should always tear down budgets
		Budget.clearBudgets();
	}

	/**
	 * Creates some test budgets with some test entries and refreshes the
	 * ListView of entries
	 */
	private void createTestEntriesAndRefresh() {
		// Some dates for the test budgets and test entries
		LocalDate date1 = new LocalDate(2011, 5, 31);
		LocalDate date2 = new LocalDate(2012, 3, 20);
		LocalDate date3 = new LocalDate(2012, 9, 1);
		LocalDate date4 = new LocalDate(2013, 4, 5);
		LocalDate date5 = new LocalDate(2013, 5, 31);
		LocalDate date6 = new LocalDate(2013, 6, 1);

		// Test budget 1
		Budget testBudget1 = new Budget(TEST_BUDGET_NAME_1, 300, false,
				date1, Duration.WEEK);

		// Test budget 2
		Budget testBudget2 = new Budget(TEST_BUDGET_NAME_2, 200, true,
				date2, Duration.MONTH);

		// Test budget 3
		Budget testBudget3 = new Budget(TEST_BUDGET_NAME_3, 100, false,
				date3, Duration.YEAR);

		// Make some test entries
		Entry testEntry1_budget1 = new Entry(500, testBudget1,
				TEST_ENTRY_NAME_1, date1);
		Entry testEntry2_budget1 = new Entry(1000, testBudget1,
				TEST_ENTRY_NAME_2, date2);
		Entry testEntry3_budget2 = new Entry(5000, testBudget2,
				TEST_ENTRY_NAME_3, date3);
		Entry testEntry4_budget2 = new Entry(3000, testBudget2,
				TEST_ENTRY_NAME_4, date4);
		Entry testEntry5_budget3 = new Entry(4000, testBudget3,
				TEST_ENTRY_NAME_5, date5);
		Entry testEntry6_budget3 = new Entry(12000, testBudget3,
				TEST_ENTRY_NAME_6, date6);

		// And put the test entries into the budgets
		testBudget1.addEntry(testEntry1_budget1);
		testBudget1.addEntry(testEntry2_budget1);
		testBudget2.addEntry(testEntry3_budget2);
		testBudget2.addEntry(testEntry4_budget2);
		testBudget3.addEntry(testEntry5_budget3);
		testBudget3.addEntry(testEntry6_budget3);

		// And call onResume(), which will refresh the list of entries
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getActivity().onResume();
			}
		});
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
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

	//	@MediumTest
	//	public void test_onAddEntryClicked_startsActivityCorrectly() {
	//		// Check that the "Add Entry" button starts the AddEntryActivity
	//		// correctly when there is at least one created budget
	//
	//		// Create a budget, because AddEntryActivity requires that there is
	//		// at least one created budget before a user can create an entry
	//		LocalDate date1 = new LocalDate(2011, 5, 31);
	//		@SuppressWarnings("unused")
	//		Budget testBudget1 = new Budget(TEST_BUDGET_NAME_1, 300, false,
	//				date1, Duration.WEEK);
	//
	//		solo.clickOnButton("Add Entry");
	//		solo.sleep(500);
	//
	//		boolean addEntryActivityStarted =
	//				solo.waitForActivity(AddEntryActivity.class, 2000);
	//		// Activity should be started
	//		assertTrue(addEntryActivityStarted);
	//	}

	@MediumTest
	public void test_sortOrderCorrect_sortByDate() {
		// Tests that entries are displayed in the correct order,
		// by most recent to least recent entry date

		// Expected order for "Sort By Date":
		//	TEST_ENTRY_NAME_6 (most recent)
		//	TEST_ENTRY_NAME_5
		//	TEST_ENTRY_NAME_4
		//	TEST_ENTRY_NAME_3
		//	TEST_ENTRY_NAME_2
		//	TEST_ENTRY_NAME_1 (least recent)

		createTestEntriesAndRefresh();

		// Press the option for "Sort By Date" in the spinner
		solo.pressSpinnerItem(0, 0);
		solo.sleep(500);

		// And check the order of entries in the re-sorted ListView

		// For each entry, get its TextViews, and check that there is a TextView
		// with the expected entry note
		ArrayList<TextView> textViews = solo.clickInList(1);
		assertTrue(textViewHasText(textViews, TEST_ENTRY_NAME_6));

		textViews = solo.clickInList(2);
		assertTrue(textViewHasText(textViews, TEST_ENTRY_NAME_5));

		textViews = solo.clickInList(3);
		assertTrue(textViewHasText(textViews, TEST_ENTRY_NAME_4));

		textViews = solo.clickInList(4);
		assertTrue(textViewHasText(textViews, TEST_ENTRY_NAME_3));

		// Only checks the first four items in the ListView because Robotium
		// seems to dislike clicking items past the bottom of the screen,
		// and scrolling down resets the index of items differently for
		// different screen sizes
	}

	@MediumTest
	public void test_sortOrderCorrect_sortByAmount() {
		// Tests that entries are displayed in the correct order,
		// by greatest amount to least amount

		// Expected order for "Sort By Amount":
		//  TEST_ENTRY_NAME_6 (greatest amount)
		//  TEST_ENTRY_NAME_3
		//  TEST_ENTRY_NAME_5
		//  TEST_ENTRY_NAME_4
		//  TEST_ENTRY_NAME_2
		//  TEST_ENTRY_NAME_1 (least amount)

		createTestEntriesAndRefresh();

		// Press the option for "Sort By Amount" in the spinner
		solo.pressSpinnerItem(0, 1);
		solo.sleep(500);

		// And check the order of entries in the re-sorted ListView

		// For each entry, get its TextViews, and check that there is a TextView
		// with the expected entry note
		ArrayList<TextView> textViews = solo.clickInList(1);
		assertTrue(textViewHasText(textViews, TEST_ENTRY_NAME_6));

		textViews = solo.clickInList(2);
		assertTrue(textViewHasText(textViews, TEST_ENTRY_NAME_3));

		textViews = solo.clickInList(3);
		assertTrue(textViewHasText(textViews, TEST_ENTRY_NAME_5));

		textViews = solo.clickInList(4);
		assertTrue(textViewHasText(textViews, TEST_ENTRY_NAME_4));
	}

	@MediumTest
	public void test_sortOrderCorrect_sortByBudget() {
		// Test that entries are displayed in the correct order,
		// by budget name, alphabetically

		// "Sort By Budget" expected order:
		// 		TEST_BUDGET_NAME_1
		// 		TEST_BUDGET_NAME_1
		// 		TEST_BUDGET_NAME_2
		// 		TEST_BUDGET_NAME_2
		// 		TEST_BUDGET_NAME_3
		// 		TEST_BUDGET_NAME_3

		createTestEntriesAndRefresh();

		// Press the option for "Sort By Budget" in the spinner
		solo.pressSpinnerItem(0, 2);
		solo.sleep(500);

		// And check the order of entries in the re-sorted ListView

		// For each entry, get its TextViews, and check that there is a TextView
		// with the expected budget name
		ArrayList<TextView> textViews = solo.clickInList(1);
		assertTrue(textViewHasText(textViews, TEST_BUDGET_NAME_1));

		textViews = solo.clickInList(2);
		assertTrue(textViewHasText(textViews, TEST_BUDGET_NAME_1));

		textViews = solo.clickInList(3);
		assertTrue(textViewHasText(textViews, TEST_BUDGET_NAME_2));

		textViews = solo.clickInList(4);
		assertTrue(textViewHasText(textViews, TEST_BUDGET_NAME_2));
	}

	@MediumTest
	public void test_sortOrderCorrect_byCreationTime() {
		// Test that entries are displayed in the correct order,
		// by entry creation time, from most recent to least recent

		// Expected order for "Sort By Creation time":
		//  TEST_ENTRY_NAME_6 (most recently created)
		//  TEST_ENTRY_NAME_5
		//  TEST_ENTRY_NAME_4
		//  TEST_ENTRY_NAME_3
		//  TEST_ENTRY_NAME_2
		//  TEST_ENTRY_NAME_1 (least recently created)

		createTestEntriesAndRefresh();

		// Press the option for "Sort By Creation time" in the spinner
		solo.pressSpinnerItem(0, 3);
		solo.sleep(500);

		// And check the order of entries in the re-sorted ListView

		// For each entry, get its TextViews, and check that there is a TextView
		// with the expected entry name
		ArrayList<TextView> textViews = solo.clickInList(1);
		assertTrue(textViewHasText(textViews, TEST_ENTRY_NAME_6));

		textViews = solo.clickInList(2);
		assertTrue(textViewHasText(textViews, TEST_ENTRY_NAME_5));

		textViews = solo.clickInList(3);
		assertTrue(textViewHasText(textViews, TEST_ENTRY_NAME_4));

		textViews = solo.clickInList(4);
		assertTrue(textViewHasText(textViews, TEST_ENTRY_NAME_3));
	}

	/**
	 * Returns whether the ArrayList of TextViews has a TextView whose text
	 * matches <code>text</code>
	 */
	private boolean textViewHasText(ArrayList<TextView> textViews, String text) {
		// Used because solo.clickInList(int pos) seems to sometimes return a
		// list of TextViews in an unexpected order
		for (TextView tv : textViews) {
			if (tv.getText().toString().equals(text)) {
				return true;
			}
		}
		return false;
	}
}
