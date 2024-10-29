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
        Mockito.when(data.getToolsFromCode(Mockito.anyString())).thenReturn(Tools.builder().build());
        Mockito.when(data.getToolsChargesFromType(Mockito.anyString())).thenReturn(ToolsCharges.builder().chargesId(1).build());
        service = new ToolRentalService(data, repository);
        RentalRequest request = RentalRequest.builder().code("TEST").build();
        // Act
        ToolsCharges result = PrivateMethodTester.tester(service, "getToolsCharges", request);
        // Assert
        Assertions.assertThat(result).isNotNull();
    }


}