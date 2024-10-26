package com.toolsrus.rentals.db.repository;

import com.toolsrus.rentals.db.models.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, String> {

}
