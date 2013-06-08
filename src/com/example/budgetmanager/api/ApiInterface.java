package com.example.budgetmanager.api;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Budget.Duration;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.R;
import com.example.budgetmanager.UBudgetApp;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class that facilitates connections to the HTTP API.
 *
 * @author Chris brucec5
 * @author Graham grahamb5
 *
 */
public class ApiInterface {

	// The singleton instance of ApiInterface.
	private static ApiInterface instance;
	private static final String TAG = "ApiInterface";

	private final String baseUrl;
	private final String usersUrl;
	private final String sessionUrl;
	private final String budgetsUrl;
	private final String entriesUrl;
	private final String budgetsAndEntriesUrl;

	private final String DATE_FORMAT;

	private final String DATETIME_FORMAT;

	private final AsyncHttpClient client;
	private final PersistentCookieStore cookieStore;

	/**
	 * Singleton factory method to get the singleton instance.
	 *
	 * @return singleton ApiInterface instance
	 */
	public static ApiInterface getInstance() {
		Log.d(TAG, "Getting instance of api interface");
		if (instance == null) {
			Log.d(TAG, "Creating new instance of api interface");
			instance = new ApiInterface(UBudgetApp.getAppContext());
		}
		return instance;
	}

	private ApiInterface(Context context) {
		Resources r = context.getResources();
		baseUrl = r.getString(R.string.base_url);
		usersUrl = baseUrl + r.getString(R.string.users);
		sessionUrl = baseUrl + r.getString(R.string.session);
		budgetsUrl = baseUrl + r.getString(R.string.budgets);
		entriesUrl = baseUrl + r.getString(R.string.entries);
		budgetsAndEntriesUrl = baseUrl + r.getString(R.string.budgets_and_entries);

		DATE_FORMAT = r.getString(R.string.api_date_format);
		DATETIME_FORMAT = r.getString(R.string.api_datetime_format);

		cookieStore = new PersistentCookieStore(context);
		client = new AsyncHttpClient();
		client.setTimeout(10000);
		client.setCookieStore(cookieStore);

		// Need to specify that we want JSON back from the server.
		client.addHeader("Accept", "application/json");
	}

	/**
	 * The format used to transfer dates between the client and server
	 * @return A string holding the date format used by the server
	 */
	public String getDateFormat() {
		return DATE_FORMAT;
	}

	/**
	 * The format used to transfer date-times between the client and server
	 * @return A string holding the date-time format used by the server
	 */
	public String getDateTimeFormat() {
		return DATETIME_FORMAT;
	}

	/**
	 * Logs the user out of the app.
	 */
	public void logOut() {
		// clears the cookies in the storage.
		cookieStore.clear();
	}

	/**
	 * Creates a budget on the API server. Asynchronous.
	 *
	 * @param b Budget instance to send to the server.
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is a {@link Long} that represents
	 * the ID of the Budget on the server.
	 */
	public void create(final Budget b, final ApiCallback<Long> callback) {
		if (failOnNoInternet(callback)) {
			return;
		}

		RequestParams params = new RequestParams();

		String startDate = b.getStartDate().toString(DATE_FORMAT);

		params.put("budget_name", b.getName());
		params.put("amount", "" + b.getBudgetAmount());
		params.put("recur", "" + b.isRecurring());
		params.put("start_date", startDate);
		params.put("recurrence_duration", b.getDuration().toString());

		client.post(budgetsUrl, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject obj) {
				try {
					long id = obj.getLong("id");
					b.setId(id);
					callback.onSuccess(id);
				} catch (JSONException e) {
					// This will catch if the server doesn't send an ID
					// But it's designed to always send an ID.
					Log.e(TAG, e.getMessage());
					callback.onFailure(e.getMessage());
				}
			}

			@Override
			public void onFailure(Throwable t, JSONObject obj) {
				callback.onFailure(t.getMessage());
			}

			@Override
			public void onFailure(Throwable t, String message) {
				if (t instanceof SocketTimeoutException) {
					callback.onFailure("Network Timeout");
				} else {
					callback.onFailure(message);
				}
			}
		});
	}

	/**
	 * Creates an entry on the API server. Asynchronous.
	 *
	 * @param e Entry instance to send to the server.
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is a {@link Long} that represents the
	 * ID of the Entry on the server.
	 */
	public void create(final Entry e, final ApiCallback<Long> callback) {
		if (failOnNoInternet(callback)) {
			return;
		}

		RequestParams params = new RequestParams();
		params.put("amount", "" + e.getAmount());
		params.put("notes", e.getNotes());
		params.put("expenditure_date", e.getDate().toString(DATE_FORMAT));
		params.put("budget_id", "" + e.getBudget().getId());

		client.post(entriesUrl, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject obj) {
				try {
					long id = obj.getLong("id");
					LocalDateTime createdAt = LocalDateTime.parse(obj.getString("created_at"),
							DateTimeFormat.forPattern(DATETIME_FORMAT));
					LocalDateTime updatedAt = LocalDateTime.parse(obj.getString("updated_at"),
							DateTimeFormat.forPattern(DATETIME_FORMAT));
					e.setEntryId(id);
					e.setCreatedAt(createdAt);
					e.setUpdatedAt(updatedAt);
					callback.onSuccess(id);
				} catch (JSONException e) {
					// This will catch if the server doesn't send an ID
					// But it's designed to always send an ID.
					Log.e(TAG, e.getMessage());
					callback.onFailure(e.getMessage());
				}
			}

			@Override
			public void onFailure(Throwable t, JSONObject obj) {
				callback.onFailure(t.getMessage());
			}

			@Override
			public void onFailure(Throwable t, String message) {
				if (t instanceof SocketTimeoutException) {
					callback.onFailure("Network Timeout");
				} else {
					callback.onFailure(message);
				}
			}
		});
	}

	/**
	 * Updates an already existing Budget on the API server. Asynchronous.
	 *
	 * @param b Budget instance to send to the server.
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is always <code>null</code>.
	 */
	public void update(Budget b, final ApiCallback<Object> callback) {
		if (failOnNoInternet(callback)) {
			return;
		}

		RequestParams params = new RequestParams();

		String startDate = b.getStartDate().toString(DATE_FORMAT);

		params.put("id", "" + b.getId());
		params.put("budget_name", b.getName());
		params.put("amount", "" + b.getBudgetAmount());
		params.put("recur", "" + b.isRecurring());
		params.put("start_date", startDate);
		params.put("recurrence_duration", b.getDuration().toString());

		client.put(budgetsUrl + "/" + b.getId(), params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject obj) {
				callback.onSuccess(null);
			}

			@Override
			public void onFailure(Throwable t, JSONObject obj) {
				callback.onFailure(t.getMessage());
			}

			@Override
			public void onFailure(Throwable t, String message) {
				if (t instanceof SocketTimeoutException) {
					callback.onFailure("Network Timeout");
				} else {
					callback.onFailure(message);
				}
			}
		});
	}

	/**
	 * Updates an already existing Entry on the API server. Asynchronous.
	 * If successful, it will update the Entry's updatedAt time.
	 *
	 * @param e Entry instance to send to the server.
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is always <code>null</code>.
	 */
	public void update(final Entry e, final ApiCallback<Object> callback) {
		if (failOnNoInternet(callback)) {
			return;
		}

		RequestParams params = new RequestParams();
		params.put("id", "" + e.getEntryId());
		params.put("amount", "" + e.getAmount());
		params.put("notes", e.getNotes());
		params.put("expenditure_date", e.getDate().toString(DATE_FORMAT));
		params.put("budget_id", "" + e.getBudget().getId());

		client.put(entriesUrl + "/" + e.getEntryId(), params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject obj) {
				try {
					// Get the server-generated "updated-at" time.
					LocalDateTime updatedAt = LocalDateTime.parse(
							obj.getString("updated_at"),
							DateTimeFormat.forPattern(DATETIME_FORMAT));
					e.setUpdatedAt(updatedAt);
					callback.onSuccess(null);
				} catch (JSONException e) {
					callback.onFailure(e.getMessage());
				}
			}

			@Override
			public void onFailure(Throwable t, JSONObject obj) {
				callback.onFailure(t.getMessage());
			}

			@Override
			public void onFailure(Throwable t, String message) {
				if (t instanceof SocketTimeoutException) {
					callback.onFailure("Network Timeout");
				} else {
					callback.onFailure(message);
				}
			}
		});
	}

	/**
	 * Destroys an already existing Budget on the API server and all related
	 * Entries. Asynchronous.
	 *
	 * @param b Budget instance to destroy.
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is always <code>null</code>.
	 */
	public void remove(Budget b, final ApiCallback<Object> callback) {
		if (failOnNoInternet(callback)) {
			return;
		}

		client.delete(budgetsUrl + "/" + b.getId(), new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject obj) {
				try {
					if (obj.has("destroyed") && obj.getBoolean("destroyed")) {
						callback.onSuccess(null);
					} else {
						callback.onFailure("Error deleting budget.");
					}
				} catch (JSONException e) {
					// This will catch if the server doesn't send a destruction
					// verification, but it's designed to always send one.
					Log.e(TAG, e.getMessage());
					callback.onFailure(e.getMessage());
				}
			}

			@Override
			public void onFailure(Throwable t, JSONObject obj) {
				callback.onFailure(t.getMessage());
			}

			@Override
			public void onFailure(Throwable t, String message) {
				if (t instanceof SocketTimeoutException) {
					callback.onFailure("Network Timeout");
				} else {
					callback.onFailure(message);
				}
			}
		});
	}

	/**
	 * Destroys an already existing Entry on the API server. Asynchronous.
	 *
	 * @param e Entry instance to destroy.
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is always <code>null</code>.
	 */
	public void remove(Entry e, final ApiCallback<Object> callback) {
		if (failOnNoInternet(callback)) {
			return;
		}

		client.delete(entriesUrl + "/" + e.getEntryId(), new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject obj) {
				try {
					if (obj.has("destroyed") && obj.getBoolean("destroyed")) {
						callback.onSuccess(null);
					} else {
						callback.onFailure("Error deleting entry.");
					}
				} catch (JSONException e) {
					// This will catch if the server doesn't send a destruction
					// verification, but it's designed to always send one.
					Log.e(TAG, e.getMessage());
					callback.onFailure(e.getMessage());
				}
			}

			@Override
			public void onFailure(Throwable t, JSONObject obj) {
				callback.onFailure(t.getMessage());
			}

			@Override
			public void onFailure(Throwable t, String message) {
				if (t instanceof SocketTimeoutException) {
					callback.onFailure("Network Timeout");
				} else {
					callback.onFailure(message);
				}
			}
		});
	}

	/**
	 * Fetches a collection of Budgets owned by the current user.
	 *
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is a
	 * {@link java.util.List}&lt;{@link Budget}&gt;
	 * containing all Budgets for the current user.
	 */
	public void fetchBudgets(final ApiCallback<List<Budget>> callback) {
		if (failOnNoInternet(callback)) {
			return;
		}

		Log.d(TAG, "Fetching budgets");

		client.get(budgetsUrl, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray budgetsJson) {
				List<Budget> budgetList = new ArrayList<Budget>();

				int budgetsLen = budgetsJson.length();

				// Iterate through the JSON array and create new
				// budgets for each index.
				for (int i = 0; i < budgetsLen; ++i) {
					try {
						JSONObject budgetObject = budgetsJson.getJSONObject(i);

						String budgetName = budgetObject.getString("budget_name");
						String duration = budgetObject.getString("recurrence_duration");
						int amount = budgetObject.getInt("amount");
						boolean recur = budgetObject.optBoolean("recur");
						LocalDate startDate = LocalDate.parse(budgetObject.getString("start_date"),
								DateTimeFormat.forPattern(DATE_FORMAT));
						long id = budgetObject.getLong("id");

						Budget newBudget = new Budget(budgetName, amount,
								recur, startDate, Duration.valueOf(duration));
						newBudget.setId(id);

						budgetList.add(newBudget);
					} catch (JSONException e) {
						Log.e(TAG, e.getMessage());
						callback.onFailure(e.getMessage());
						return;
					}
				}

				callback.onSuccess(budgetList);
			}

			@Override
			public void onFailure(Throwable t, JSONObject obj) {
				String status = obj.optString("status", "Service Error");
				callback.onFailure(status);
			}

			@Override
			public void onFailure(Throwable t, String message) {
				if (t instanceof SocketTimeoutException) {
					callback.onFailure("Network Timeout");
				} else {
					callback.onFailure(message);
				}
			}
		});
	}

	/**
	 * Fetches a collection of Entries associated with a given Budget.
	 *
	 * @param b Budget to fetch Entries from.
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is a
	 * {@link java.util.List}&lt;{@link Entry}&gt;
	 * containing all Entries for the given Budget.
	 */
	public void fetchEntries(final Budget b, final ApiCallback<List<Entry>> callback) {
		if (failOnNoInternet(callback)) {
			return;
		}

		Log.d(TAG, "Fetching entries for budget # " + b.getId());
		String requestUrl = entriesUrl + "/" + b.getId() + "/by_budget";

		client.get(requestUrl, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray entriesJson) {
				int entriesLen = entriesJson.length();

				// Iterate through the JSON array and create new
				// entries for each index.
				for (int i = 0; i < entriesLen; ++i) {
					try {
						JSONObject entriesObject = entriesJson.getJSONObject(i);

						long id = entriesObject.getLong("id");
						int amount = entriesObject.getInt("amount");
						LocalDate date = LocalDate.parse(
								entriesObject.getString("expenditure_date"),
								DateTimeFormat.forPattern(DATE_FORMAT));
						LocalDateTime createdAt = LocalDateTime.parse(
								entriesObject.getString("created_at"),
								DateTimeFormat.forPattern(DATETIME_FORMAT));
						LocalDateTime updatedAt = LocalDateTime.parse(
								entriesObject.getString("updated_at"),
								DateTimeFormat.forPattern(DATETIME_FORMAT));
						String notes = entriesObject.getString("notes");

						Entry newEntry = new Entry(id, amount, b, notes, date);
						newEntry.setCreatedAt(createdAt);
						newEntry.setUpdatedAt(updatedAt);

						b.addEntry(newEntry);
					} catch (JSONException e) {
						Log.e(TAG, e.getMessage());
						callback.onFailure(e.getMessage());
						return;
					}
				}

				callback.onSuccess(b.getEntries());
			}

			@Override
			public void onFailure(Throwable t, JSONObject obj) {
				String status = obj.optString("status", "Service Error");
				callback.onFailure(status);
			}

			@Override
			public void onFailure(Throwable t, String message) {
				if (t instanceof SocketTimeoutException) {
					callback.onFailure("Network Timeout");
				} else {
					callback.onFailure(message);
				}
			}
		});
	}

	/**
	 * Fetches a collection of Budgets and Entries owned by the current user.
	 *
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is a
	 * {@link java.util.List}&lt;{@link Budget}&gt;
	 * containing all Budgets for the current user, each
	 * containing all of its Entries.
	 */
	public void fetchBudgetsAndEntries(final ApiCallback<List<Budget>> callback) {
		if (failOnNoInternet(callback)) {
			return;
		}

		Log.d(TAG, "Fetching all budgets and entries");

		client.get(budgetsAndEntriesUrl, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray budgetsJson) {
				List<Budget> budgetList = new ArrayList<Budget>();

				int budgetsLen = budgetsJson.length();

				// Iterate through the JSON array and create new
				// budgets for each index.
				for (int i = 0; i < budgetsLen; ++i) {
					try {
						JSONObject budgetObject = budgetsJson.getJSONObject(i);

						String budgetName = budgetObject.getString("budget_name");
						String duration = budgetObject.getString("recurrence_duration");
						int amount = budgetObject.getInt("amount");
						boolean recur = budgetObject.optBoolean("recur");
						LocalDate startDate = LocalDate.parse(budgetObject.getString("start_date"),
								DateTimeFormat.forPattern(DATE_FORMAT));
						long id = budgetObject.getLong("id");


						Budget newBudget = new Budget(budgetName, amount,
								recur, startDate, Duration.valueOf(duration));
						newBudget.setId(id);

						JSONArray entriesJson = budgetObject.getJSONArray("entries");
						int entriesLen = entriesJson.length();

						// Iterate through the JSON array and create new
						// entries for each index.
						for (int j = 0; j < entriesLen; j++) {
							JSONObject entriesObject = entriesJson.getJSONObject(j);

							long entryId = entriesObject.getLong("id");
							int entryAmount = entriesObject.getInt("amount");
							LocalDate entryDate = LocalDate.parse(
									entriesObject.getString("expenditure_date"),
									DateTimeFormat.forPattern(DATE_FORMAT));
							LocalDateTime createdAt = LocalDateTime.parse(
									entriesObject.getString("created_at"),
									DateTimeFormat.forPattern(DATETIME_FORMAT));
							LocalDateTime updatedAt = LocalDateTime.parse(
									entriesObject.getString("updated_at"),
									DateTimeFormat.forPattern(DATETIME_FORMAT));
							String entryNotes = entriesObject.getString("notes");

							Entry newEntry = new Entry(entryId, entryAmount,
									newBudget, entryNotes, entryDate);
							newEntry.setCreatedAt(createdAt);
							newEntry.setUpdatedAt(updatedAt);

							newBudget.addEntry(newEntry);
						}

						budgetList.add(newBudget);
					} catch (JSONException e) {
						Log.e(TAG, e.getMessage());
						callback.onFailure(e.getMessage());
						return;
					}
				}

				callback.onSuccess(budgetList);
			}

			@Override
			public void onFailure(Throwable t, JSONObject obj) {
				String status = obj.optString("status", "Service Error");
				callback.onFailure(status);
			}

			@Override
			public void onFailure(Throwable t, String message) {
				if (t instanceof SocketTimeoutException) {
					callback.onFailure("Network Timeout");
				} else {
					callback.onFailure(message);
				}
			}
		});
	}

	/**
	 * Logs a user in to the API.
	 *
	 * @param email Email address of the user
	 * @param password Plaintext password of the user (will be sent over HTTPS)
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is always <code>null</code>.
	 */
	public void logIn(final String email, final String password,
			final ApiCallback<Object> callback) {
		if (failOnNoInternet(callback)) {
			callback.onFailure("Active internet connection required");
			return;
		}

		RequestParams params = new RequestParams();
		params.put("username", email);
		params.put("password", password);

		Log.d(TAG, "logging in as " + email);

		client.post(sessionUrl, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject obj) {
				Log.d(TAG, "User " + email + " logged in");
				if (callback != null) {
					callback.onSuccess(null);
				}
			}

			@Override
			public void onFailure(Throwable t, JSONObject obj) {
				String message = "Invalid email/password combination.";
				if (callback != null) {
					try {
						String nameErr = obj.getJSONArray("username").getString(0);
						String passErr = obj.getJSONArray("password_digest").getString(0);

						final String errMessage = nameErr + " " + passErr;

						Log.d(TAG, "errors: " + errMessage);
						callback.onFailure(message);
					} catch (JSONException ej) {
						Log.d(TAG, "JSON problems on log in: " + t.getMessage());
						callback.onFailure(message);
					}
				}
			}

			@Override
			public void onFailure(Throwable t, String message) {
				if (t instanceof SocketTimeoutException) {
					callback.onFailure("Network Timeout");
				} else {
					callback.onFailure(message);
				}
			}
		});
	}

	/**
	 * Creates and logs a user in to the API.
	 *
	 * @param email Email address of the user
	 * @param password Plaintext password of the user (will be sent over HTTPS)
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is always <code>null</code>.
	 */
	public void createUser(final String email, final String password,
			final ApiCallback<Object> callback) {
		if (failOnNoInternet(callback)) {
			return;
		}

		RequestParams params = new RequestParams();
		params.put("username", email);
		params.put("password", password);

		Log.d(TAG, "Creating user " + email);

		client.post(usersUrl, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject obj) {
				Log.d(TAG, "User " + email + " created");
				if (callback != null) {
					callback.onSuccess(null);
				}
			}

			@Override
			public void onFailure(Throwable t, JSONObject obj) {
				if (callback != null) {
					try {
						final String errMessage = obj.getJSONArray("username").getString(0);
						Log.d(TAG, "errors: " + errMessage);
						callback.onFailure(errMessage);
					} catch (JSONException ej) {
						Log.d(TAG, "JSON problems on user creation: " + t.getMessage());
						callback.onFailure(t.getMessage());
					}
				}
			}

			@Override
			public void onFailure(Throwable t, String message) {
				if (t instanceof SocketTimeoutException) {
					callback.onFailure("Network Timeout");
				} else {
					callback.onFailure(message);
				}
			}
		});
	}

	/**
	 * Checks whether or not a user is currently logged in.
	 *
	 * @param callback Callbacks to run if a user is or isn't logged in.
	 * If a user is logged in, it will call onSuccess with <code>null</code>
	 * as its parameter.
	 * If no user is logged in, it will call onFailure with <code>null</code>
	 * as its parameter.
	 */
	public void checkLoginStatus(final ApiCallback<Object> callback) {
		if (failOnNoInternet(callback)) {
			return;
		}

		client.get(sessionUrl, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				if (callback != null) {
					callback.onSuccess(null);
				}
			}

			@Override
			public void onFailure(Throwable t, String response) {
				if (callback != null) {
					callback.onFailure(null);
				}
			}
		});
	}

	/**
	 * Checks if there is an active connection to the Internet. If there is no connection,
	 * the callback specified is alerted via onFailure with an error specifying so.
	 *
	 * @param callback The callback to call onFailure on if there is no Internet.
	 * @return <code>true</code> if failure occurs (no internet), <code>false</code> otherwise.
	 */
	private boolean failOnNoInternet(ApiCallback<?> callback) {
		ConnectivityManager conMgr = (ConnectivityManager)
				UBudgetApp.getAppContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();

		if (activeNetwork != null && activeNetwork.isConnected()) {
			Log.d(TAG, "Internet connection found.");
			return false;
		}

		Log.d(TAG, "No internet connection found.");
		callback.onFailure("Active internet connection required.");
		return true;
	}
}
