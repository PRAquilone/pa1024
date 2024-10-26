package com.toolsrus.rentals.models;

import com.toolsrus.rentals.db.models.RentalAgreement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RentalResponse {

    private RentalAgreement agreement;

    private String message;

    private HttpStatus status;

}
