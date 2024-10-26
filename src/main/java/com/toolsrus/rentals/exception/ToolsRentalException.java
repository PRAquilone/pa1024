package com.toolsrus.rentals.exception;

import java.util.Optional;

/**
 * This wraps all the custom exceptions
 */
public class ToolsRentalException extends Exception {

    public static final String DEFAULT_MESSAGE = "Tools Rental Unknown Exception Encountered";

    public ToolsRentalException() {
        super(DEFAULT_MESSAGE);
    }

    public ToolsRentalException(String message) {
        super(Optional.ofNullable(message).orElse(DEFAULT_MESSAGE));
    }

    public ToolsRentalException(String message, Throwable cause) {
        super(Optional.ofNullable(message).orElse(DEFAULT_MESSAGE), cause);
    }

    public ToolsRentalException(Throwable cause) {
        super(cause);
    }

    protected ToolsRentalException(String message,
                                   Throwable cause,
                                   boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(Optional.ofNullable(message).orElse(DEFAULT_MESSAGE), cause, enableSuppression, writableStackTrace);
    }

}
