package com.toolsrus.rentals.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ToolRequest {

    private String code;

    private Integer rentalDayCount;

    private BigDecimal discount;

    private LocalDate checkOutDate;


}
