package com.toolsrus.rentals.db.repository;

import com.toolsrus.rentals.db.models.HolidayDaysOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DaysRepository extends JpaRepository<HolidayDaysOfWeek, String> {

}
