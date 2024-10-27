package com.toolsrus.rentals.service;

import com.toolsrus.rentals.component.ToolRentalData;
import com.toolsrus.rentals.db.models.RentalAgreement;
import com.toolsrus.rentals.models.RentalRequest;
import com.toolsrus.rentals.models.RentalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ToolRentalService {

    private final ToolRentalData data;

    public ToolRentalService(ToolRentalData data) {
        this.data = data;
    }


    /**
     * Rent a tool if possible
     *
     * @param request The rental request
     * @return The rental agreement if the tool can be rented
     */
    public RentalResponse rentalTool(RentalRequest request) {
        data.populateDataFields();
        RentalResponse response = null;
        try {
            RentalAgreement agreement = RentalAgreement.builder()
                    .toolStatus(data.getStatuses().get(0).getStatus())
                    .type(data.getTypes().get(0).getType())
                    .code(data.getTools().get(0).getCode())
                    .build();
            response = RentalResponse.builder()
                    .status(HttpStatus.OK)
                    .agreement(agreement)
                    .message(Optional.ofNullable(agreement).map(x -> "Rental Agreement Attached").orElse("Unable to rent tool"))
                    .build();
        } catch (Exception exception) {
            response = RentalResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(exception.getMessage())
                    .build();
        }
        return response;
    }

}
