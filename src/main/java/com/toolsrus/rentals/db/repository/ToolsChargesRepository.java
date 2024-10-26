package com.toolsrus.rentals.db.repository;

import com.toolsrus.rentals.db.models.ToolsCharges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolsChargesRepository extends JpaRepository<ToolsCharges, String> {

}
