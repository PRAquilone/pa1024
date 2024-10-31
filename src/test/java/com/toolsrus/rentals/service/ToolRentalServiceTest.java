package com.toolsrus.rentals.service;

import com.toolsrus.rentals.PrivateMethodTester;
import com.toolsrus.rentals.db.component.ToolRentalData;
import com.toolsrus.rentals.db.connector.ToolRentalConnectorImpl;
import com.toolsrus.rentals.db.models.Holiday;
import com.toolsrus.rentals.db.models.HolidayDaysOfWeek;
import com.toolsrus.rentals.db.models.HolidayFrequency;
import com.toolsrus.rentals.db.models.RentalAgreement;
import com.toolsrus.rentals.db.models.ToolStatus;
import com.toolsrus.rentals.db.models.ToolType;
import com.toolsrus.rentals.db.models.Tools;
import com.toolsrus.rentals.db.models.ToolsCharges;
import com.toolsrus.rentals.db.models.Vendors;
import com.toolsrus.rentals.db.repository.DaysRepository;
import com.toolsrus.rentals.db.repository.FrequencyRepository;
import com.toolsrus.rentals.db.repository.HolidayRepository;
import com.toolsrus.rentals.db.repository.RentalAgreementRepository;
import com.toolsrus.rentals.db.repository.ToolStatusRepository;
import com.toolsrus.rentals.db.repository.ToolTypeRepository;
import com.toolsrus.rentals.db.repository.ToolsChargesRepository;
import com.toolsrus.rentals.db.repository.ToolsRepository;
import com.toolsrus.rentals.db.repository.VendorRepository;
import com.toolsrus.rentals.exception.InvalidRentalRequestToolTypeNotFoundException;
import com.toolsrus.rentals.models.ChargeValues;
import com.toolsrus.rentals.models.RentalRequest;
import net.bytebuddy.utility.RandomString;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class ToolRentalServiceTest {


    @Mock
    public DaysRepository daysRepository;
    @Mock
    public FrequencyRepository frequencyRepository;
    @Mock
    public HolidayRepository holidayRepository;
    @Mock
    public ToolsRepository toolsRepository;
    @Mock
    public ToolsChargesRepository toolsChargesRepository;
    @Mock
    public ToolStatusRepository toolStatusRepository;
    @Mock
    public ToolTypeRepository toolTypeRepository;
    @Mock
    public VendorRepository vendorRepository;

    public Random random = new Random(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());

    @Mock
    public ToolRentalConnectorImpl connector;

    @Mock
    public RentalAgreementRepository rentalAgreementRepository;

    @InjectMocks
    public ToolRentalService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        connector = new ToolRentalConnectorImpl(rentalAgreementRepository, createTestData());
        service = new ToolRentalService(connector);
    }


    @Test
    void test_RentalTool_HappyPath() throws Exception {
        // Arrange
        ToolRentalData toolRentalData = createTestData();
        RentalRequest rentalRequest = RentalRequest.builder()
                .code(toolRentalData.getTools().get(0).getCode())
                .checkOutDate(LocalDate.of(2024, 1, 2))
                .discount(BigDecimal.valueOf(random.nextDouble(0.01, 99.99)))
                .rentalDayCount(random.nextInt(1, 10))
                .build();
        Mockito.when(rentalAgreementRepository.findByCode(Mockito.any())).thenReturn(null);
        connector = new ToolRentalConnectorImpl(rentalAgreementRepository, toolRentalData);
        service = new ToolRentalService(connector);
        // Act
        RentalAgreement result = service.rentalTool(rentalRequest);
        // Assert
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void test_BuildFinalCharges_HappyPath() {
        // Arrange
        buildTestingServiceWithData();
        Map<LocalDate, ChargeValues> charges = new HashMap<>();
        ChargeValues values = ChargeValues.builder()
                .fullCharge(BigDecimal.ZERO)
                .toolsCharge(connector.getData().getToolsCharges().get(0))
                .discountedCharge(BigDecimal.ZERO)
                .chargeDaysCount(1)
                .build();
        charges.put(LocalDate.now(), values);
        // Act
        ChargeValues result = PrivateMethodTester.tester(service, "buildFinalCharges", charges);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getFullCharge()).isNotNull();
        Assertions.assertThat(result.getFullCharge()).isEqualTo(values.getFullCharge());
        Assertions.assertThat(result.getDiscountedCharge()).isNotNull();
        Assertions.assertThat(result.getDiscountedCharge()).isEqualTo(values.getDiscountedCharge());
        Assertions.assertThat(result.getChargeDaysCount()).isNotNull();
        Assertions.assertThat(result.getChargeDaysCount()).isEqualTo(values.getChargeDaysCount());
        Assertions.assertThat(result.getToolsCharge()).isNotNull();
        Assertions.assertThat(result.getToolsCharge()).isEqualTo(values.getToolsCharge());
    }

    @Test
    void test_BuildRentalAgreement_HappyPath() {
        // Arrange
        ToolRentalData toolRentalData = buildTestingServiceWithData();
        RentalRequest rentalRequest = RentalRequest.builder()
                .code(toolRentalData.getTools().get(0).getCode())
                .checkOutDate(LocalDate.of(2024, 1, 2))
                .discount(BigDecimal.valueOf(random.nextDouble(0.01, 99.99)))
                .rentalDayCount(random.nextInt(1, 10))
                .build();
        ChargeValues values = ChargeValues.builder()
                .fullCharge(BigDecimal.ZERO)
                .toolsCharge(connector.getData().getToolsCharges().get(0))
                .discountedCharge(BigDecimal.ZERO)
                .chargeDaysCount(1)
                .build();
        // Act
        RentalAgreement result = PrivateMethodTester.tester(service, "buildRentalAgreement", rentalRequest, values, 1l);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getRentalId()).isNotNull();
        Assertions.assertThat(result.getRentalId()).isEqualTo(1);
        Assertions.assertThat(result.getCode()).isNotNull();
        Assertions.assertThat(result.getCode()).isEqualTo(rentalRequest.getCode());
        Assertions.assertThat(result.getType()).isNotNull();
        Assertions.assertThat(result.getType()).isEqualTo(toolRentalData.getTools().get(0).getType());
        Assertions.assertThat(result.getBrand()).isNotNull();
        Assertions.assertThat(result.getBrand()).isEqualTo(toolRentalData.getVendors().get(0).getBrand());
        Assertions.assertThat(result.getRentalDays()).isNotNull();
        Assertions.assertThat(result.getRentalDays()).isEqualTo(rentalRequest.getRentalDayCount());
        Assertions.assertThat(result.getCheckOutDate()).isNotNull();
        Assertions.assertThat(result.getCheckOutDate()).isEqualTo(rentalRequest.getCheckOutDate());
        Assertions.assertThat(result.getDueDate()).isNotNull();
        Assertions.assertThat(result.getDueDate()).isEqualTo(rentalRequest.getCheckOutDate().plusDays(rentalRequest.getRentalDayCount()));
        Assertions.assertThat(result.getChargeDays()).isNotNull();
        Assertions.assertThat(result.getChargeDays()).isEqualTo(values.getChargeDaysCount());
        Assertions.assertThat(result.getDue()).isNotNull();
        Assertions.assertThat(result.getDue()).isEqualTo(values.getDiscountedCharge());
        Assertions.assertThat(result.getDailyCharge()).isNotNull();
        Assertions.assertThat(result.getDailyCharge()).isEqualTo(values.getToolsCharge().getDailyCharge());
        Assertions.assertThat(result.getPreDiscountCharge()).isNotNull();
        Assertions.assertThat(result.getPreDiscountCharge()).isEqualTo(values.getFullCharge());
        Assertions.assertThat(result.getDiscountPercent()).isNotNull();
        Assertions.assertThat(result.getDiscountPercent()).isEqualTo(rentalRequest.getDiscount());
        Assertions.assertThat(result.getDiscountAmount()).isNotNull();
        Assertions.assertThat(result.getDiscountAmount()).isEqualTo(values.getFullCharge().subtract(values.getDiscountedCharge()));
        Assertions.assertThat(result.getFinalCharge()).isNotNull();
        Assertions.assertThat(result.getFinalCharge()).isEqualTo(values.getDiscountedCharge());
        Assertions.assertThat(result.getToolStatus()).isNotNull();
        Assertions.assertThat(result.getToolStatus()).isNotEmpty();
        Assertions.assertThat(result.getToolStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void test_DetermineDayCharges_HappyPath() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        RentalRequest request = RentalRequest.builder()
                .code(data.getTools().get(0).getCode())
                .checkOutDate(LocalDate.of(2024, 1, 2))
                .discount(BigDecimal.valueOf(random.nextDouble(0.01, 99.99)))
                .rentalDayCount(random.nextInt(1, 10))
                .build();
        Map<LocalDate, ChargeValues> charges = new HashMap<>();
        ChargeValues chargeValues = ChargeValues.builder()
                .fullCharge(BigDecimal.ZERO)
                .toolsCharge(connector.getData().getToolsCharges().get(0))
                .discountedCharge(BigDecimal.ZERO)
                .chargeDaysCount(1)
                .build();
        charges.put(LocalDate.now(), chargeValues);
        // Act
        Map<LocalDate, ChargeValues> result = PrivateMethodTester.tester(service, "determineDayCharges", request, charges);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result.get(LocalDate.now())).isNotNull();
        Assertions.assertThat(result.get(LocalDate.now()).getFullCharge()).isNotNull();
        Assertions.assertThat(result.get(LocalDate.now()).getFullCharge()).isEqualTo(data.getToolsCharges().get(0).getDailyCharge());
        Assertions.assertThat(result.get(LocalDate.now()).getChargeDaysCount()).isNotNull();
        Assertions.assertThat(result.get(LocalDate.now()).getChargeDaysCount()).isEqualTo(chargeValues.getChargeDaysCount());
        Assertions.assertThat(result.get(LocalDate.now()).getToolsCharge()).isNotNull();
        Assertions.assertThat(result.get(LocalDate.now()).getToolsCharge()).isEqualTo(chargeValues.getToolsCharge());
    }


    @Test
    void test_GetToolsCharges_HappyPath() throws Exception {
        // Arrange
        ToolRentalData toolRentalData = buildTestingServiceWithData();
        RentalRequest request = RentalRequest.builder().code(toolRentalData.getTools().get(0).getCode()).build();
        // Act
        ToolsCharges result = PrivateMethodTester.tester(service, "getToolsCharges", request);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getChargesId()).isNotNull();
        Assertions.assertThat(result.getChargesId()).isEqualTo(toolRentalData.getToolsCharges().get(0).getChargesId());
        Assertions.assertThat(result.getDailyCharge()).isNotNull();
        Assertions.assertThat(result.getDailyCharge()).isEqualTo(toolRentalData.getToolsCharges().get(0).getDailyCharge());
        Assertions.assertThat(result.getHolidayCharge()).isNotNull();
        Assertions.assertThat(result.getHolidayCharge()).isEqualTo(toolRentalData.getToolsCharges().get(0).getHolidayCharge());
        Assertions.assertThat(result.getWeekDayCharge()).isNotNull();
        Assertions.assertThat(result.getWeekDayCharge()).isEqualTo(toolRentalData.getToolsCharges().get(0).getWeekDayCharge());
        Assertions.assertThat(result.getWeekEndCharge()).isNotNull();
        Assertions.assertThat(result.getWeekEndCharge()).isEqualTo(toolRentalData.getToolsCharges().get(0).getWeekEndCharge());
    }

    @Test
    void test_GetToolsCharges_UnHappyPath_ThrowsException() throws Exception {
        // Arrange
        ToolRentalData toolRentalData = createTestData();
        toolRentalData.setToolsCharges(new ArrayList<>());
        connector = new ToolRentalConnectorImpl(rentalAgreementRepository, toolRentalData);
        service = new ToolRentalService(connector);
        RentalRequest request = RentalRequest.builder().code("TEST").build();
        // Act
        Exception exception = assertThrows(InvalidRentalRequestToolTypeNotFoundException.class, () -> {
            ToolsCharges result = PrivateMethodTester.testerException(service, "getToolsCharges", InvalidRentalRequestToolTypeNotFoundException.class, request);
        });
        // Assert
        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception).isInstanceOf(InvalidRentalRequestToolTypeNotFoundException.class);
        Assertions.assertThat(exception.getMessage()).isNotNull();
        Assertions.assertThat(exception.getMessage()).isEqualTo(InvalidRentalRequestToolTypeNotFoundException.DEFAULT_MESSAGE);
    }

    /**
     * Build the data connector and inject into service for simple test cases
     *
     * @return  Return rental data for any testing needs
     */
    private ToolRentalData buildTestingServiceWithData() {
        ToolRentalData toolRentalData = createTestData();
        connector = new ToolRentalConnectorImpl(rentalAgreementRepository, toolRentalData);
        service = new ToolRentalService(connector);
        return toolRentalData;
    }

    /**
     * Create the test data
     *
     * @return The created object
     */
    private ToolRentalData createTestData() {
        HolidayDaysOfWeek holidayDaysOfWeek = HolidayDaysOfWeek.builder()
                .dayOfWeek("MONDAY")
                .build();
        HolidayFrequency holidayFrequency = HolidayFrequency.builder()
                .frequency("FIRST")
                .build();
        ToolType toolType = ToolType.builder()
                .type(RandomString.make(10))
                .build();
        Vendors vendors = Vendors.builder()
                .brand(RandomString.make(10))
                .build();
        Tools tools = Tools.builder()
                .code(RandomString.make(10))
                .brand(vendors.getBrand())
                .type(toolType.getType())
                .build();
        ToolsCharges toolsCharges = ToolsCharges.builder()
                .chargesId(random.nextInt(1000, 10000))
                .type(toolType.getType())
                .dailyCharge(BigDecimal.valueOf(random.nextDouble(0.01, 999.99)))
                .holidayCharge(random.nextBoolean())
                .weekDayCharge(random.nextBoolean())
                .weekEndCharge(random.nextBoolean())
                .build();
        Holiday holiday = Holiday.builder()
                .name("TEST")
                .holidayMonth(2)
                .holidayDay(9)
                .build();
        ToolStatus toolStatus = ToolStatus.builder()
                .status("ACTIVE")
                .build();
        Mockito.when(daysRepository.findAll()).thenReturn(Lists.newArrayList(holidayDaysOfWeek));
        Mockito.when(frequencyRepository.findAll()).thenReturn(Lists.newArrayList(holidayFrequency));
        Mockito.when(holidayRepository.findAll()).thenReturn(Lists.newArrayList(holiday));
        Mockito.when(toolsRepository.findAll()).thenReturn(Lists.newArrayList(tools));
        Mockito.when(toolsChargesRepository.findAll()).thenReturn(Lists.newArrayList(toolsCharges));
        Mockito.when(toolStatusRepository.findAll()).thenReturn(Lists.newArrayList(toolStatus));
        Mockito.when(toolTypeRepository.findAll()).thenReturn(Lists.newArrayList(toolType));
        Mockito.when(vendorRepository.findAll()).thenReturn(Lists.newArrayList(vendors));
        ToolRentalData data = new ToolRentalData(daysRepository,
                frequencyRepository,
                holidayRepository,
                toolsRepository,
                toolsChargesRepository,
                toolStatusRepository,
                toolTypeRepository,
                vendorRepository);
        data.populateDataFields();
        return data;
    }


}