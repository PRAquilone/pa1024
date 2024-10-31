package com.toolsrus.rentals.db.connector;

import com.toolsrus.rentals.db.component.ToolRentalData;
import com.toolsrus.rentals.db.models.RentalAgreement;
import com.toolsrus.rentals.db.repository.RentalAgreementRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ToolRentalConnectorImpl implements ToolRentalConnector {

    private final RentalAgreementRepository rentalAgreementRepository;
    private final ToolRentalData data;

    public ToolRentalConnectorImpl(RentalAgreementRepository rentalAgreementRepository, ToolRentalData data) {
        this.rentalAgreementRepository = rentalAgreementRepository;
        this.data = data;
    }


    /**
     * Get the preloaded data
     *
     * @return The loaded data
     */
    @Override
    public ToolRentalData getData() {
        data.populateDataFields();
        return data;
    }

    /**
     * Get the total number of rows in the agreement database
     *
     * @return The count
     */
    @Override
    public Long getTotalAgreements() {
        return rentalAgreementRepository.count();
    }

    /**
     * Save a new rental agreement
     *
     * @param rentalAgreement The rental agreement
     */
    @Override
    public void saveRentalAgreement(RentalAgreement rentalAgreement) {
        if (Optional.ofNullable(rentalAgreement).isPresent()) {
            rentalAgreementRepository.save(rentalAgreement);
        }
    }

    /**
     * Find the rental agreement by code
     *
     * @param code The code to find
     * @return The id of the rental agreement or null
     */
    @Override
    public Long findRentalAgreementByCode(String code) {
        Long foundId = null;
        if (StringUtils.isNotBlank(code)) {
            foundId = rentalAgreementRepository.findByCode(code);
        }
        return foundId;
    }
}
