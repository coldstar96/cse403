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
import com.example.budgetmanager.MainActivity;
import com.example.budgetmanager.R;
import com.jayway.android.robotium.solo.Solo;

/**
 * Tests that the MainActivity initializes views used by the EntryLogsTab, and
 * that the "Add Budget" and "Add Entry" buttons start or do not start
 * AddBudgetActivity and AddEntryActivity as expected.
 * 
 * @author James PushaKi
 */
public class TestCaseMainActivity
extends ActivityInstrumentationTestCase2<MainActivity> {

	Activity mainActivity;
	Fragment entryLogsTab;

	private Solo solo;
	private ListView listView;
	private Spinner sortSpinner;

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
}
