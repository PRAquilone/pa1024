package com.toolsrus.rentals.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ToolRentalExceptionHandler {

    /**
     * Handle the general tools rental exception
     *
     * @param exception The exception that was thrown
     * @return. The response entity with the error
     */
    @ExceptionHandler(ToolsRentalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> toolsRentalException(ToolsRentalException exception) {
        return ResponseEntity
                .internalServerError()
                .body(exception.getMessage());
    }

}
