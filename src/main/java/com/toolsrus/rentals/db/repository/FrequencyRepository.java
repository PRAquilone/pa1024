package com.toolsrus.rentals.db.repository;

import com.toolsrus.rentals.db.models.HolidayFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FrequencyRepository extends JpaRepository<HolidayFrequency, String> {

}
