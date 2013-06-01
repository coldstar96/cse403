package com.example.budgetmanager.api.test;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A subclass of the AsyncHttpClient for testing purposes.
 * Used as a stub to test the ApiInterface in isolation from the network.
 *
 * @author Graham grahamb5
 */
public class TestAsyncHttpClient extends AsyncHttpClient {
	private JSONObject jsonObject;
	private JSONArray jsonArray;
	boolean object;
	boolean succeeds;

	/**
	 * Sets the next response to a handler.
	 *
	 * @param obj The JSONObject or JSONArray to set the next response to.
	 * @param succeeds Whether the next response will call the onSuccess or onFailure handlers.
	 * @throws IllegalArgumentException if the passed object is not JSONObject or JSONArray
	 */
	public void setNextResponse(Object obj, boolean succeeds) {
		if (object = obj instanceof JSONObject) {
			jsonObject = (JSONObject) obj;
		} else if (obj instanceof JSONArray) {
			jsonArray = (JSONArray) obj;
		} else {
			throw new IllegalArgumentException("Must be JSONObject or JSONArray.");
		}

		this.succeeds = succeeds;
	}

	/**
	 * Calls the specified handler with the last set JSON response.
	 *
	 * @param handler The handler to forward the JSON response to.
	 */
	private void callHandler(AsyncHttpResponseHandler handler) {
		assert handler instanceof JsonHttpResponseHandler;
		if (object) {
			assert jsonObject != null;
			if (succeeds) {
				((JsonHttpResponseHandler) handler).onSuccess(jsonObject);
			} else {
				((JsonHttpResponseHandler) handler).onFailure(new Exception("Set to fail."), jsonObject);
			}
		} else {
			assert jsonArray != null;
			if (succeeds) {
				((JsonHttpResponseHandler) handler).onSuccess(jsonArray);
			} else {
				((JsonHttpResponseHandler) handler).onFailure(new Exception("Set to fail."), jsonArray);
			}
		}
	}

	// Overwrite AsyncHttpClient methods to use our forwarding method.

	/**
	 * Returns a mock network connection response to the <code>handler</code>.
	 */
	@Override
	public void post(String arg, RequestParams params, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}

	/**
	 * Returns a mock network connection response to the <code>handler</code>.
	 */
	@Override
	public void post(String arg, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}

	/**
	 * Returns a mock network connection response to the <code>handler</code>.
	 */
	@Override
	public void put(String arg, RequestParams params, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}

	/**
	 * Returns a mock network connection response to the <code>handler</code>.
	 */
	@Override
	public void put(String arg, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}

	/**
	 * Returns a mock network connection response to the <code>handler</code>.
	 */
	@Override
	public void delete(String arg, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}

	/**
	 * Returns a mock network connection response to the <code>handler</code>.
	 */
	@Override
	public void get(String arg, RequestParams params, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}

	/**
	 * Returns a mock network connection response to the <code>handler</code>.
	 */
	@Override
	public void get(String arg, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}
}
