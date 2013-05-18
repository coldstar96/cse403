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
	 * @param jsonString The string to create the JSON object from.
	 * @return true if the string parsed correctly, false otherwise.
	 */
	public void setNextResponse(Object obj) {
		if (object = obj instanceof JSONObject)
			jsonObject = (JSONObject) obj;
		else
			jsonArray = (JSONArray) obj;
	}
	
	private void callHandler(AsyncHttpResponseHandler handler) {
		assert handler instanceof JsonHttpResponseHandler;
		if (object)
			((JsonHttpResponseHandler) handler).onSuccess(jsonObject);
		else
			((JsonHttpResponseHandler) handler).onSuccess(jsonArray);
	}
	
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
