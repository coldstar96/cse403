package com.example.budgetmanager.api;

import com.example.budgetmanager.Budget;

/**
 * Singleton class that facilitates connections to the HTTP API.
 *
 * @author chris brucec5
 *
 */
public class ApiInterface {

	/**
	 * Singleton factory method to get the singleton instance.
	 *
	 * @return singleton ApiInterface instance
	 */
	public static ApiInterface getInstance() {
		// TODO: implement
		return null;
	}

	/**
	 * Creates a budget on the API server. Asynchronous.
	 *
	 * @param b Budget instance to send to the server.
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is a {@link Long} that represents
	 * the ID of the Budget on the server.
	 * For onFailure, the object passed is a {@link String} detailing what went
	 * wrong.
	 */
	public void create(Budget b, ApiCallback callback) {
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
	 * For onFailure, the object passed is a {@link String} detailing what went
	 * wrong.
	 */
	public void create(Entry e, ApiCallback callback) {
		// TODO: implement
	}

	/**
	 * Updates an already existing Budget on the API server. Asynchronous.
	 *
	 * @param b Budget instance to send to the server.
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is always <code>null</code>.
	 * For onFailure, the object passed is a {@link String} detailing what went
	 * wrong.
	 */
	public void update(Budget b, ApiCallback callback) {
		// TODO: implement
	}

	/**
	 * Updates an already existing Budget on the API server. Asynchronous.
	 *
	 * @param e Entry instance to send to the server.
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is always <code>null</code>.
	 * For onFailure, the object passed is a {@link String} detailing what went
	 * wrong.
	 */
	public void update(Entry e, ApiCallback callback) {
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
	 * For onFailure, the object passed is a {@link String} detailing what went
	 * wrong.
	 */
	public void remove(Budget b, ApiCallback callback) {
		// TODO: implement
	}

	/**
	 * Destroys an already existing Entry on the API server. Asynchronous.
	 *
	 * @param e Entry instance to destroy.
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is always <code>null</code>.
	 * For onFailure, the object passed is a {@link String} detailing what went
	 * wrong.
	 */
	public void remove(Entry e, ApiCallback callback) {
		// TODO: implement
	}

	/**
	 * Fetches a collection of Budgets owned by the current user.
	 *
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is a
	 * {@link java.util.Collection}&lt;{@link Budget}&gt;
	 * containing all Budgets for the current user.
	 * For onFailure, the object passed is a {@link String} detailing what went
	 * wrong.
	 */
	public void fetchBudgets(ApiCallback callback) {
		// TODO: implement
	}

	/**
	 * Fetches a collection of Entries associated with a given Budget.
	 *
	 * @param b Budget to fetch Entries from.
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is a
	 * {@link java.util.Collection}&lt;{@link Entry}&gt;
	 * containing all Entries for the given Budget.
	 * For onFailure, the object passed is a {@link String} detailing what went
	 * wrong.
	 */
	public void fetchEntries(Budget b, ApiCallback callback) {
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
	 * For onFailure, the object passed is a {@link String} detailing what went
	 * wrong.
	 */
	public void logIn(String email, String password, ApiCallback callback) {
		// TODO: implement
	}

	/**
	 * Creates and logs a user in to the API.
	 *
	 * @param email Email address of the user
	 * @param password Plaintext password of the user (will be sent over HTTPS)
	 * @param callback Callbacks to run on success or failure, or
	 * <code>null</code> for no callbacks.
	 * For onSuccess, the object passed is always <code>null</code>.
	 * For onFailure, the object passed is a {@link String} detailing what went
	 * wrong.
	 */
	public void createUser(String email, String password, ApiCallback callback) {
		// TODO: implement
	}
}
