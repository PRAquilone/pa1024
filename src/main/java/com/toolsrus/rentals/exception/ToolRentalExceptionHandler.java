package com.toolsrus.rentals.exception;

import com.toolsrus.rentals.models.RentalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ToolRentalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle the general tools rental exception
     *
     * @param exception The exception that was thrown
     * @return. The response entity with the error
     */
    @ExceptionHandler(ToolsRentalException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<RentalResponse> toolsRentalException(ToolsRentalException exception) {
        return ResponseEntity
                .internalServerError()
                .body(RentalResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage()));
    }

    /**
     * Handle the general discount percent invalid
     *
     * @param exception The exception that was thrown
     * @return. The response entity with the error
     */
    @ExceptionHandler(DiscountPercentInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RentalResponse> discountPercentInvalidException(DiscountPercentInvalidException exception) {
        return ResponseEntity
                .badRequest()
                .body(RentalResponse.error(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    /**
     * Handle the general invalid rental request
     *
     * @param exception The exception that was thrown
     * @return. The response entity with the error
     */
    @ExceptionHandler(InvalidRentalRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RentalResponse> invalidRentalRequestException(InvalidRentalRequestException exception) {
        return ResponseEntity
                .badRequest()
                .body(RentalResponse.error(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    /**
     * Handle the general invalid rental request tool type not found
     *
     * @param exception The exception that was thrown
     * @return. The response entity with the error
     */
    @ExceptionHandler(InvalidRentalRequestToolTypeNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RentalResponse> invalidRentalRequestToolTypeNotFoundException(InvalidRentalRequestToolTypeNotFoundException exception) {
        return ResponseEntity
                .badRequest()
                .body(RentalResponse.error(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    /**
     * Handle the general rental day count invalid
     *
     * @param exception The exception that was thrown
     * @return. The response entity with the error
     */
    @ExceptionHandler(RentalDayCountInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RentalResponse> rentalDayCountInvalidException(RentalDayCountInvalidException exception) {
        return ResponseEntity
                .badRequest()
                .body(RentalResponse.error(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    /**
     * Handle the general tool already rented
     *
     * @param exception The exception that was thrown
     * @return. The response entity with the error
     */
    @ExceptionHandler(ToolAlreadyRentedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RentalResponse> toolAlreadyRentedException(ToolAlreadyRentedException exception) {
        return ResponseEntity
                .badRequest()
                .body(RentalResponse.error(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }


}
