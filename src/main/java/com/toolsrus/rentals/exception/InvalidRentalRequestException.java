package com.toolsrus.rentals.exception;

import java.util.Optional;

/**
 * Discount percent is not valid
 */
public class InvalidRentalRequestException extends ToolsRentalException {

    public static final String DEFAULT_MESSAGE = "Invalid or empty rental request.";

    public InvalidRentalRequestException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidRentalRequestException(String message) {
        super(Optional.ofNullable(message).orElse(DEFAULT_MESSAGE));
    }

    public InvalidRentalRequestException(String message, Throwable cause) {
        super(Optional.ofNullable(message).orElse(DEFAULT_MESSAGE), cause);
    }

    public InvalidRentalRequestException(Throwable cause) {
        super(cause);
    }

}
