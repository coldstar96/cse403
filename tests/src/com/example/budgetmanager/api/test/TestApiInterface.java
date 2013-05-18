package com.example.budgetmanager.api.test;

import java.lang.reflect.Field;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Budget.Duration;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.api.ApiCallback;
import com.example.budgetmanager.api.ApiInterface;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

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
		try {
			// Need to set the context for the test.
			setStaticValue("com.example.budgetmanager.UBudgetApp", "context", getContext());
		} catch (Exception e) { }
		
		api = ApiInterface.getInstance();
		testClient = new TestAsyncHttpClient();
		
		try {
			// API's client field is private. Lets use
			// reflection to change it to our test client.
			setInstanceValue(api, "client", testClient);
		} catch (Exception e) { }
	}
	
	/**
	 * Tests the creation of a budget from the server response data.
	 */
	@SmallTest
	public void test_create_budget() throws JSONException {
		final Budget b = new Budget("Budget", 5000, false, LocalDate.now(), Duration.WEEK);
		final long NEW_ID = 100;
		testClient.setNextResponse(new JSONObject()
				.put("id", NEW_ID)
		);
		assertEquals(Budget.NEW_ID, b.getId());
		api.create(b, new ApiCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				assertEquals(NEW_ID, b.getId());
			}

			@Override
			public void onFailure(String errorMessage) {
				fail("Should not fail, JSON string was valid.");
			}
		});
	}
	
	/**
	 * Tests the failure of creation of a budget from the server response data.
	 */
	@SmallTest
	public void test_create_budget_no_id() throws JSONException {
		final Budget b = new Budget("Budget", 5000, false, LocalDate.now(), Duration.WEEK);
		testClient.setNextResponse(new JSONObject()
				.put("not-id", "gibberish")
		);
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
	public void test_create_entry() throws JSONException {
		final Budget b = new Budget("Budget", 5000, false, LocalDate.now(), Duration.WEEK);
		final Entry e = new Entry(100, b, "notes", 
				LocalDate.parse("2013-05-16", DateTimeFormat.forPattern("yyyy-MM-dd")));
		final long NEW_ID = 100;
		testClient.setNextResponse(new JSONObject()
				.put("id", NEW_ID)
		);
		assertEquals(Budget.NEW_ID, b.getId());
		api.create(e, new ApiCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				assertEquals(NEW_ID, e.getEntryId());
			}

			@Override
			public void onFailure(String errorMessage) {
				fail("Should not fail, JSON string was valid.");
			}
		});
	}
	
	/**
	 * Tests the failure of creation of an entry from the server response data.
	 */
	@SmallTest
	public void test_create_entry_no_id() throws JSONException {
		final Budget b = new Budget("Budget", 5000, false, LocalDate.now(), Duration.WEEK);
		final Entry e = new Entry(100, b, "notes", 
				LocalDate.parse("2013-05-16", DateTimeFormat.forPattern("yyyy-MM-dd")));
		testClient.setNextResponse(new JSONObject()
				.put("not-id", "gibberish")
		);
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
	 */
	@SmallTest
	public void test_fetch_budgets() throws JSONException {
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
	 */
	@SmallTest
	public void test_fetch_budgets_invalid_response() throws JSONException {
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
	 */
	@SmallTest
	public void test_fetch_entries() throws JSONException {
		final int NUM_ENTRIES = 10;
		final long START_ID = 1;
		final Budget b = new Budget("Budget", 5000, false, LocalDate.now(), Duration.WEEK);
		b.setId(100);
		
		JSONArray jsonEntries = new JSONArray();
		for (int i = 0; i < NUM_ENTRIES; i++) {
			jsonEntries.put(new JSONObject()
					.put("id", START_ID + i)
					.put("amount", 1000 * (i + 1))
					.put("expenditure_date", "2013-11-" + (14 + i))
					.put("notes", "Note " + i)
			);
		}
		
		api.fetchEntries(b, new ApiCallback<List<Entry>>() {

			@Override
			public void onSuccess(List<Entry> result) {
				assertEquals(NUM_ENTRIES, result.size());
				for (int i = 0; i < NUM_ENTRIES; i++) {
					Entry e = result.get(i);
					assertEquals(START_ID + i, e.getEntryId());
					assertEquals(1000 * (i + 1), e.getAmount());
					assertEquals(LocalTime.parse("2013-11-" + (14 + i), 
							DateTimeFormat.forPattern("yyyy-MM-dd")), e.getDate());
					assertEquals("Note " + i, e.getNotes());
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
	 */
	@SmallTest
	public void test_fetch_entries_invalid_response() throws JSONException {
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
	 */
	@SmallTest
	public void test_fetch_budgets_entries() throws JSONException {
		final int NUM_BUDGETS_ENTRIES = 10;
		
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
						assertEquals(LocalTime.parse("2013-11-" + (14 + j), 
								DateTimeFormat.forPattern("yyyy-MM-dd")), e.getDate());
						assertEquals("Note " + j, e.getNotes());
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
	 */
	@SmallTest
	public void test_fetch_budgets_entries_invalid_response() throws JSONException {
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
     * Use reflection to change value of any instance field.
     * 
     * @param classInstance An Object instance.
     * @param fieldName The name of a field in the class instantiated by classInstancee
     * @param newValue The value you want the field to be set to.
     */
    private static void setInstanceValue(final Object classInstance, final String fieldName, final Object newValue) throws SecurityException,
            NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        // Get the private field
        final Field field = classInstance.getClass().getDeclaredField(fieldName);
        // Allow modification on the field
        field.setAccessible(true);
        // Sets the field to the new value for this instance
        field.set(classInstance, newValue);
    }
    
    /**
     * Use reflection to change value of any static field.
     * @param className The complete name of the class (ex. java.lang.String)
     * @param fieldName The name of a static field in the class
     * @param newValue The value you want the field to be set to.
     * @throws SecurityException .
     * @throws NoSuchFieldException .
     * @throws ClassNotFoundException .
     * @throws IllegalArgumentException .
     * @throws IllegalAccessException .
     */
    public static void setStaticValue(final String className, final String fieldName, final Object newValue) throws SecurityException, NoSuchFieldException,
            ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        // Get the private String field
        final Field field = Class.forName(className).getDeclaredField(fieldName);
        // Allow modification on the field
        field.setAccessible(true);
        // Get
        final Object oldValue = field.get(Class.forName(className));
        // Sets the field to the new value
        field.set(oldValue, newValue);
    }
}
