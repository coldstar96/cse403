package com.example.budgetmanager.test;

import com.example.budgetmanager.RegisterActivity;
import com.jayway.android.robotium.solo.Solo;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.EditText;

public class TestCaseRegisterActivity
	extends ActivityInstrumentationTestCase2<RegisterActivity> {

	private Solo solo;

	private EditText emailField;
	private EditText passwordField;
	private EditText passwordConfirmField;

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

		emailField = solo.getEditText("Email");
		passwordField = solo.getEditText("Password");
		passwordConfirmField = solo.getEditText("Verify Password");
	}

	public void test_blankEmailField_shouldNotAllow() {
		solo.clickOnButton("Register");
		solo.sleep(500);

		String emailError = (String) emailField.getError();

		assertNotNull(emailError);
		assertEquals(REQUIRED_FIELD, emailError);
	}

	public void test_blankPasswordField_shouldNotAllow() {
		solo.clickOnButton("Register");
		solo.sleep(500);

		String passwordError = (String) passwordField.getError();

		assertNotNull(passwordError);
		assertEquals(REQUIRED_FIELD, passwordError);
	}

	public void test_blankVerifyPasswordField_shouldNotAllow() {
		solo.clickOnButton("Register");
		solo.sleep(500);

		String confirmError = (String) passwordConfirmField.getError();

		assertNotNull(confirmError);
		assertEquals(REQUIRED_FIELD, confirmError);
	}

	public void test_badEmail_shouldNotAllow() {
		solo.enterText(emailField, "not an email");

		solo.clickOnButton("Register");
		solo.sleep(500);

		String expectedError = "This email address is invalid";
		String emailError = (String) emailField.getError();

		assertNotNull(emailError);
		assertEquals(expectedError, emailError);
	}

	public void test_nonMatchingPasswords_shouldNotAllow() {
		solo.enterText(passwordField, "Password1");
		solo.enterText(passwordConfirmField, "Password2");

		solo.clickOnButton("Register");
		solo.sleep(500);

		String expectedError = "This password does not match the password above.";
		String confirmError = (String) passwordConfirmField.getError();
		String passwordError = (String) passwordField.getError();

		assertNotNull(confirmError);
		assertEquals(expectedError, confirmError);

		// There shouldn't be a problem with the first password field
		assertNull(passwordError);
	}

	@LargeTest
	public void test_emailInUseAlready_shouldNotAllow() {
		// NOTE: we're banking on there already being a user with this email.
		// In the current production state, there is. Thus, we shall never
		// nuke the production database or remove this user!
		solo.enterText(emailField, "chris808@gmail.com");

		String password = "password";
		solo.enterText(passwordField, password);
		solo.enterText(passwordConfirmField, password);

		solo.clickOnButton("Register");
		// Give the network lots of time to respond
		solo.sleep(10000);

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
