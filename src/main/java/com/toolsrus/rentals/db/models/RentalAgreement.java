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
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "Tool_Rentals") // , schema = "TOOLS_R_US_SCHEMA")
public class RentalAgreement implements Serializable {

    @Id
    @Column(name = "id")
    private Integer rentalId;

    @Column(name = "code")
    private String code;

    @Column(name = "type")
    private String type;

    @Column(name = "brand")
    private String brand;

    @Column(name = "rental_days")
    private Integer rentalDays;

    @Column(name = "checkout_date")
    private LocalDate checkOutDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "charge_days")
    private Integer chargeDays;

    @Column(name = "due")
    private BigDecimal due;

    @Column(name = "daily_charge")
    private BigDecimal dailyCharge;

    @Column(name = "pre_discount_charge")
    private BigDecimal preDiscountCharge;

    @Column(name = "discount_percent")
    private BigDecimal discountPercent;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "final_charge")
    private BigDecimal finalCharge;

    @Column(name = "status")
    private String toolStatus;

}
