package com.example.budgetmanager.test;

import com.example.budgetmanager.api.ApiInterface;
import com.loopj.android.http.AsyncHttpClient;

import java.lang.reflect.Field;

/**
 * A collection of miscellaneous methods for tests.
 *
 * @author Chris brucec5
 * @author Graham grahamb5
 *
 */
public class TestUtilities {
	/**
     * Use reflection to change value of any instance field.
     *
     * @param classInstance An Object instance.
     * @param fieldName The name of a field in the class instantiated by classInstancee
     * @param newValue The value you want the field to be set to.
     */
    public static void setInstanceValue(final Object classInstance, final String fieldName, final Object newValue) throws SecurityException,
            NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        // Get the private field
        final Field field = classInstance.getClass().getDeclaredField(fieldName);
        // Allow modification on the field
        field.setAccessible(true);
        // Sets the field to the new value for this instance
        field.set(classInstance, newValue);
    }

    /**
     * Use reflection to change value of any static field.
     * @param className The complete name of the class (ex. java.lang.String)
     * @param fieldName The name of a static field in the class
     * @param newValue The value you want the field to be set to.
     */
    public static void setStaticValue(final String className, final String fieldName, final Object newValue) throws SecurityException, NoSuchFieldException,
            ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        // Get the private String field
        final Field field = Class.forName(className).getDeclaredField(fieldName);
        // Allow modification on the field
        field.setAccessible(true);
        // Get
        final Object oldValue = field.get(Class.forName(className));
        // Sets the field to the new value
        field.set(oldValue, newValue);
    }

    /**
     * Use reflection to make an ApiInterface instance with a stubbed out HTTP client.
     * @param testClient The AsyncHttpClient stub to inject
     * @return an ApiInterface instance with stubbed out HTTP client..
     */
    public static ApiInterface getStubbedApiInterface(AsyncHttpClient testClient) {
    	ApiInterface api = ApiInterface.getInstance();

		try {
			// API's client field is private. Lets use
			// reflection to change it to our test client.
			TestUtilities.setInstanceValue(api, "client", testClient);
		} catch (Exception e) { }

		return api;
    }
}
