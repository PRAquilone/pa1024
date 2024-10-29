package com.toolsrus.rentals.service;

import com.toolsrus.rentals.PrivateMethodTester;
import com.toolsrus.rentals.component.ToolRentalData;
import com.toolsrus.rentals.db.models.Tools;
import com.toolsrus.rentals.db.models.ToolsCharges;
import com.toolsrus.rentals.db.repository.RentalAgreementRepository;
import com.toolsrus.rentals.models.RentalRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;


@ExtendWith(MockitoExtension.class)
class ToolRentalServiceTest {

    @Mock
    public ToolRentalData data;

    @Mock
    public RentalAgreementRepository repository;

    @InjectMocks
    public ToolRentalService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ToolRentalService(data, repository);
    }

    @Test
    void rentalTool() {
    }

    @Test
    void testGetToolsCharges() throws Exception {
        // Arrange
        ToolsCharges toolsCharges = ToolsCharges.builder()
                .chargesId(1)
                .dailyCharge(BigDecimal.ONE)
                .holidayCharge(true)
                .weekDayCharge(true)
                .weekEndCharge(true)
                .build();
        Mockito.when(data.getToolsFromCode(Mockito.any())).thenReturn(Tools.builder().build());
        Mockito.when(data.getToolsChargesFromType(Mockito.any())).thenReturn(toolsCharges);
        service = new ToolRentalService(data, repository);
        RentalRequest request = RentalRequest.builder().code("TEST").build();
        // Act
        ToolsCharges result = PrivateMethodTester.tester(service, "getToolsCharges", request);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getChargesId()).isNotNull();
        Assertions.assertThat(result.getChargesId()).isEqualTo(1);
        Assertions.assertThat(result.getDailyCharge()).isNotNull();
        Assertions.assertThat(result.getDailyCharge()).isEqualTo(BigDecimal.ONE);
        Assertions.assertThat(result.getHolidayCharge()).isNotNull();
        Assertions.assertThat(result.getHolidayCharge()).isEqualTo(true);
        Assertions.assertThat(result.getWeekDayCharge()).isNotNull();
        Assertions.assertThat(result.getWeekDayCharge()).isEqualTo(true);
        Assertions.assertThat(result.getWeekEndCharge()).isNotNull();
        Assertions.assertThat(result.getWeekEndCharge()).isEqualTo(true);
    }


}