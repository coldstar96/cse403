package com.example.budgetmanager.api.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Budget.Duration;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;
import com.example.budgetmanager.test.TestUtilities;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * This is a whitebox test of the ApiInterface. It tests the internals
 * of network response callbacks using a special stubbed-out HTTP client.
 *
 * @author Graham grahamb5
 */
public class TestApiInterface extends AndroidTestCase {
	private ApiInterface api;
	private TestAsyncHttpClient testClient;

	/**
	 * Sets the API Interface's client field to be our
	 * special test HTTP client, or else testing would take
	 * a long time, having many HTTP requests to a remote
	 * server.
	 */
	protected void setUp() {
		Budget.clearBudgets();
		try {
			// Need to set the context for the test, or we'll get a
			// NullPointerException.
			TestUtilities.setStaticValue("com.example.budgetmanager.UBudgetApp", "context", getContext());
		} catch (Exception e) { }

		testClient = new TestAsyncHttpClient();
		api = TestUtilities.getStubbedApiInterface(testClient);
	}

	/**
	 * Tests the creation of a budget from the server response data.
	 * White-box test.
	 */
	@SmallTest
	public void test_create_newBudget_shouldSetId() throws JSONException {
		final Budget b = new Budget("Budget", 5000, false, LocalDate.now(), Duration.WEEK);
		final long BUDGET_ID = 100;

		// Set next "response" from the server.
		testClient.setNextResponse(new JSONObject()
				.put("id", BUDGET_ID), true);

		assertEquals(Budget.NEW_ID, b.getId());
		api.create(b, new ApiCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				assertEquals(BUDGET_ID, b.getId());
			}

			@Override
			public void onFailure(String errorMessage) {
				fail("Should not fail, JSON string was valid.");
			}
		});
	}

	/**
	 * Tests the failure of creation of a budget from the server response data.
	 * White-box test.
	 */
	@SmallTest
	public void test_create_newBudget_responseHasNoId_shouldReturnFailure() throws JSONException {
		final Budget b = new Budget("Budget", 5000, false, LocalDate.now(), Duration.WEEK);
		// Set next "response" from the server.
		testClient.setNextResponse(new JSONObject()
				.put("not-id", "gibberish"), true);
		api.create(b, new ApiCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				fail("Receiving a id-less response should fail.");
			}

			@Override
			public void onFailure(String errorMessage) {
				assertNotNull(errorMessage);
			}
		});
	}

	/**
	 * Tests the creation of an entry from the server response data.
	 */
	@SmallTest
	public void test_create_newEntry_shouldSetId() throws JSONException {
		final Budget b = new Budget("Budget", 5000, false, LocalDate.now(), Duration.WEEK);
		final Entry e = new Entry(100, b, "notes",
				LocalDate.parse("2013-05-16", DateTimeFormat.forPattern("yyyy-MM-dd")));
		final long ENTRY_ID = 100;
		// Set next "response" from the server.
		testClient.setNextResponse(new JSONObject()
				.put("id", ENTRY_ID)
				.put("created_at", "2013-11-14 01:00:00")
				.put("updated_at", "2013-11-14 01:30:00"), true);
		assertEquals(Budget.NEW_ID, b.getId());
		api.create(e, new ApiCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				assertEquals(ENTRY_ID, e.getEntryId());
			}

			@Override
			public void onFailure(String errorMessage) {
				fail("Should not fail, JSON string was valid.");
			}
		});
	}

	/**
	 * Tests the failure of creation of an entry from the server response data.
	 * White-box test.
	 */
	@SmallTest
	public void test_create_newEntry_responseHasMissingFields_shouldFail() throws JSONException {
		final Budget b = new Budget("Budget", 5000, false, LocalDate.now(), Duration.WEEK);
		final Entry e = new Entry(100, b, "notes",
				LocalDate.parse("2013-11-14", DateTimeFormat.forPattern("yyyy-MM-dd")));
		// Set next "response" from the server.
		testClient.setNextResponse(new JSONObject()
				.put("not-id", "gibberish"), true);
		api.create(e, new ApiCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				fail("Receiving a id-less response should fail.");
			}

			@Override
			public void onFailure(String errorMessage) {
				assertNotNull(errorMessage);
			}
		});
	}

	/**
	 * Tests the fetching of a user's budgets from the server response data.
	 * White-box test.
	 */
	@SmallTest
	public void test_fetchBudgets_allValid_shouldSucceed() throws JSONException {
		final int NUM_BUDGETS = 10;
		final long START_ID = 1;

		JSONArray jsonBudgets = new JSONArray();
		for (int i = 0; i < NUM_BUDGETS; i++) {
			jsonBudgets.put(new JSONObject()
					.put("budget_name", "Budget " + i)
					.put("recurrence_duration", Duration.DAY.toString())
					.put("amount", 1000 * (i + 1))
					.put("recur", true)
					.put("start_date", "1991-11-" + (14 + i))
					.put("id", START_ID + i)
			);
		}

		// Set next "response" from the server.
		testClient.setNextResponse(jsonBudgets, true);

		api.fetchBudgets(new ApiCallback<List<Budget>>() {

			@Override
			public void onSuccess(List<Budget> result) {
				assertEquals(NUM_BUDGETS, result.size());
				for (int i = 0; i < NUM_BUDGETS; i++) {
					Budget b = result.get(i);
					assertEquals("Budget " + i, b.getName());
					assertEquals(Duration.DAY, b.getDuration());
					assertEquals(1000 * (i + 1), b.getBudgetAmount());
					assertTrue(b.isRecurring());
					assertEquals(LocalDate.parse("1991-11-" + (14 + i),
							DateTimeFormat.forPattern("yyyy-MM-dd")), b.getStartDate());
					assertEquals(START_ID + i, b.getId());
				}
			}

			@Override
			public void onFailure(String errorMessage) {
				fail("Shouldn't fail, results are valid.");
			}

		});
	}

	/**
	 * Tests the failure of fetching of a user's budgets from the server response data.
	 * White-box test.
	 */
	@SmallTest
	public void test_fetchBudgets_invalidResponse_shouldFail() throws JSONException {
		final int NUM_BUDGETS = 10;
		final long START_ID = 1;

		JSONArray jsonBudgets = new JSONArray();
		for (int i = 0; i < NUM_BUDGETS; i++) {
			jsonBudgets.put(new JSONObject()
					.put("name", "Budget " + i) // INVALID, should be budget_name
					.put("recurrence_duration", Duration.DAY.toString())
					.put("amount", 1000 * (i + 1))
					.put("recur", true)
					.put("start_date", "1991-11-" + (14 + i))
					.put("id", START_ID + i)
			);
		}

		// Set next "response" from the server.
		testClient.setNextResponse(jsonBudgets, true);

		api.fetchBudgets(new ApiCallback<List<Budget>>() {
			@Override
			public void onSuccess(List<Budget> result) {
				fail("Shouldn't succeed, results are invalid.");
			}

			@Override
			public void onFailure(String errorMessage) {
				assertNotNull(errorMessage);
			}
		});
	}

	/**
	 * Tests the fetching of a user's specified budget's entries
	 * from the server response data.
	 * White-box test.
	 */
	@SmallTest
	public void test_fetchEntries_allValid_shouldSucceed() throws JSONException {
		final int NUM_ENTRIES = 10;
		final long START_ID = 1;
		final String CREATED_UPDATED = "2013-11-14 01:00:00";
		final Budget b = new Budget("Budget", 5000, false, LocalDate.now(), Duration.WEEK);
		b.setId(100);

		JSONArray jsonEntries = new JSONArray();
		for (int i = 0; i < NUM_ENTRIES; i++) {
			jsonEntries.put(new JSONObject()
					.put("id", START_ID + i)
					.put("amount", 1000 * (i + 1))
					.put("expenditure_date", "2013-11-" + (14 + i))
					.put("notes", "Note " + i)
					.put("created_at", CREATED_UPDATED)
					.put("updated_at", CREATED_UPDATED)
			);
		}

		// Set next "response" from the server.
		testClient.setNextResponse(jsonEntries, true);

		api.fetchEntries(b, new ApiCallback<List<Entry>>() {
			@Override
			public void onSuccess(List<Entry> result) {
				assertEquals(NUM_ENTRIES, result.size());
				for (int i = 0; i < NUM_ENTRIES; i++) {
					Entry e = result.get(i);
					assertEquals(START_ID + i, e.getEntryId());
					assertEquals(1000 * (i + 1), e.getAmount());
					assertEquals(LocalDate.parse("2013-11-" + (14 + i),
							DateTimeFormat.forPattern("yyyy-MM-dd")), e.getDate());
					assertEquals("Note " + i, e.getNotes());
					assertEquals(LocalDateTime.parse(CREATED_UPDATED,
							DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")), e.getCreatedAt());
					assertEquals(LocalDateTime.parse(CREATED_UPDATED,
							DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")), e.getUpdatedAt());
				}
			}

			@Override
			public void onFailure(String errorMessage) {
				fail("Shouldn't fail, results are valid.");
			}
		});
	}

	/**
	 * Tests the failure of fetching of a user's specified budget's entries
	 * from the server response data.
	 * White-box test.
	 */
	@SmallTest
	public void test_fetchEntries_invalidResponse_shouldFail() throws JSONException {
		final int NUM_BUDGETS = 10;
		final long START_ID = 1;
		final Budget b = new Budget("Budget", 5000, false, LocalDate.now(), Duration.WEEK);
		b.setId(100);

		JSONArray jsonEntries = new JSONArray();
		for (int i = 0; i < NUM_BUDGETS; i++) {
			jsonEntries.put(new JSONObject()
					.put("id", START_ID + i)
					.put("amount", 1000 * (i + 1))
					.put("date", "2013-11-" + (14 + i)) // INVALID, should be expenditure_date
					.put("notes", "Note " + i)
			);
		}

		// Set next "response" from the server.
		testClient.setNextResponse(jsonEntries, true);

		api.fetchEntries(b, new ApiCallback<List<Entry>>() {
			@Override
			public void onSuccess(List<Entry> result) {
				fail("Shouldn't succeed, results are invalid.");
			}

			@Override
			public void onFailure(String errorMessage) {
				assertNotNull(errorMessage);
			}
		});
	}

	/**
	 * Tests the fetching of a user's specified budget's, including entries,
	 * from the server response data.
	 * White-box test.
	 */
	@SmallTest
	public void test_fetchBudgetsAndEntries_allValid_shouldSucceed() throws JSONException {
		final int NUM_BUDGETS_ENTRIES = 10;
		final String CREATED_UPDATED = "2013-11-14 01:00:00";
		final long START_ID = 1;

		JSONArray jsonBudgets = new JSONArray();
		for (int i = 0; i < NUM_BUDGETS_ENTRIES; i++) {
			JSONArray jsonEntries = new JSONArray();
			for (int j = 0; j < NUM_BUDGETS_ENTRIES; j++) {
				jsonEntries.put(new JSONObject()
						.put("id", START_ID + j)
						.put("amount", 1000 * (j + 1))
						.put("expenditure_date", "2013-11-" + (14 + j))
						.put("notes", "Note " + j)
						.put("created_at", CREATED_UPDATED)
						.put("updated_at", CREATED_UPDATED)
				);
			}

			jsonBudgets.put(new JSONObject()
					.put("budget_name", "Budget " + i)
					.put("recurrence_duration", Duration.DAY.toString())
					.put("amount", 1000 * (i + 1))
					.put("recur", true)
					.put("start_date", "1991-11-" + (14 + i))
					.put("id", START_ID + i)
					.put("entries", jsonEntries)
			);
		}

		// Set next "response" from the server.
		testClient.setNextResponse(jsonBudgets, true);

		api.fetchBudgetsAndEntries(new ApiCallback<List<Budget>>() {
			@Override
			public void onSuccess(List<Budget> result) {
				assertEquals(NUM_BUDGETS_ENTRIES, result.size());
				for (int i = 0; i < NUM_BUDGETS_ENTRIES; i++) {
					Budget b = result.get(i);
					assertEquals("Budget " + i, b.getName());
					assertEquals(Duration.DAY, b.getDuration());
					assertEquals(1000 * (i + 1), b.getBudgetAmount());
					assertTrue(b.isRecurring());
					assertEquals(LocalDate.parse("1991-11-" + (14 + i),
							DateTimeFormat.forPattern("yyyy-MM-dd")), b.getStartDate());
					assertEquals(START_ID + i, b.getId());

					List<Entry> entries = b.getEntries();
					assertEquals(NUM_BUDGETS_ENTRIES, result.size());
					for (int j = 0; j < NUM_BUDGETS_ENTRIES; j++) {
						Entry e = entries.get(j);
						assertEquals(START_ID + j, e.getEntryId());
						assertEquals(1000 * (j + 1), e.getAmount());
						assertEquals(LocalDate.parse("2013-11-" + (14 + j),
								DateTimeFormat.forPattern("yyyy-MM-dd")), e.getDate());
						assertEquals("Note " + j, e.getNotes());
						assertEquals(LocalDateTime.parse(CREATED_UPDATED,
								DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")), e.getCreatedAt());
						assertEquals(LocalDateTime.parse(CREATED_UPDATED,
								DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")), e.getUpdatedAt());
					}
				}
			}

			@Override
			public void onFailure(String errorMessage) {
				fail("Shouldn't fail, results are valid.");
			}
		});
	}

	/**
	 * Tests the failure of fetching of a user's specified budget's, including entries,
	 * from the server response data.
	 * White-box test.
	 */
	@SmallTest
	public void test_fetchBudgetsAndEntries_invalidResponse_shouldFail() throws JSONException {
		final int NUM_BUDGETS = 10;
		final long START_ID = 1;

		JSONArray jsonBudgets = new JSONArray();
		for (int i = 0; i < NUM_BUDGETS; i++) {
			jsonBudgets.put(new JSONObject()
					.put("name", "Budget " + i) // INVALID, should be budget_name
					.put("recurrence_duration", Duration.DAY.toString())
					.put("amount", 1000 * (i + 1))
					.put("recur", true)
					.put("start_date", "1991-11-" + (14 + i))
					.put("id", START_ID + i)
					.put("entries", new JSONArray())
			);
		}

		// Set next "response" from the server.
		testClient.setNextResponse(jsonBudgets, true);

		api.fetchBudgetsAndEntries(new ApiCallback<List<Budget>>() {
			@Override
			public void onSuccess(List<Budget> result) {
				fail("Shouldn't succeed, results are invalid.");
			}

			@Override
			public void onFailure(String errorMessage) {
				assertNotNull(errorMessage);
			}
		});
	}

	/**
	 * Tests the login failure handling functionality.
	 * White-box test.
	 */
	@SmallTest
	public void test_logIn_invalidUsernameOrPassword_shouldPassErrorBack() throws JSONException {
		final String USERNAME = "test@test.com";
		final String PASSWORD = "password";
		final String USERNAME_ERROR = USERNAME + ":";
		final String PASSWORD_ERROR = "Invalid username or password.";

		JSONObject obj = new JSONObject().put("username", new JSONArray().put(USERNAME_ERROR))
				.put("password_digest", new JSONArray().put(PASSWORD_ERROR));

		// Set next "response" from the server.
		testClient.setNextResponse(obj, false);

		api.logIn(USERNAME, PASSWORD, new ApiCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				fail("Should fail, username/password were invalid.");
			}

			@Override
			public void onFailure(String errorMessage) {
				assertEquals(USERNAME_ERROR + " " + PASSWORD_ERROR, errorMessage);
			}
		});
	}

	/**
	 * Tests the create user failure handling functionality
	 * White-box test.
	 */
	@SmallTest
	public void test_createUser_usernameTaken_shouldPassErrorBack() throws JSONException {
		final String USERNAME = "test@test.com";
		final String PASSWORD = "password";
		final String USERNAME_ERROR = "Username already taken.";

		JSONObject obj = new JSONObject().put("username", new JSONArray().put(USERNAME_ERROR));

		// Set next "response" from the server.
		testClient.setNextResponse(obj, false);

		api.createUser(USERNAME, PASSWORD, new ApiCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				fail("Should fail, username was already taken.");
			}

			@Override
			public void onFailure(String errorMessage) {
				assertEquals(USERNAME_ERROR, errorMessage);
			}
		});
	}

	/**
	 * Tests the Entry updating success functionality.
	 * The updatedAt of the Entry should be updated.
	 * White-box test.
	 */
	@SmallTest
	public void test_update_entry_shouldUpdateUpdatedAt() throws JSONException {
		Budget budget = new Budget("Test Budget", 200, true, LocalDate.now(), Duration.WEEK);
		final Entry entry = new Entry(1, 100, budget, "Test Entry", LocalDate.now());

		String expectedUpdatedAtStr = "2013-11-14 01:00:00";
		final LocalDateTime expectedUpdatedAt = LocalDateTime.parse(
				expectedUpdatedAtStr, DateTimeFormat.forPattern(api.DATETIME_FORMAT));
		JSONObject obj = new JSONObject().put("updated_at", expectedUpdatedAtStr);

		// Set next "response" from the "server"
		testClient.setNextResponse(obj, true);

		api.update(entry, new ApiCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				assertEquals("The updatedAt was not set properly by the API",
						expectedUpdatedAt, entry.getUpdatedAt());
			}

			@Override
			public void onFailure(String errorMessage) {
				fail("Should have called the onFailure callback");
			}
		});
	}

	/**
	 * Tests the Entry updating failure functionality (server sends missing data).
	 * The updatedAt of the Entry should remain the same.
	 * White-box test.
	 */
	@SmallTest
	public void test_update_entry_invalidResponse_shouldFail() throws JSONException {
		Budget budget = new Budget("Test Budget", 200, true, LocalDate.now(), Duration.WEEK);
		final Entry entry = new Entry(1, 100, budget, "Test Entry", LocalDate.now());

		JSONObject obj = new JSONObject().put("updatedat", "");

		// Set next "response" from the "server"
		testClient.setNextResponse(obj, true);

		api.update(entry, new ApiCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				fail("This request should have not succeeded (should be a JSON failure)");
			}

			@Override
			public void onFailure(String errorMessage) {
				assertNotNull(errorMessage);
			}
		});
	}

	/**
	 * Tests the Budget updating success functionality.
	 * White-box test.
	 */
	@SmallTest
	public void test_update_budget_allGoesWell_shouldSucceed() throws JSONException {
		Budget budget = new Budget("Test Budget", 200, true, LocalDate.now(), Duration.WEEK);
		budget.setId(1);

		JSONObject obj = new JSONObject().put("success", "true");

		// Set next "response" from the "server"
		testClient.setNextResponse(obj, true);

		api.update(budget, new ApiCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				// The result should be null.
				assertNull(result);
			}

			@Override
			public void onFailure(String errorMessage) {
				fail("This request should have succeeded");
			}
		});
	}

	/**
	 * Tests the Budget updating failure functionality.
	 * White-box test.
	 * @throws JSONException
	 */
	@SmallTest
	public void test_update_budget_serverError_shouldFail() throws JSONException {
		Budget budget = new Budget("Test Budget", 200, true, LocalDate.now(), Duration.WEEK);
		budget.setId(1);

		JSONObject obj = new JSONObject().put("success", "false");

		// Set next "response" from the "server"
		testClient.setNextResponse(obj, false);

		api.update(budget, new ApiCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				fail("This request should have failed");
			}

			@Override
			public void onFailure(String errorMessage) {
				assertNotNull(errorMessage);
			}
		});
	}

	/**
	 * Tests the Entry removal success functionality.
	 * White-box test.
	 */
	@SmallTest
	public void test_remove_entry_successfullyDeleted_shouldSucceed() throws JSONException {
		Budget budget = new Budget("Test Budget", 200, true, LocalDate.now(), Duration.WEEK);
		final Entry entry = new Entry(1, 100, budget, "Test Entry", LocalDate.now());

		JSONObject obj = new JSONObject().put("destroyed", true);

		// Set next "response" from the "server"
		testClient.setNextResponse(obj, true);

		api.remove(entry, new ApiCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				assertNull(result);
			}

			@Override
			public void onFailure(String errorMessage) {
				fail("This request should have succeeded");
			}
		});
	}

	/**
	 * Tests the Entry removal failure functionality.
	 * White-box test.
	 */
	@SmallTest
	public void test_remove_entry_notProperlyDeleted_shouldFail() throws JSONException {
		Budget budget = new Budget("Test Budget", 200, true, LocalDate.now(), Duration.WEEK);
		final Entry entry = new Entry(1, 100, budget, "Test Entry", LocalDate.now());

		JSONObject obj = new JSONObject().put("destroyed", false);

		// Set next "response" from the "server"
		testClient.setNextResponse(obj, true);

		api.remove(entry, new ApiCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				fail("This request should have failed (bad JSON )");
			}

			@Override
			public void onFailure(String errorMessage) {
				assertNotNull(errorMessage);
			}
		});
	}

	/**
	 * Tests the Budget removal success functionality.
	 * White-box test.
	 */
	@SmallTest
	public void test_remove_budget_successfullyDeleted_shouldSucceed() throws JSONException {
		Budget budget = new Budget("Test Budget", 200, true, LocalDate.now(), Duration.WEEK);
		budget.setId(1);

		JSONObject obj = new JSONObject().put("destroyed", true);

		// Set next "response" from the "server"
		testClient.setNextResponse(obj, true);

		api.remove(budget, new ApiCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				// The result should be null.
				assertNull(result);
			}

			@Override
			public void onFailure(String errorMessage) {
				fail("This request should have succeeded");
			}
		});
	}

	/**
	 * Tests the Budget removal failure functionality.
	 * White-box test.
	 * @throws JSONException
	 */
	@SmallTest
	public void test_remove_budget_notProperlyDeleted_shouldFail() throws JSONException {
		Budget budget = new Budget("Test Budget", 200, true, LocalDate.now(), Duration.WEEK);
		budget.setId(1);

		JSONObject obj = new JSONObject().put("destroyed", false);

		// Set next "response" from the "server"
		testClient.setNextResponse(obj, true);

		api.remove(budget, new ApiCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				fail("This request should not have succeeded (bad JSON)");
			}

			@Override
			public void onFailure(String errorMessage) {
				assertNotNull(errorMessage);
			}
		});
	}
}
