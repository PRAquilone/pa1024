package com.toolsrus.rentals.db.repository;

import com.toolsrus.rentals.db.models.RentalAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalAgreementRepository extends JpaRepository<RentalAgreement, Integer> {

    @Query("SELECT rentalId FROM RentalAgreement tr WHERE tr.code = :toolCode AND tr.toolStatus = 'ACTIVE'")
    Long findByCode(@Param("toolCode") String code);

}
