package com.example.budgetmanager.api;

/**
 * Interface used to pass callbacks to the ApiInterface.
 *
 */
public interface ApiCallback {

	/**
	 * Method to be run if the API request completed successfully.
	 *
	 * @param o Result from a successful API request. The API method that this
	 * ApiCallback is passed to will define what type <code>o</code> is.
	 */
	public void onSuccess(Object o);

	/**
	 * Method to be run if the API request completed unsuccessfully.
	 *
	 * @param o Result from an unsuccessful API request. The API method that
	 * this ApiCallback is passed to will define what type <code>o</code> is.
	 */
	public void onFailure(Object o);
}
