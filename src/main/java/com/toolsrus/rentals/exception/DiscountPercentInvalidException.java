package com.toolsrus.rentals.exception;

import java.util.Optional;

/**
 * Discount percent is not valid
 */
public class DiscountPercentInvalidException extends ToolsRentalException {

    public static final String DEFAULT_MESSAGE = "Discount percent is not in the range 0-100.";

    public DiscountPercentInvalidException() {
        super(DEFAULT_MESSAGE);
    }

    public DiscountPercentInvalidException(String message) {
        super(Optional.ofNullable(message).orElse(DEFAULT_MESSAGE));
    }

    public DiscountPercentInvalidException(String message, Throwable cause) {
        super(Optional.ofNullable(message).orElse(DEFAULT_MESSAGE), cause);
    }

    public DiscountPercentInvalidException(Throwable cause) {
        super(cause);
    }

}
