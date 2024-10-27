package com.toolsrus.rentals.controller;

import com.toolsrus.rentals.models.RentalRequest;
import com.toolsrus.rentals.models.RentalResponse;
import com.toolsrus.rentals.service.ToolRentalService;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<RentalResponse> rentTool(@RequestBody RentalRequest request) {
        log.info("Request " + Optional.ofNullable(request).map(RentalRequest::toString).orElse("Empty Request"));
        RentalResponse rentalResponse = toolRentalService.rentalTool(request);
        log.info("Response " + Optional.ofNullable(rentalResponse).map(RentalResponse::toString).orElse("Empty Response"));
        return Optional.ofNullable(rentalResponse)
                .filter(rr -> !rr.getStatus().isError())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.internalServerError().body(rentalResponse));
    }
}
