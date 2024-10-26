package com.toolsrus.rentals.exception;

import java.util.Optional;

/**
 * Tool is already rented exceptions
 */
public class ToolAlreadyRentedException extends ToolsRentalException {

    public static final String DEFAULT_MESSAGE = "The tool is already rented and unable to be rented at this time.";

    public ToolAlreadyRentedException() {
        super(DEFAULT_MESSAGE);
    }

    public ToolAlreadyRentedException(String message) {
        super(Optional.ofNullable(message).orElse(DEFAULT_MESSAGE));
    }

    public ToolAlreadyRentedException(String message, Throwable cause) {
        super(Optional.ofNullable(message).orElse(DEFAULT_MESSAGE), cause);
    }

    public ToolAlreadyRentedException(Throwable cause) {
        super(cause);
    }

}
