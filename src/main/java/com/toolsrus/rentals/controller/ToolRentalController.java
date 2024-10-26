package com.toolsrus.rentals.controller;

import com.toolsrus.rentals.models.RentalRequest;
import com.toolsrus.rentals.models.RentalResponse;
import com.toolsrus.rentals.service.ToolRentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController("/tools/rental")
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
    @PostMapping("/rent")
    public ResponseEntity<RentalResponse> rentTool(@RequestBody RentalRequest request) {
        RentalResponse rentalResponse = toolRentalService.rentalTool(request);
        return Optional.ofNullable(rentalResponse)
                .filter(rr -> !rr.getStatus().isError())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.internalServerError().body(rentalResponse));
    }
}
