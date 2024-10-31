package com.toolsrus.rentals;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.commons.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * This class is to test a private method
 */
@SuppressWarnings("unchecked")
public class PrivateMethodTester {

    /**
     * This is the main method to test the private method
     *
     * @param instance   The instance object we are testing
     * @param methodName The name of the method
     * @param params     The parameters for the method
     * @param <T>        The type of the return object
     * @return The return object from the method
     */
    @SneakyThrows
    public static <T> T tester(Object instance, String methodName, Object... params) {
        return testerException(instance, methodName, null, params);
    }

    /**
     * This is the main method to test the private method with an expected exception
     *
     * @param instance          The instance object we are testing
     * @param methodName        The name of the method
     * @param exceptedException The expected exception so that it does not re-wrap it in a runtime
     * @param params            The parameters for the method
     * @param <T>               The type of the return object
     * @param <E>               The expected exception class
     * @return The return object from the method
     */
    @SneakyThrows
    public static <T, E extends Exception> T testerException(Object instance, String methodName, Class<E> exceptedException, Object... params) {
        verifyDataForTest(instance, methodName);
        Method method = getMethod(instance, methodName);
        return executeTestMethod(instance, exceptedException, method, params);
    }

    /**
     * Execute the testing of the method
     *
     * @param instance The instance object we are testing
     * @param params   The parameters for the method
     * @param method   The method to test
     * @param <T>      The type of the return object
     * @param <E>      The expected exception class
     * @return The return object from the method
     */
    private static <T, E extends Exception> T executeTestMethod(Object instance, Class<E> exceptedException, Method method, Object... params) throws Throwable {
        try {
            method.setAccessible(true);
            return (T) method.invoke(instance, params);
        } catch (Exception exception) {

            // Check if we have the expected exception and just re-throw it if we do
            Throwable matchedException = containsExpectedException(exception, exceptedException);
            if (Optional.ofNullable(matchedException).isPresent()) {
                throw matchedException;

                // If we do not have the expected exception then assert fail
            } else {
                Assertions.fail("Unable to test due to " + exception.getMessage(), exception);
            }
        }
        return null;
    }

    /**
     * Determine if in the stack trace we have the expected exception
     *
     * @param exception         The exception that was thrown
     * @param expectedException The expected exception
     * @param <E>               The expected exception class
     * @return Expected exception object found or null
     */
    private static <E extends Exception> Throwable containsExpectedException(Throwable exception, Class<E> expectedException) {
        if ((Optional.ofNullable(expectedException).isEmpty()) ||
                (StringUtils.isBlank(expectedException.getName())) ||
                (Optional.ofNullable(exception).isEmpty())) {
            return null;
        }
        if (expectedException.getName().contains(exception.getClass().getSimpleName())) {
            return exception;
        }
        if (Optional.ofNullable(exception.getCause()).isEmpty()) {
            return null;
        }
        if (exception.getClass().getName().equalsIgnoreCase(exception.getCause().getClass().getName())) {
            return null;
        }
        return containsExpectedException(exception.getCause(), expectedException);
    }

    /**
     * Verify that we have data for testing
     * Will assert fail if data not present
     *
     * @param instance   The instance object we are testing
     * @param methodName The name of the method
     */
    private static void verifyDataForTest(Object instance, String methodName) {
        if (Optional.ofNullable(instance).isEmpty()) {
            Assertions.fail("PrivateMethodTester: Unable to test because instance is empty");
        }
        if (StringUtils.isBlank(methodName)) {
            Assertions.fail("PrivateMethodTester: Method name not provided");
        }
    }

    /**
     * Get the method object from the name and the instance
     * Will assert fail if can not find method
     *
     * @param instance   The instance object we are testing
     * @param methodName The name of the method
     * @return The method object
     */
    private static Method getMethod(Object instance, String methodName) {
        Method[] methodList = instance.getClass().getDeclaredMethods();
        Method returnValue = null;
        for (Method meth : methodList) {
            if (meth.getName().equalsIgnoreCase(methodName)) {
                returnValue = meth;
                break;
            }
        }
        if (Optional.ofNullable(returnValue).isEmpty()) {
            Assertions.fail("PrivateMethodTester: Method " + methodName + " could not be found in " + instance.getClass().getName());
        }
        return returnValue;
    }

}
