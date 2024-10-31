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

    /**
     * Create a good response
     *
     * @param status    The status to send
     * @param agreement The rental agreement
     * @return The created response
     */
    public static RentalResponse ok(RentalAgreement agreement) {
        return RentalResponse.builder()
                .status(HttpStatus.OK)
                .message("SUCCESS")
                .agreement(agreement)
                .build();
    }

    /**
     * Create an error response
     *
     * @param status  The status to send
     * @param message The error message
     * @return The created response
     */
    public static RentalResponse error(HttpStatus status, String message) {
        return RentalResponse.builder()
                .status(status)
                .message(message)
                .build();
    }

}
