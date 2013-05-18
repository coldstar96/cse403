package com.example.budgetmanager.api.test;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * A subclass of the AsyncHttpClient for testing purposes.
 * 
 * @author Graham grahamb5
 */
public class TestAsyncHttpClient extends AsyncHttpClient {
	private JSONObject jsonObject;
	private JSONArray jsonArray;
	boolean object;
	
	/**
	 * Sets the next response to a handler.
	 * 
	 * @param obj The JSONObject or JSONArray to set the next response to.
	 * @throws IllegalArgumentException if the passed object is not JSONObject or JSONArray
	 */
	public void setNextResponse(Object obj) {
		if (object = obj instanceof JSONObject)
			jsonObject = (JSONObject) obj;
		else if (obj instanceof JSONArray)
			jsonArray = (JSONArray) obj;
		else
			throw new IllegalArgumentException("Must be JSONObject or JSONArray.");
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
			((JsonHttpResponseHandler) handler).onSuccess(jsonObject);
		} else {
			assert jsonArray != null;
			((JsonHttpResponseHandler) handler).onSuccess(jsonArray);
		}
	}
	
	// Overwrite AsyncHttpClient methods to use our forwarding method.
	
	@Override
	public void post(String arg, RequestParams params, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}
	
	@Override
	public void post(String arg, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}
	
	@Override
	public void get(String arg, RequestParams params, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}
	
	@Override
	public void get(String arg, AsyncHttpResponseHandler handler) {
		callHandler(handler);
	}
}
