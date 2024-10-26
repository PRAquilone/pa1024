package com.toolsrus.rentals.exception;

import java.util.Optional;

/**
 * Invalid number of rental days
 */
public class RentalDayCountInvalidException extends ToolsRentalException {

    public static final String DEFAULT_MESSAGE = "Rental day count is not 1 or greater.";

    public RentalDayCountInvalidException() {
        super(DEFAULT_MESSAGE);
    }

    public RentalDayCountInvalidException(String message) {
        super(Optional.ofNullable(message).orElse(DEFAULT_MESSAGE));
    }

    public RentalDayCountInvalidException(String message, Throwable cause) {
        super(Optional.ofNullable(message).orElse(DEFAULT_MESSAGE), cause);
    }

    public RentalDayCountInvalidException(Throwable cause) {
        super(cause);
    }

}
