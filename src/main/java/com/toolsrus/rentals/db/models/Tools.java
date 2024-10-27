package com.toolsrus.rentals.db.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
@Table(name = "Tools" ) // , schema = "TOOLS_R_US_SCHEMA")
public class Tools implements Serializable {

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "type")
    private String type;

    @Column(name = "brand")
    private String brand;

}
