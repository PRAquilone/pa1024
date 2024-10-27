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
@Table(name = "Holidays") // , schema = "TOOLS_R_US_SCHEMA")
public class Holiday implements Serializable {

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "holiday_month")
    private Integer holidayMonth;

    @Column(name = "holiday_day")
    private Integer holidayDay;

    @Column(name = "day_of_the_week")
    private String dayOfTheWeek;

    @Column(name = "frequency")
    private String holidayFrequency;

}
