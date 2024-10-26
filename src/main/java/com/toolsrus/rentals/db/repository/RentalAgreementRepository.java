package com.toolsrus.rentals.db.repository;

import com.toolsrus.rentals.db.models.RentalAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalAgreementRepository extends JpaRepository<RentalAgreement, Integer> {

}
