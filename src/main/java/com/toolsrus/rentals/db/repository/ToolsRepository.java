package com.toolsrus.rentals.db.repository;

import com.toolsrus.rentals.db.models.Tools;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolsRepository extends JpaRepository<Tools, String> {

}
