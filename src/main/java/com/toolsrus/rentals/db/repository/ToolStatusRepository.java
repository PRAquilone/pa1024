package com.toolsrus.rentals.db.repository;

import com.toolsrus.rentals.db.models.ToolStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolStatusRepository extends JpaRepository<ToolStatus, String> {

}
