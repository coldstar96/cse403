package com.example.budgetmanager.api.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Budget.Duration;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.api.ApiInterface;
import com.example.budgetmanager.test.TestUtilities;
import com.loopj.android.http.RequestParams;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

/**
 * This is a whitebox test of the ApiInterface. It tests the internals
 * of network response callbacks using a special mocked-out HTTP client.
 *
 * @author Chris brucec5
 */
public class TestApiInterfaceMock extends AndroidTestCase {
	ApiInterface api;
	AsyncHttpClientMock testClient;

	/**
	 * Sets the API Interface's client field to be our
	 * special test HTTP client, or else testing would take
	 * a long time, having many HTTP requests to a remote
	 * server.
	 */
	protected void setUp() throws Exception {
		super.setUp();

		Budget.clearBudgets();
		try {
			// Need to set the context for the test, or we'll get a
			// NullPointerException.
			TestUtilities.setStaticValue("com.example.budgetmanager.UBudgetApp", "context", getContext());
		} catch (Exception e) { }

		// Set up the mocked test client
		testClient = new AsyncHttpClientMock();
		api = TestUtilities.getStubbedApiInterface(testClient);
	}

	/**
	 * Tests to make sure that create(Budget) creates the correct params
	 * and passes them to the AsyncHttpClient
	 */
	@SmallTest
	public void test_create_budget_requestParamsAreGood() {
		LocalDate startDate = new LocalDate(2013, 05, 05);
		String startDateString = startDate.toString(
				DateTimeFormat.forPattern(api.getDateFormat()));
		final Budget b = new Budget("Budget", 5000, false, startDate, Duration.WEEK);

		// Set up the expected request params
		RequestParams params = new RequestParams();
		params.put("budget_name", b.getName());
		params.put("amount", "" + b.getBudgetAmount());
		params.put("recur", "" + b.isRecurring());
		params.put("start_date", startDateString);
		params.put("recurrence_duration", b.getDuration().toString());

		testClient.expect(params);

		api.create(b, null);
	}

	/**
	 * Tests to make sure that create(Entry) creates the correct params
	 * and passes them to the AsyncHttpClient
	 */
	@SmallTest
	public void test_create_entry_requestParamsAreGood() {
		LocalDate startDate = new LocalDate(2013, 05, 05);
		String startDateString = startDate.toString(
				DateTimeFormat.forPattern(api.getDateFormat()));
		final Budget b = new Budget("Budget", 5000, false, startDate, Duration.WEEK);
		b.setId(12345);

		final Entry e = new Entry(100, b, "Test Entry", startDate);

		// Set up the expected request params
		RequestParams params = new RequestParams();
		params.put("amount", "" + e.getAmount());
		params.put("notes", "" + e.getNotes());
		params.put("expenditure_date", startDateString);
		params.put("budget_id", "" + b.getId());

		testClient.expect(params);

		api.create(e, null);
	}

	/**
	 * Tests to make sure that update(Budget) creates the correct params
	 * and passes them to the AsyncHttpClient
	 */
	@SmallTest
	public void test_update_budget_requestParamsAreGood() {
		LocalDate startDate = new LocalDate(2013, 05, 05);
		String startDateString = startDate.toString(
				DateTimeFormat.forPattern(api.getDateFormat()));
		final Budget b = new Budget("Budget", 5000, false, startDate, Duration.WEEK);
		b.setId(12345);

		// Set up the expected request params
		RequestParams params = new RequestParams();
		params.put("budget_name", b.getName());
		params.put("amount", "" + b.getBudgetAmount());
		params.put("recur", "" + b.isRecurring());
		params.put("start_date", startDateString);
		params.put("recurrence_duration", b.getDuration().toString());
		params.put("id", "" + b.getId());

		testClient.expect(params);

		api.update(b, null);
	}

	/**
	 * Tests to make sure that create(Entry) creates the correct params
	 * and passes them to the AsyncHttpClient
	 */
	@SmallTest
	public void test_update_entry_requestParamsAreGood() {
		LocalDate startDate = new LocalDate(2013, 05, 05);
		String startDateString = startDate.toString(
				DateTimeFormat.forPattern(api.getDateFormat()));
		final Budget b = new Budget("Budget", 5000, false, startDate, Duration.WEEK);
		b.setId(12345);

		final Entry e = new Entry(100, b, "Test Entry", startDate);
		e.setEntryId(23456);

		// Set up the expected request params
		RequestParams params = new RequestParams();
		params.put("amount", "" + e.getAmount());
		params.put("notes", "" + e.getNotes());
		params.put("expenditure_date", startDateString);
		params.put("budget_id", "" + b.getId());
		params.put("id", "" + e.getEntryId());

		testClient.expect(params);

		api.update(e, null);
	}

	/**
	 * Tests to make sure createUser creates the correct params
	 * and passes them to the AsyncHttpClient
	 */
	@SmallTest
	public void test_createUser_requestParamsAreGood() {
		String username = "test@gmail.com";
		String password = "testpassword";

		RequestParams params = new RequestParams();
		params.put("username", username);
		params.put("password", password);

		testClient.expect(params);

		api.createUser(username, password, null);
	}

	@SmallTest
	public void test_logIn_requestParamsAreGood() {
		String username = "test@gmail.com";
		String password = "testpassword";

		RequestParams params = new RequestParams();
		params.put("username", username);
		params.put("password", password);

		testClient.expect(params);

		api.logIn(username, password, null);
	}
}
