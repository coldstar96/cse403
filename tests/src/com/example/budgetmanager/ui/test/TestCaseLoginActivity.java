package com.example.budgetmanager.ui.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.EditText;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.LoginActivity;
import com.example.budgetmanager.api.ApiInterface;
import com.example.budgetmanager.api.test.TestAsyncHttpClient;
import com.example.budgetmanager.test.TestUtilities;
import com.jayway.android.robotium.solo.Solo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tests that LoginActivity throws the correct errors for different invalid
 * inputs in the email and password fields, and that it does not throw any extra
 * errors
 *
 * @author James PushaKi
 */
public class TestCaseLoginActivity
extends ActivityInstrumentationTestCase2<LoginActivity> {

	private static final String VALID_EMAIL = "example@gmail.com";
	private static final String INVALID_EMAIL_HAS_SPACE = "example @gmail.com";
	private static final String INVALID_EMAIL_NO_AT = "exampleatgmail.com";
	private static final String INVALID_EMAIL_NO_DOT = "example@gmaildotcom";
	private static final String VALID_PASSWORD = "abcd";
	private static final String INVALID_PASSWORD_TOO_SHORT = "abc";

	private Solo solo;

	private EditText emailView;
	private EditText passwordView;
	private Button loginButton;

	@SuppressWarnings("unused")
	private ApiInterface api;
	private TestAsyncHttpClient testClient;

	public TestCaseLoginActivity() {
		super(LoginActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());

		// Always tear down budgets!
		Budget.clearBudgets();

		emailView = (EditText) getActivity().
				findViewById(com.example.budgetmanager.R.id.email);
		passwordView = (EditText) getActivity().
				findViewById(com.example.budgetmanager.R.id.password);
		loginButton = (Button) solo.
				getView(com.example.budgetmanager.R.id.log_in_button);

		testClient = new TestAsyncHttpClient();
		api = TestUtilities.getStubbedApiInterface(testClient);
	}

	@MediumTest
	public void test_onCreate_viewsNotNull() {
		// Ensure all of the views are present
		assertNotNull(emailView);
		assertNotNull(passwordView);
		assertNotNull(loginButton);
	}

	@MediumTest
	public void test_attemptLogin_emptyEmailThrowsError() {
		// Set up a case with a missing email and a valid password.
		// Should only have an error on the email field.
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				emailView.setText("");
				passwordView.setText(VALID_PASSWORD);
				loginButton.performClick();
			}
		});

		solo.sleep(500);

		String expectedError = "This field is required";
		String foundError = (String) emailView.getError();

		assertNotNull("There was no error on the email view", foundError);
		assertEquals(expectedError, foundError);

		// Only the email view should throw an error
		assertNull(passwordView.getError());
	}

	@MediumTest
	public void test_attemptLogin_spaceInEmailThrowsError() {
		// Set up a case with a badly formatted email and a good password.
		// Should only have an error on the email field.
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				emailView.setText(INVALID_EMAIL_HAS_SPACE);
				passwordView.setText(VALID_PASSWORD);
				loginButton.performClick();
			}
		});

		solo.sleep(500);

		String expectedError = "This email address is invalid";
		String foundError = (String) emailView.getError();

		assertNotNull("There was no error on the email view", foundError);
		assertEquals(expectedError, foundError);

		// Only the email view should throw an error
		assertNull(passwordView.getError());
	}

	@MediumTest
	public void test_attemptLogin_noAtSignInEmailThrowsError() {
		// Set up a case with a badly formatted email and a good password.
		// Should only have an error on the email field.
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				emailView.setText(INVALID_EMAIL_NO_AT);
				passwordView.setText(VALID_PASSWORD);
				loginButton.performClick();
			}
		});

		solo.sleep(500);

		String expectedError = "This email address is invalid";
		String foundError = (String) emailView.getError();

		assertNotNull("There was no error on the email view", foundError);
		assertEquals(expectedError, foundError);

		// Only the email view should throw an error
		assertNull(passwordView.getError());
	}

	@MediumTest
	public void test_attemptLogin_noPeriodInEmailThrowsError() {
		// Set up a case with a badly formatted email and a good password.
		// Should only have an error on the email field.
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				emailView.setText(INVALID_EMAIL_NO_DOT);
				passwordView.setText(VALID_PASSWORD);
				loginButton.performClick();
			}
		});

		solo.sleep(500);

		String expectedError = "This email address is invalid";
		String foundError = (String) emailView.getError();

		assertNotNull("There was no error on the email view", foundError);
		assertEquals(expectedError, foundError);

		// Only the email view should throw an error
		assertNull(passwordView.getError());
	}

	@MediumTest
	public void test_attemptLogin_emptyPasswordThrowsError() {
		// Set up a case with a missing password but a good email.
		// Should only throw an error on the password field.
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				emailView.setText(VALID_EMAIL);
				passwordView.setText("");
				loginButton.performClick();
			}
		});

		solo.sleep(500);

		String expectedError = "This field is required";
		String foundError = (String) passwordView.getError();

		assertNotNull("There was no error on the password view", foundError);
		assertEquals(expectedError, foundError);

		// Only the password view should throw an error
		assertNull(emailView.getError());
	}

	@MediumTest
	public void test_attemptLogin_passwordTooShortThrowsError() {
		// Set up a case with too-short of a password but a good email.
		// Should only have an error on the password field.
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				emailView.setText(VALID_EMAIL);
				passwordView.setText(INVALID_PASSWORD_TOO_SHORT);
				loginButton.performClick();
			}
		});

		solo.sleep(500);

		String expectedError = "This password is too short";
		String foundError = (String) passwordView.getError();

		assertNotNull("There was no error on the password view", foundError);
		assertEquals(expectedError, foundError);

		// Only the password view should throw an error
		assertNull(emailView.getError());
	}

	@MediumTest
	public void test_attemptLogin_validEmailAndPasswordNoError() throws JSONException {
		// Use the stubbed HTTP client to set up a result from the server
		// without ever hitting the network. This result says that there's
		// no user with matching username/email and password.
		final String USERNAME_ERROR = VALID_EMAIL + ":";
		final String PASSWORD_ERROR = "Invalid username or password.";

		// Error JSONObject to be returned to the API
		JSONObject obj = new JSONObject().put("username", new JSONArray().put(USERNAME_ERROR))
				.put("password_digest", new JSONArray().put(PASSWORD_ERROR));

		// Set next "response" from the server.
		testClient.setNextResponse(obj, false);

		// Set up a completely valid email/password.
		// Should not cause any errors on the UI elements.
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				emailView.setText(VALID_EMAIL);
				passwordView.setText(VALID_PASSWORD);
				loginButton.performClick();
			}
		});

		solo.sleep(500);

		assertNull(emailView.getError());
		assertNull(passwordView.getError());
	}
}