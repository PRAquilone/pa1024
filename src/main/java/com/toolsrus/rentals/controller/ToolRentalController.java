package com.toolsrus.rentals.controller;

import com.toolsrus.rentals.db.models.RentalAgreement;
import com.toolsrus.rentals.exception.DiscountPercentInvalidException;
import com.toolsrus.rentals.exception.InvalidRentalRequestException;
import com.toolsrus.rentals.exception.InvalidRentalRequestToolTypeNotFoundException;
import com.toolsrus.rentals.exception.RentalDayCountInvalidException;
import com.toolsrus.rentals.exception.ToolAlreadyRentedException;
import com.toolsrus.rentals.exception.ToolCodeNotFoundException;
import com.toolsrus.rentals.models.RentalResponse;
import com.toolsrus.rentals.models.ToolRequest;
import com.toolsrus.rentals.service.ToolRentalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping
public class ToolRentalController {

    // The service class for this application
    private final ToolRentalService toolRentalService;

    /**
     * The constructor
     *
     * @param service the service class
     */
    public ToolRentalController(ToolRentalService service) {
        this.toolRentalService = service;
    }

    /**
     * Rent a tool
     *
     * @param request The rental request
     * @return The response entity
     */
    @PostMapping("/tools/rental/rent")
    public ResponseEntity<RentalResponse> rentTool(@RequestBody ToolRequest request) throws Exception {
        log.info("Request {}", Optional.ofNullable(request).map(ToolRequest::toString).orElse("Empty Request"));
        try {
            return callToCreateRentalAgreement(request);
        } catch (Exception exception) {
            return buildErrorResponse(exception);
        }
    }

    /**
     * Rent a tool
     *
     * @param request The rental request
     * @return The response entity
     */
    @PostMapping("/tools/rental/rent/return")
    public ResponseEntity<RentalResponse> returnRental(@RequestBody ToolRequest request) throws Exception {
        log.info("Request {}", Optional.ofNullable(request).map(ToolRequest::toString).orElse("Empty Request"));
        try {
            return callToReturnRental(request);
        } catch (Exception exception) {
            return buildErrorResponse(exception);
        }
    }

    /**
     * Build an error response
     *
     * @param exception The exception encountered
     * @return The error response entity
     */
    private ResponseEntity<RentalResponse> buildErrorResponse(Exception exception) {
        return ResponseEntity.internalServerError()
                .body(RentalResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .message(exception.getMessage())
                        .build());
    }

    /**
     * Call the service and attempt to return a rental
     *
     * @param request The request object
     * @return A message display success
     * @throws InvalidRentalRequestException Exception that can be thrown
     * @throws ToolCodeNotFoundException     Exception that can be thrown
     */
    private ResponseEntity<RentalResponse> callToReturnRental(ToolRequest request) throws InvalidRentalRequestException, ToolCodeNotFoundException {
        toolRentalService.returnRentalTool(request);
        RentalResponse rentalResponse = RentalResponse.success("Successfully returned tool " + request.getCode());
        log.info("Response {}", Optional.ofNullable(rentalResponse).map(RentalResponse::toString).orElse("Empty Response"));
        return Optional.ofNullable(rentalResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.internalServerError().body(rentalResponse));
    }

    /**
     * Call the service and attempt to create a rental
     *
     * @param request The request object
     * @return A rental agreement response entity
     * @throws InvalidRentalRequestException   Exception that can be thrown
     * @throws RentalDayCountInvalidException  Exception that can be thrown
     * @throws DiscountPercentInvalidException Exception that can be thrown
     * @throws ToolAlreadyRentedException      Exception that can be thrown
     */
    private ResponseEntity<RentalResponse> callToCreateRentalAgreement(ToolRequest request) throws InvalidRentalRequestException, RentalDayCountInvalidException, DiscountPercentInvalidException, ToolAlreadyRentedException, InvalidRentalRequestToolTypeNotFoundException {
        RentalAgreement agreement = toolRentalService.rentalTool(request);
        RentalResponse rentalResponse = RentalResponse.ok(agreement);
        log.info("Response {}", Optional.ofNullable(rentalResponse).map(RentalResponse::toString).orElse("Empty Response"));
        return Optional.ofNullable(rentalResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.internalServerError().body(rentalResponse));
    }

}
