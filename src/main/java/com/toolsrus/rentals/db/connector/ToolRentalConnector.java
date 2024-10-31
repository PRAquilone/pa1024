package com.toolsrus.rentals.db.connector;

import com.toolsrus.rentals.db.component.ToolRentalData;
import com.toolsrus.rentals.db.models.RentalAgreement;

public interface ToolRentalConnector {

    /**
     * Get the preloaded data
     *
     * @return The loaded data
     */
    ToolRentalData getData();

    /**
     * Get the total number of rows in the agreement database
     *
     * @return The count
     */
    Long getTotalAgreements();

    /**
     * Save a new rental agreement
     *
     * @param rentalAgreement The rental agreement
     */
    void saveRentalAgreement(RentalAgreement rentalAgreement);

    /**
     * Find the rental agreement by code
     *
     * @param code The code to find
     * @return The id of the rental agreement or null
     */
    Long findRentalAgreementByCode(String code);


}
