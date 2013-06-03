package com.example.budgetmanager.api.test;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import junit.framework.Assert;

/**
 * A subclass of the AsyncHttpClient for testing purposes.
 * Used as a mock to test the ApiInterface in isolation from the network.
 * Specifically, tests to ensure that it sends the right RequestParams
 * to the "server".
 *
 * @author Chris brucec5
 */
public class AsyncHttpClientMock extends AsyncHttpClient {
	private static final String TAG = "AsyncHttpClientMock";

	private RequestParams expectedParams;

	public void expect(RequestParams params) {
		expectedParams = params;
	}

	/**
	 * Loops through a given RequestParams and asserts that it has everything expected.
	 * Will only run the tests if expect has been called.
	 *
	 * @param testParams The params to test
	 * @throws Exception If any weird reflection problems happen
	 */
	private void testRequestParams(RequestParams testParams) {
		if (expectedParams != null) {
			List<BasicNameValuePair> expected;
			List<BasicNameValuePair> actual;

			try {
				expected = getParamList(expectedParams);
				actual = getParamList(testParams);

				for (BasicNameValuePair ePair : expected) {
					boolean found = false;
					for (BasicNameValuePair aPair : actual) {
						Log.d(TAG, "Testing " + ePair.toString() + " with " + aPair.toString());
						if (ePair.getName().equals(aPair.getName())
								&& ePair.getValue().equals(aPair.getValue())) {
							found = true;
							break;
						}
					}
					String msg = "RequestParams not what was expected: "
								+ ePair.getName() + " didn't match";
					Assert.assertTrue(msg, found);
				}
			} catch (Exception e) {
				Assert.fail("Failure initializing RequestParams check: " + e.getMessage());
			}
		}
	}

	/**
	 * Break into the RequestParams to liberate the list of BasicNameValuePairs so we can test
	 * their contents.
	 *
	 * @param params The RequestParams to get the parameters out of.
	 * @return The list of BasicNameValuePairs that contain the RequestParams parameters.
	 */
	private List<BasicNameValuePair> getParamList(RequestParams params) throws NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Class<? extends RequestParams> requestParams = params.getClass();
		Method getParams = requestParams.getDeclaredMethod("getParamsList");

		getParams.setAccessible(true);

		@SuppressWarnings("unchecked")
		List<BasicNameValuePair> paramList = (List<BasicNameValuePair>) getParams.invoke(params);

		return paramList;
	}

	// Override AsyncHttpClient methods to use our expectation

	/**
	 * Tests to see if the params passed in to this post request match the expectation.
	 */
	@Override
	public void post(String arg, RequestParams params, AsyncHttpResponseHandler handler) {
		testRequestParams(params);
	}

	/**
	 * Does nothing, as there are no RequestParams to pass in.
	 */
	@Override
	public void post(String arg, AsyncHttpResponseHandler handler) {
	}

	/**
	 * Tests to see if the params passed in to this put request match the expectation.
	 */
	@Override
	public void put(String arg, RequestParams params, AsyncHttpResponseHandler handler) {
		testRequestParams(params);
	}

	/**
	 * Does nothing, as there are no RequestParams to pass in.
	 */
	@Override
	public void put(String arg, AsyncHttpResponseHandler handler) {
	}

	/**
	 * Does nothing, as there are no RequestParams to pass in.
	 */
	@Override
	public void delete(String arg, AsyncHttpResponseHandler handler) {
	}

	/**
	 * Tests to see if the params passed in to this get request match the expectation.
	 */
	@Override
	public void get(String arg, RequestParams params, AsyncHttpResponseHandler handler) {
		testRequestParams(params);
	}

	/**
	 * Does nothing, as there are no RequestParams to pass in.
	 */
	@Override
	public void get(String arg, AsyncHttpResponseHandler handler) {
	}

}
