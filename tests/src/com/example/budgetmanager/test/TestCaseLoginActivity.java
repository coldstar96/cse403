package com.example.budgetmanager.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

import com.example.budgetmanager.LoginActivity;
import com.example.budgetmanager.UBudgetApp;
import com.jayway.android.robotium.solo.Solo;

/**
 * Tests that LoginActivity throws the correct errors for different invalid
 * inputs in the email and password fields, and does not throw any extra errors
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

	private UBudgetApp app;
	private Solo solo;
	
	private EditText emailView;
	private EditText passwordView;
	private Button loginButton;

	public TestCaseLoginActivity() {
		super(LoginActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());

		app = (UBudgetApp) getActivity().getApplication();
		app.getBudgetList().clear();
		
		emailView = (EditText) getActivity().
				findViewById(com.example.budgetmanager.R.id.email);
		passwordView = (EditText) getActivity().
				findViewById(com.example.budgetmanager.R.id.password);
		loginButton = (Button) solo.
				getView(com.example.budgetmanager.R.id.log_in_button);
	}
	
	public void test_onCreate_viewsNotNull() {
		assertNotNull(emailView);
		assertNotNull(passwordView);
		assertNotNull(loginButton);
	}
	
	public void test_attemptLogin_emptyEmailThrowsError() {
		solo.typeText(emailView, "");
		solo.typeText(passwordView, VALID_PASSWORD);
		
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
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
	
	public void test_attemptLogin_spaceInEmailThrowsError() {
		solo.typeText(emailView, INVALID_EMAIL_HAS_SPACE);
		solo.typeText(passwordView, VALID_PASSWORD);

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
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
	
	public void test_attemptLogin_noAtSignInEmailThrowsError() {
		solo.typeText(emailView, INVALID_EMAIL_NO_AT);
		solo.typeText(passwordView, VALID_PASSWORD);
		
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
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
	
	public void test_attemptLogin_noPeriodInEmailThrowsError() {
		solo.typeText(emailView, INVALID_EMAIL_NO_DOT);
		solo.typeText(passwordView, VALID_PASSWORD);

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
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
	
	public void test_attemptLogin_emptyPasswordThrowsError() {
		solo.typeText(emailView, VALID_EMAIL);
		solo.typeText(passwordView, "");

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
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
	
	public void test_attemptLogin_passwordTooShortThrowsError() {
		solo.typeText(emailView, VALID_EMAIL);
		solo.typeText(passwordView, INVALID_PASSWORD_TOO_SHORT);
		
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
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
	
	public void test_attemptLogin_validEmailAndPasswordNoError() {
		solo.typeText(emailView, VALID_EMAIL);
		solo.typeText(passwordView, VALID_PASSWORD);
		
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				loginButton.performClick();
			}
		});
		
		solo.sleep(500);
		
		assertNull(emailView.getError());
		assertNull(passwordView.getError());
	}
}