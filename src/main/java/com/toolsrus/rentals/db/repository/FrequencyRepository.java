package com.toolsrus.rentals.db.repository;

import com.toolsrus.rentals.db.models.Frequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FrequencyRepository extends JpaRepository<Frequency, String> {

}
