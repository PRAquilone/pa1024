package com.toolsrus.rentals.db.repository;

import com.toolsrus.rentals.db.models.Days;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DaysRepository extends JpaRepository<Days, String> {

}
