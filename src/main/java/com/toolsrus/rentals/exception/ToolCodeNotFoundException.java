package com.toolsrus.rentals.exception;

import java.util.Optional;

/**
 * Tool is already rented exceptions
 */
public class ToolCodeNotFoundException extends ToolsRentalException {

    public static final String DEFAULT_MESSAGE = "The request appears to not have tool code.";

    public ToolCodeNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public ToolCodeNotFoundException(String message) {
        super(Optional.ofNullable(message).orElse(DEFAULT_MESSAGE));
    }

    public ToolCodeNotFoundException(String message, Throwable cause) {
        super(Optional.ofNullable(message).orElse(DEFAULT_MESSAGE), cause);
    }

    public ToolCodeNotFoundException(Throwable cause) {
        super(cause);
    }

}
