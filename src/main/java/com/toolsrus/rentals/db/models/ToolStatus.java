package com.toolsrus.rentals.db.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "Tool_Status") // , schema = "TOOLS_R_US_SCHEMA")
public class ToolStatus implements Serializable {

    @Id
    @Column(name = "status")
    private String status;

}
