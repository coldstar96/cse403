package com.example.budgetmanager.ui.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.EditText;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.RegisterActivity;
import com.example.budgetmanager.api.ApiInterface;
import com.example.budgetmanager.api.test.AsyncHttpClientStub;
import com.example.budgetmanager.test.TestUtilities;
import com.jayway.android.robotium.solo.Solo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Test the register activity.
 *
 * @author chris brucec5
 *
 */
public class TestCaseRegisterActivity
	extends ActivityInstrumentationTestCase2<RegisterActivity> {

	private Solo solo;

	private EditText emailField;
	private EditText passwordField;
	private EditText passwordConfirmField;
	private Button registerButton;

	@SuppressWarnings("unused")
	private ApiInterface api;
	private AsyncHttpClientStub testClient;

	private static final String REQUIRED_FIELD = "This field is required";

	public TestCaseRegisterActivity() {
		super(RegisterActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// This needs to be done because the RegisterActivity expects
		// extras in its intent to be present. Without them, you get a
		// NullPointerException.
		Intent intent = new Intent();
		intent.putExtra("email", "");
		intent.putExtra("password", "");
		setActivityIntent(intent);

		solo = new Solo(getInstrumentation(), getActivity());

		// Get the various UI elements to interact with
		emailField = (EditText) getActivity().findViewById(
				com.example.budgetmanager.R.id.email);
		passwordField = (EditText) getActivity().findViewById(
				com.example.budgetmanager.R.id.password);
		passwordConfirmField = (EditText) getActivity().findViewById(
				com.example.budgetmanager.R.id.password2);
		registerButton = (Button) getActivity().findViewById(
				com.example.budgetmanager.R.id.register_button);


		// Set up a fake API
		testClient = new AsyncHttpClientStub();
		api = TestUtilities.getStubbedApiInterface(testClient);

		Budget.clearBudgets();
	}

	@MediumTest
	public void test_blankEmailField_shouldNotAllow() {
		// Just click on the register button without doing anything.
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				registerButton.performClick();
			}
		});
		solo.sleep(1000);

		// The email field should complain about being blank
		String emailError = (String) emailField.getError();

		assertNotNull(emailError);
		assertEquals(REQUIRED_FIELD, emailError);
	}

	@MediumTest
	public void test_blankPasswordField_shouldNotAllow() {
		// Just click on the register button without doing anything.
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				registerButton.performClick();
			}
		});
		solo.sleep(1000);

		// The password field should complain about being blank
		String passwordError = (String) passwordField.getError();

		assertNotNull(passwordError);
		assertEquals(REQUIRED_FIELD, passwordError);
	}

	@MediumTest
	public void test_blankVerifyPasswordField_shouldNotAllow() {
		// Just click on the register button without doing anything.
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				registerButton.performClick();
			}
		});
		solo.sleep(1000);

		// The confirmation field should complain about being blank
		String confirmError = (String) passwordConfirmField.getError();

		assertNotNull(confirmError);
		assertEquals(REQUIRED_FIELD, confirmError);
	}

	@MediumTest
	public void test_badEmail_shouldNotAllow() {
		// Enter an email that isn't actually a valid email address
		// Try to register, wait for animations.
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				emailField.setText("not an email");
				registerButton.performClick();
			}
		});
		solo.sleep(1000);

		// The email field should complain about the email being bad
		String expectedError = "This email address is invalid";
		String emailError = (String) emailField.getError();

		assertNotNull(emailError);
		assertEquals(expectedError, emailError);
	}

	@MediumTest
	public void test_nonMatchingPasswords_shouldNotAllow() {
		// Input mismatched password/confirmation
		// Try to register and wait for animations
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				passwordField.setText("Password1");
				passwordConfirmField.setText("Password2");
				registerButton.performClick();
			}
		});
		solo.sleep(1000);

		// There should be an error about passwords not matching
		// But only on the password field. Confirmation has no errors.
		String expectedError = "This password does not match the password above.";
		String confirmError = (String) passwordConfirmField.getError();
		String passwordError = (String) passwordField.getError();

		assertNotNull(confirmError);
		assertEquals(expectedError, confirmError);

		// There shouldn't be a problem with the first password field
		assertNull(passwordError);
	}

	@LargeTest
	public void test_emailInUseAlready_shouldNotAllow() throws JSONException {
		// Use the stubbed HTTP client to set up a result from the server
		// without ever hitting the network. This result says that there's
		// already a user with that email, so it should fail the request.
		final String USERNAME_ERROR = "Username already taken.";
		JSONObject obj = new JSONObject().put("username", new JSONArray().put(USERNAME_ERROR));
		testClient.setNextResponse(obj, false);

		// Enter the same password and confirmation
		// Try to register
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String password = "password";

				emailField.setText("example@example.com");
				passwordField.setText(password);
				passwordConfirmField.setText(password);
				registerButton.performClick();
			}
		});
		solo.sleep(1000);

		// The only error should be on the email field,
		// complaining about the non-uniqueness of the emails.
		String expectedError = "This email address is already in use";
		String emailError = (String) emailField.getError();
		String confirmError = (String) passwordConfirmField.getError();
		String passwordError = (String) passwordField.getError();

		assertNotNull(emailError);
		assertEquals(expectedError, emailError);

		// There shouldn't be errors for the other fields
		assertNull(passwordError);
		assertNull(confirmError);

	}

}
