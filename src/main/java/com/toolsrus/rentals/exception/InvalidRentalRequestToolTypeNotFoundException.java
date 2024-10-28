package com.toolsrus.rentals.exception;

import java.util.Optional;

/**
 * Discount percent is not valid
 */
public class InvalidRentalRequestToolTypeNotFoundException extends ToolsRentalException {

    public static final String DEFAULT_MESSAGE = "Invalid or empty rental request tool code making it impossible to determine tool type charges.";

    public InvalidRentalRequestToolTypeNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidRentalRequestToolTypeNotFoundException(String message) {
        super(Optional.ofNullable(message).orElse(DEFAULT_MESSAGE));
    }

    public InvalidRentalRequestToolTypeNotFoundException(String message, Throwable cause) {
        super(Optional.ofNullable(message).orElse(DEFAULT_MESSAGE), cause);
    }

    public InvalidRentalRequestToolTypeNotFoundException(Throwable cause) {
        super(cause);
    }

}
