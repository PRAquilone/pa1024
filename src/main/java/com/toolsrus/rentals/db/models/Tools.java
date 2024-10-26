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
@Table(name = "tools")
public class Tools implements Serializable {

    @Id
    @Column(name = "toolCode")
    private String code;

    @Column(name = "toolType")
    private String type;

    @Column(name = "toolBrand")
    private String brand;

}
