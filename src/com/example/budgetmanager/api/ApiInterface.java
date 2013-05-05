package com.example.budgetmanager.api;
import com.example.budgetmanager.*;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.R;
import com.example.budgetmanager.UBudgetApp;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

/**
 * Singleton class that facilitates connections to the HTTP API.
 *
 * @author chris brucec5
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

	private final AsyncHttpClient client;

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

		PersistentCookieStore cookieStore = new PersistentCookieStore(context);
		client = new AsyncHttpClient();
		client.setCookieStore(cookieStore);

		// Need to specify that we want JSON back from the server.
		client.addHeader("Accept", "application/json");
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
		RequestParams params = new RequestParams();
		params.put("budget_name", b.getName());
		params.put("amount", "" + b.getBudgetAmount());
		params.put("recur", "" + b.doesRecur());
		params.put("start_date", "" + b.startTimeMillis());
		params.put("recurrence_duration", b.getDuration().toString());
		params.put("other_duration", "" + b.getOtherDuration());

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
			public void onFailure(Throwable e) {
				callback.onFailure(e.getMessage());
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
	public void create(Entry e, ApiCallback<Long> callback) {
		RequestParams params = new RequestParams();
		params.put("amount", "" + e.getAmount());
		params.put("notes", e.getNotes());
		params.put("expenditure_date", e.getDate().toString());
		params.put("budget_id", "" + e.getBudget().getId());
	}

	/**
	 * Updates an already existing Budget on the API server. Asynchronous.
	 *
	 * @param b Budget instance to send to the server.
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is always <code>null</code>.
	 */
	public void update(Budget b, ApiCallback<Object> callback) {
		// TODO: implement
	}

	/**
	 * Updates an already existing Budget on the API server. Asynchronous.
	 *
	 * @param e Entry instance to send to the server.
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is always <code>null</code>.
	 */
	public void update(Entry e, ApiCallback<Object> callback) {
		// TODO: implement
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
	public void remove(Budget b, ApiCallback<Object> callback) {
		// TODO: implement
	}

	/**
	 * Destroys an already existing Entry on the API server. Asynchronous.
	 *
	 * @param e Entry instance to destroy.
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is always <code>null</code>.
	 */
	public void remove(Entry e, ApiCallback<Object> callback) {
		// TODO: implement
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
		Log.d(TAG, "Fetching budgets");

		client.get(budgetsUrl, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray budgetsJson) {
				List<Budget> budgetList = new ArrayList<Budget>();

				int budgetsLen = budgetsJson.length();

				for (int i = 0; i < budgetsLen; ++i) {
					try {
						JSONObject budgetObject = budgetsJson.getJSONObject(i);

						String budgetName = budgetObject.getString("budget_name");
						String duration = budgetObject.getString("recurrence_duration");
						int amount = budgetObject.getInt("amount");
						int currentAmount = budgetObject.optInt("amount_so_far");
						int otherDuration = budgetObject.optInt("other_duration");
						boolean recur = budgetObject.optBoolean("recur");
						long startDate = budgetObject.getLong("start_date");
						long id = budgetObject.getLong("id");

						Budget newBudget = new Budget(budgetName, amount,
								currentAmount, recur, startDate,
								duration, otherDuration);
						newBudget.setId(id);

						budgetList.add(newBudget);
					} catch (JSONException e) {
						Log.e(TAG, e.getMessage());
					}
				}

				callback.onSuccess(budgetList);
			}

			@Override
			public void onFailure(Throwable e, JSONObject obj) {
				String status = obj.optString("status", "Service Error");
				callback.onFailure(status);
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
	public void fetchEntries(Budget b, ApiCallback<List<Entry>> callback) {
		// TODO: implement
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
			public void onFailure(Throwable e, JSONObject obj) {
				if (callback != null) {
					try {
						String nameErr = obj.getJSONArray("username").join(" ");
						String passErr = obj.getJSONArray("password_digest").join(" ");

						final String errMessage = nameErr + " " + passErr;

						Log.d(TAG, "errors: " + errMessage);
						callback.onFailure(errMessage);
					} catch (JSONException ej) {
						Log.d(TAG, "JSON problems on log in: " + e.getMessage());
						callback.onFailure(e.getMessage());
					}
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
			public void onFailure(Throwable e, JSONObject obj) {
				if (callback != null) {
					try {
						final String errMessage = obj.getJSONArray("username").join(" ");
						Log.d(TAG, "errors: " + errMessage);
						callback.onFailure(errMessage);
					} catch (JSONException ej) {
						Log.d(TAG, "JSON problems on user creation: " + e.getMessage());
						callback.onFailure(e.getMessage());
					}
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
		client.get(sessionUrl, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				if (callback != null) {
					callback.onSuccess(null);
				}
			}

			@Override
			public void onFailure(Throwable e, String response) {
				if (callback != null) {
					callback.onFailure(null);
				}
			}
		});
	}
}
