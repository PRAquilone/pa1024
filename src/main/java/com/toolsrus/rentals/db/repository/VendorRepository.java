package com.toolsrus.rentals.db.repository;

import com.toolsrus.rentals.db.models.Vendors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<Vendors, String> {

}
