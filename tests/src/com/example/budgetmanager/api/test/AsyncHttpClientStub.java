package com.example.budgetmanager.api.test;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A subclass of the AsyncHttpClient for testing purposes.
 * Used as a stub to test the ApiInterface in isolation from the network.
 *
 * @author Graham grahamb5
 */
public class AsyncHttpClientStub extends AsyncHttpClient {
	private Queue<Object> responseQueue;
	private Queue<Boolean> successQueue;
	private String errorMessage = "Set to fail.";

	public AsyncHttpClientStub() {
		responseQueue = new LinkedList<Object>();
		successQueue = new LinkedList<Boolean>();
	}

	/**
	 * Enqueues a fake stubbed response from the server.
	 *
	 * @param obj The JSONObject or JSONArray to enqueue.
	 * @param succeeds Whether this response will call the onSuccess or onFailure handlers.
	 * @throws IllegalArgumentException if the passed object is not JSONObject or JSONArray
	 */
	public void setNextResponse(Object obj, boolean succeeds) {
		if (obj instanceof JSONObject || obj instanceof JSONArray) {
			responseQueue.add(obj);
			successQueue.add(succeeds);
		} else {
			throw new IllegalArgumentException("Must be JSONObject or JSONArray.");
		}
	}

	/**
	 * Enqueues an error message for the next response.
	 * Defaults to "Set to fail."
	 *
	 * @param msg The error message to set
	 */
	public void setErrorMessage(String msg) {
		errorMessage = msg;
	}

	/**
	 * Calls the specified handler with the last set JSON response.
	 *
	 * @param handler The handler to forward the JSON response to.
	 */
	private void callHandler(AsyncHttpResponseHandler handler) {
		if (responseQueue.size() > 0) {
			Object responseJson = responseQueue.poll();
			boolean success = successQueue.poll();

			if (responseJson instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject) responseJson;
				if (success) {
					((JsonHttpResponseHandler) handler).onSuccess(jsonObject);
				} else {
					((JsonHttpResponseHandler) handler).onFailure(
							new Exception(errorMessage), jsonObject);
				}
			} else {
				JSONArray jsonArray = (JSONArray) responseJson;
				if (success) {
					((JsonHttpResponseHandler) handler).onSuccess(jsonArray);
				} else {
					((JsonHttpResponseHandler) handler).onFailure(
							new Exception(errorMessage), jsonArray);
				}
			}
		}
	}

	// Override AsyncHttpClient methods to use our forwarding method.

	/**
	 * Returns a stub network connection response to the <code>handler</code>.
	 */
	@Override
	public void post(String arg, RequestParams params, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}

	/**
	 * Returns a stub network connection response to the <code>handler</code>.
	 */
	@Override
	public void post(String arg, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}

	/**
	 * Returns a stub network connection response to the <code>handler</code>.
	 */
	@Override
	public void put(String arg, RequestParams params, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}

	/**
	 * Returns a stub network connection response to the <code>handler</code>.
	 */
	@Override
	public void put(String arg, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}

	/**
	 * Returns a stub network connection response to the <code>handler</code>.
	 */
	@Override
	public void delete(String arg, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}

	/**
	 * Returns a stub network connection response to the <code>handler</code>.
	 */
	@Override
	public void get(String arg, RequestParams params, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}

	/**
	 * Returns a stub network connection response to the <code>handler</code>.
	 */
	@Override
	public void get(String arg, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}
}
