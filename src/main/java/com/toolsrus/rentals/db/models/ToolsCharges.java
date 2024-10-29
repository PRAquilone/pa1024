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

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "Tools_Charges") // , schema = "TOOLS_R_US_SCHEMA")
public class ToolsCharges implements Serializable {

    @Id
    @Column(name = "id")
    private Integer chargesId;

    @Column(name = "type")
    private String type;

    @Column(name = "daily_charge")
    private BigDecimal dailyCharge;

    @Column(name = "weekday_charge")
    private Boolean weekDayCharge;

    @Column(name = "weekend_charge")
    private Boolean weekEndCharge;

    @Column(name = "holiday_charge")
    private Boolean holidayCharge;

}
