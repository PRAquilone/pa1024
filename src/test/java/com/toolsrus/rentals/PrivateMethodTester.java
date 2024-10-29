package com.toolsrus.rentals;

import org.junit.platform.commons.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Optional;

public class PrivateMethodTester {

    public static <T> T tester(Object instance, String methodName, Object... params) {
        verifyDataForTest(instance, methodName);
        Method method = getMethod(instance, methodName);
        try {
            return (T) method.invoke(instance, params);
        } catch (Exception exception) {
            throw new RuntimeException("Unable to test due to " + exception.getMessage(), exception);
        }
    }

    private static void verifyDataForTest(Object instance, String methodName) {
        if (Optional.ofNullable(instance).isEmpty()) {
            throw new RuntimeException("PrivateMethodTester: Unable to test because instance is empty");
        }
        if (StringUtils.isBlank(methodName)) {
            throw new RuntimeException("PrivateMethodTester: Method name not provided");
        }
    }

    private static Method getMethod(Object instance, String methodName) {
        Method[] methodList = instance.getClass().getDeclaredMethods();
        Method returnValue = null;
        for(Method meth : methodList) {
            if (meth.getName().equalsIgnoreCase(methodName)) {
                returnValue = meth;
                break;
            }
        }
        if (Optional.ofNullable(returnValue).isEmpty()) {
            throw new RuntimeException("PrivateMethodTester: Method " + methodName + " could not be found in " + instance.getClass().getName());
        }
        return returnValue;
    }
}
