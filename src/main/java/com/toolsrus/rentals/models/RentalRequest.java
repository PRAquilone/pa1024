package com.toolsrus.rentals.models;

import com.toolsrus.rentals.db.models.Tools;
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
public class RentalRequest {

    private Tools code;

    private Integer rentalDayCount;

    private BigDecimal discount;

    private LocalDate checkOutDate;


}
