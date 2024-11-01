package com.toolsrus.rentals.db.repository;

import com.toolsrus.rentals.db.models.RentalAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RentalAgreementRepository extends JpaRepository<RentalAgreement, Integer> {

    /**
     * Find an active rental agreement by the given tool code
     *
     * @param code The tool code
     * @return The rental id of the row if found
     */
    @Query("SELECT rentalId FROM RentalAgreement ra WHERE ra.code = :toolCode AND ra.toolStatus = :status")
    Long findByCode(@Param("toolCode") String code, @Param("status") String status);

    /**
     * Update a rental agreement status to CLOSED
     *
     * @param id The rental id to update
     */
    @Modifying
    @Transactional
    @Query("UPDATE RentalAgreement ra SET ra.toolStatus = :status WHERE ra.rentalId = :id")
    void updateStatusById(@Param("id") Long id, @Param("status") String status);

}
