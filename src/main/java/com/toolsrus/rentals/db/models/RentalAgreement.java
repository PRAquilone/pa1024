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
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "rental")
public class RentalAgreement implements Serializable {

    @Id
    @Column(name = "rentalId")
    private Integer rentalId;

    @OneToOne
    private Tools code;

    @OneToOne
    private ToolType type;

    @OneToOne
    private Vendors brand;

    @Column(name = "rentalDays")
    private Integer rentalDays;

    @Column(name = "checkOutDate")
    private LocalDate checkOutDate;

    @Column(name = "dueDate")
    private LocalDate dueDate;

    @Column(name = "chargeDays")
    private Integer chargeDays;

    @Column(name = "due")
    private BigDecimal due;

    @Column(name = "dailyCharge")
    private BigDecimal dailyCharge;

    @Column(name = "preDiscountCharge")
    private BigDecimal preDiscountCharge;

    @Column(name = "discountPercent")
    private BigDecimal discountPercent;

    @Column(name = "discountAmount")
    private BigDecimal discountAmount;

    @Column(name = "finalCharge")
    private BigDecimal finalCharge;

    @OneToOne
    private ToolStatus toolStatus;

}
