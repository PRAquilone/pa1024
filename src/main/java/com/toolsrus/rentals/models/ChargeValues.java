package com.toolsrus.rentals.models;

import com.toolsrus.rentals.db.models.ToolsCharges;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ChargeValues {

    private BigDecimal fullCharge;

    private BigDecimal discountedCharge;

    private Integer chargeDaysCount;

    private ToolsCharges toolsCharge;
}
