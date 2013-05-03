package com.example.budgetmanager.api;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.Entry;

public class ApiInterface {
	public static ApiInterface getInstance() {
		// TODO: implement
		return null;
	}

	public void insert(Budget b, ApiCallback callback) {
		// TODO: implement
	}

	public void insert(Entry e, ApiCallback callback) {
		// TODO: implement
	}

	public void update(Budget b, ApiCallback callback) {
		// TODO: implement
	}

	public void update(Entry e, ApiCallback callback) {
		// TODO: implement
	}

	public void remove(Budget b, ApiCallback callback) {
		// TODO: implement
	}

	public void remove(Entry e, ApiCallback callback) {
		// TODO: implement
	}

	public void fetchBudgets(ApiCallback callback) {
		// TODO: implement
	}

	public void fetchEntries(Budget b, ApiCallback callback) {
		// TODO: implement
	}

	public void logIn(String email, String password, ApiCallback callback) {
		// TODO: implement
	}

	public void createUser(String email, String password, ApiCallback callback) {
		// TODO: implement
	}
}
