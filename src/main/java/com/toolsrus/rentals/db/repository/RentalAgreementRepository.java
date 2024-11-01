package com.toolsrus.rentals.db.repository;

import com.toolsrus.rentals.db.models.RentalAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalAgreementRepository extends JpaRepository<RentalAgreement, Integer> {

    /**
     * Find an active rental agreement by the given tool code
     *
     * @param code The tool code
     * @return The rental id of the row if found
     */
    @Query("SELECT rentalId FROM RentalAgreement tr WHERE tr.code = :toolCode AND tr.toolStatus = 'ACTIVE'")
    Long findByCode(@Param("toolCode") String code);

    /**
     * Update a rental agreement status to CLOSED
     *
     * @param id The rental id to update
     */
    @Query("UPDATE RentalAgreement tr SET tr.toolStatus = 'CLOSED' WHERE tr.rentalId = :id")
    void updateStatusById(Long id);

}
