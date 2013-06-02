package com.example.budgetmanager.ui.test;

import org.joda.time.LocalDate;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.budgetmanager.AddBudgetActivity;
import com.example.budgetmanager.AddEntryActivity;
import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Budget.Duration;
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

		// "Sort By Budget" expected order:
		// 		testBudget1, testBudget2, testBudget3

		// Test budget 1
		LocalDate date1 = new LocalDate(2013, 5, 31);
		@SuppressWarnings("unused")
		Budget testBudget1 = new Budget(TEST_BUDGET_NAME_1, 300, false,
				date1, Duration.WEEK);

		// Test budget 2
		LocalDate date2 = new LocalDate(2013, 3, 20);
		@SuppressWarnings("unused")
		Budget testBudget2 = new Budget(TEST_BUDGET_NAME_2, 200, true,
				date2, Duration.MONTH);

		// Test budget 3
		LocalDate date3 = new LocalDate(2012, 9, 1);
		@SuppressWarnings("unused")
		Budget testBudget3 = new Budget(TEST_BUDGET_NAME_3, 100, false,
				date3, Duration.YEAR);
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

	@MediumTest
	public void test_onAddEntryClicked_startsActivityCorrectly() {
		// Check that the "Add Entry" button starts the AddEntryActivity
		// correctly when there is at least one created budget
		solo.clickOnButton("Add Entry");
		solo.sleep(500);

		boolean addEntryActivityStarted =
				solo.waitForActivity(AddEntryActivity.class, 2000);
		// Activity should be started
		assertTrue(addEntryActivityStarted);
	}

	@MediumTest
	public void test_sortOrderCorrect_sortByDate() {

	}

	@MediumTest
	public void test_sortOrderCorrect_sortByAmount() {

	}

	@MediumTest
	public void test_sortOrderCorrect_sortByBudget() {
		// "Sort By Budget" expected order:
		// 		testBudget1, testBudget2, testBudget3
	}

	@MediumTest
	public void test_sortOrderCorrect_byCreationTime() {

	}

	@MediumTest
	public void test_sortOrderCorrect_byUpdateTime() {

	}
}
