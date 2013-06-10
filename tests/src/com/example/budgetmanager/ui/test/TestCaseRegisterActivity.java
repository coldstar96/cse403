package com.example.budgetmanager.ui.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.EditText;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.MainActivity;
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

		// Set up a fake API
		testClient = new AsyncHttpClientStub();
		api = TestUtilities.getStubbedApiInterface(testClient);

		Budget.clearBudgets();
	}

	@Override
	protected void tearDown() {
		solo.finishOpenedActivities();
	}

	@MediumTest
	public void test_blankEmailField_shouldNotAllow() {
		// Just click on the register button without doing anything.
		solo.clickOnButton(0);

		// The email field should complain about being blank
		String emailError = (String) emailField.getError();

		assertNotNull(emailError);
		assertEquals(REQUIRED_FIELD, emailError);
	}

	@MediumTest
	public void test_blankPasswordField_shouldNotAllow() {
		// Just click on the register button without doing anything.
		solo.clickOnButton(0);

		// The password field should complain about being blank
		String passwordError = (String) passwordField.getError();

		assertNotNull(passwordError);
		assertEquals(REQUIRED_FIELD, passwordError);
	}

	@MediumTest
	public void test_blankVerifyPasswordField_shouldNotAllow() {
		// Just click on the register button without doing anything.
		solo.clickOnButton(0);

		// The confirmation field should complain about being blank
		String confirmError = (String) passwordConfirmField.getError();

		assertNotNull(confirmError);
		assertEquals(REQUIRED_FIELD, confirmError);
	}

	@MediumTest
	public void test_badEmail_shouldNotAllow() {
		// Enter an email that isn't actually a valid email address
		// Try to register, wait for animations.
		solo.typeText(emailField, "not an email");
		solo.clickOnButton(0);

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
		solo.typeText(passwordField, "Password1");
		solo.typeText(passwordConfirmField, "Password2");
		solo.clickOnButton(0);

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

	@MediumTest
	public void test_emailInUseAlready_shouldNotAllow() throws JSONException {
		// Use the stubbed HTTP client to set up a result from the server
		// without ever hitting the network. This result says that there's
		// already a user with that email, so it should fail the request.
		final String USERNAME_ERROR = "Username already taken.";
		JSONObject obj = new JSONObject().put("username", new JSONArray().put(USERNAME_ERROR));
		testClient.setNextResponse(obj, false);

		String password = "password";

		// Enter the same password and confirmation
		// Try to register
		solo.typeText(emailField, "example@example.com");
		solo.typeText(passwordField, password);
		solo.typeText(passwordConfirmField, password);
		solo.clickOnButton(0);

		String confirmError = (String) passwordConfirmField.getError();
		String passwordError = (String) passwordField.getError();

		// We should have stayed on the RegisterActivity.
		solo.assertCurrentActivity("Should have stayed on register activity",
				RegisterActivity.class);

		// There shouldn't be errors for the other fields
		assertNull(passwordError);
		assertNull(confirmError);
	}

	@MediumTest
	public void test_newEmail_shouldGoToMainActivity() {
		// Use the stubbed HTTP client to set up a result from the server
		// without ever hitting the network. This result says that that
		// we successfully created a user.
		testClient.setNextResponse(new JSONObject(), true);

		String password = "password";

		// Enter the same password and confirmation
		// Try to register
		solo.typeText(emailField, "example@example.com");
		solo.typeText(passwordField, password);
		solo.typeText(passwordConfirmField, password);
		solo.clickOnButton(0);

		String emailError = (String) emailField.getError();
		String confirmError = (String) passwordConfirmField.getError();
		String passwordError = (String) passwordField.getError();

		// There shouldn't be errors for the fields
		assertNull(emailError);
		assertNull(passwordError);
		assertNull(confirmError);

		boolean madeItToActivity = solo.waitForActivity(MainActivity.class);
		assertTrue("Should have gone to the MainActivity", madeItToActivity);
	}
}
