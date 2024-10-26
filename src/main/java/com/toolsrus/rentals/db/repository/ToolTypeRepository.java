package com.toolsrus.rentals.db.repository;

import com.toolsrus.rentals.db.models.ToolType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolTypeRepository extends JpaRepository<ToolType, String> {

}
