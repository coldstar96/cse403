package com.example.budgetmanager.ui.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.EditText;

import com.example.budgetmanager.Budget;
import com.example.budgetmanager.LoginActivity;
import com.example.budgetmanager.MainActivity;
import com.example.budgetmanager.RegisterActivity;
import com.example.budgetmanager.api.ApiInterface;
import com.example.budgetmanager.api.test.AsyncHttpClientStub;
import com.example.budgetmanager.test.TestUtilities;
import com.jayway.android.robotium.solo.Solo;

import org.json.JSONArray;
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
	private AsyncHttpClientStub testClient;

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

		testClient = new AsyncHttpClientStub();
		api = TestUtilities.getStubbedApiInterface(testClient);
	}

	@Override
	protected void tearDown() {
		solo.finishOpenedActivities();
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
		solo.typeText(passwordView, VALID_PASSWORD);
		solo.clickOnButton(0);

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
		solo.typeText(emailView, INVALID_EMAIL_HAS_SPACE);
		solo.typeText(passwordView, VALID_PASSWORD);
		solo.clickOnButton(0);

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
		solo.typeText(emailView, INVALID_EMAIL_NO_AT);
		solo.typeText(passwordView, VALID_PASSWORD);
		solo.clickOnButton(0);

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
		solo.typeText(emailView, INVALID_EMAIL_NO_DOT);
		solo.typeText(passwordView, VALID_PASSWORD);
		solo.clickOnButton(0);

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
		solo.typeText(emailView, VALID_EMAIL);
		solo.clickOnButton(0);

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
		solo.typeText(emailView, VALID_EMAIL);
		solo.typeText(passwordView, INVALID_PASSWORD_TOO_SHORT);
		solo.clickOnButton(0);

		String expectedError = "This password is too short";
		String foundError = (String) passwordView.getError();

		assertNotNull("There was no error on the password view", foundError);
		assertEquals(expectedError, foundError);

		// Only the password view should throw an error
		assertNull(emailView.getError());
	}

	@MediumTest
	public void test_attemptLogin_validEmailAndPasswordNoError() {
		// Use the stubbed HTTP client to set up a result from the server
		// without ever hitting the network.

		// Set next "responses" from the server in order to get to the Main activity
		testClient.setNextResponse(new JSONObject(), true);
		// Send back no budgets/entries (we don't need any for this test, but we
		// need the stubbed request
		testClient.setNextResponse(new JSONArray(), true);

		// Set up a completely valid email/password.
		// Should not cause any errors on the UI elements.
		solo.typeText(emailView, VALID_EMAIL);
		solo.typeText(passwordView, VALID_PASSWORD);
		solo.clickOnButton(0);

		assertNull(emailView.getError());
		assertNull(passwordView.getError());

		boolean madeItToActivity = solo.waitForActivity(MainActivity.class);
		assertTrue("Should have gone to the MainActivity", madeItToActivity);
	}

	@MediumTest
	public void test_clickOnRegister_emptyFields_shouldHaveEmptyFields() {
		// Click on the Register button
		solo.clickOnButton("Register");

		// Wait to go to the RegisterActivity
		boolean madeItToActivity = solo.waitForActivity(RegisterActivity.class);
		assertTrue("Should have gone to the RegisterActivity", madeItToActivity);

		EditText registerEmailView = solo.getEditText(0);
		EditText registerPasswordView = solo.getEditText(1);

		String registerEmailString = registerEmailView.getText().toString();
		String registerPasswordString = registerPasswordView.getText().toString();

		assertEquals("The email text should have been empty", "", registerEmailString);
		assertEquals("The password text should have been empty", "", registerPasswordString);
	}

	@MediumTest
	public void test_clickOnRegister_filledFields_shouldHaveFilledFields() {
		// Click on the Register button
		solo.enterText(emailView, VALID_EMAIL);
		solo.enterText(passwordView, VALID_PASSWORD);
		solo.clickOnButton("Register");

		// Wait to go to the RegisterActivity
		boolean madeItToActivity = solo.waitForActivity(RegisterActivity.class);
		assertTrue("Should have gone to the RegisterActivity", madeItToActivity);

		EditText registerEmailView = solo.getEditText(0);
		EditText registerPasswordView = solo.getEditText(1);

		String registerEmailString = registerEmailView.getText().toString();
		String registerPasswordString = registerPasswordView.getText().toString();

		assertEquals("The email text should have been empty", VALID_EMAIL, registerEmailString);
		assertEquals("The password text should have been empty", VALID_PASSWORD, registerPasswordString);
	}
}