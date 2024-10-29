package com.toolsrus.rentals;

import org.junit.jupiter.api.Assertions;
import org.junit.platform.commons.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * This class is to test a private method
 */
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
    public static <T> T tester(Object instance, String methodName, Object... params) {
        verifyDataForTest(instance, methodName);
        Method method = getMethod(instance, methodName);
        return executeTestMethod(instance, params, method);
    }

    /**
     * Execute the testing of the method
     *
     * @param instance The instance object we are testing
     * @param params   The parameters for the method
     * @param method   The method to test
     * @param <T>      The type of the return object
     * @return The return object from the method
     */
    private static <T> T executeTestMethod(Object instance, Object[] params, Method method) {
        try {
            method.setAccessible(true);
            return (T) method.invoke(instance, params);
        } catch (Exception exception) {
            throw new RuntimeException("Unable to test due to " + exception.getMessage(), exception);
        }
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
