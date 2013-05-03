package com.example.budgetmanager.api;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Entry;
import com.example.budgetmanager.UBudgetApp;
import com.loopj.android.http.AsyncHttpClient;
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
	private static String TAG = "ApiInterface";
	
	private AsyncHttpClient client;
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
	public void create(Budget b, ApiCallback<Long> callback) {
		// TODO: implement
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
		// TODO: implement
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
	public void fetchBudgets(ApiCallback<List<Budget>> callback) {
		// TODO: implement
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
	public void logIn(String email, String password,
			ApiCallback<Object> callback) {
		RequestParams params = new RequestParams();
		params.put("username", email);
		params.put("password", password);
		Log.d(TAG, "logging in!");
		client.post("https://ubudget.herokuapp.com/session", params, new JsonHttpResponseHandler() {
			public void onSuccess(JSONObject obj) {
				try {
					Log.d(TAG, "User name: " + obj.getJSONObject("user").getString("username"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d(TAG, "Error: " + e.getMessage());
				}
			}
			
			public void onFailure(Throwable e, JSONObject obj) {
				Log.d(TAG, "Failure: " + e.getMessage());
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
			ApiCallback<Object> callback) {
		RequestParams params = new RequestParams();
		params.put("username", email);
		params.put("password", password);
		Log.d(TAG, "logging in!");
		client.post("https://ubudget.herokuapp.com/users", params, new JsonHttpResponseHandler() {
			public void onSuccess(JSONObject obj) {
				Log.d(TAG, "User " + email + " created");
			}
			
			public void onFailure(Throwable e, JSONObject obj) {
				Log.d(TAG, "Failure: " + e.getMessage());
			}
		});
	}
}
