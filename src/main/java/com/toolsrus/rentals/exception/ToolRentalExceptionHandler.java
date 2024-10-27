package com.toolsrus.rentals.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.handler.ExceptionHandlingWebHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ToolRentalExceptionHandler  extends ResponseEntityExceptionHandler {

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

    /**
     * Handle the general tools rental exception
     *
     * @param exception The exception that was thrown
     * @return. The response entity with the error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> illegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity
                .internalServerError()
                .body(exception.getMessage());
    }

}
