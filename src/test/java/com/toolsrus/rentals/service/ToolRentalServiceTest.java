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
import com.toolsrus.rentals.exception.DiscountPercentInvalidException;
import com.toolsrus.rentals.exception.InvalidRentalRequestException;
import com.toolsrus.rentals.exception.InvalidRentalRequestToolTypeNotFoundException;
import com.toolsrus.rentals.exception.RentalDayCountInvalidException;
import com.toolsrus.rentals.exception.ToolAlreadyRentedException;
import com.toolsrus.rentals.exception.ToolCodeNotFoundException;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
    void test_returnRentalTool_HappyPath() throws Exception {
        // Arrange
        ToolRentalData data = buildTestingServiceWithDataAndFindingCode(null);
        RentalRequest rentalRequest = RentalRequest.builder()
                .code(data.getTools().get(0).getCode())
                .checkOutDate(LocalDate.of(2024, 1, 2))
                .discount(BigDecimal.valueOf(random.nextDouble(0.01, 99.99)))
                .rentalDayCount(random.nextInt(1, 10))
                .build();
        // Act Part 1
        RentalAgreement result = service.rentalTool(rentalRequest);
        // Assert Part 1
        Assertions.assertThat(result).isNotNull();
        // Arrange Part 2
        rentalRequest = RentalRequest.builder()
                .code(data.getTools().get(0).getCode())
                .build();
        // Act Part 2
        service.returnRentalTool(rentalRequest);
        // Assert Part 2
        assertTrue(true, "We successfully returned a rental");
    }

    @Test
    void test_RentalTool_HappyPath() throws Exception {
        // Arrange
        ToolRentalData data = buildTestingServiceWithDataAndFindingCode(null);
        RentalRequest rentalRequest = RentalRequest.builder()
                .code(data.getTools().get(0).getCode())
                .checkOutDate(LocalDate.of(2024, 1, 2))
                .discount(BigDecimal.valueOf(random.nextDouble(0.01, 99.99)))
                .rentalDayCount(random.nextInt(1, 10))
                .build();
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
        RentalAgreement result = PrivateMethodTester.tester(service, "buildRentalAgreement", rentalRequest, values, 1L);
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

    @Test
    void test_DetermineDiscountAmount_HappyPath() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        RentalRequest request = RentalRequest.builder()
                .code(data.getTools().get(0).getCode())
                .checkOutDate(LocalDate.of(2024, 1, 2))
                .discount(BigDecimal.valueOf(random.nextDouble(0.01, 99.99)))
                .rentalDayCount(random.nextInt(1, 10))
                .build();
        BigDecimal expected = BigDecimal.ONE.subtract(BigDecimal.ONE.multiply(request.getDiscount().divide(BigDecimal.valueOf(100))));
        // Act
        BigDecimal result = PrivateMethodTester.tester(service, "determineDiscountAmount", request, BigDecimal.ONE);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(expected);
    }

    @Test
    void test_DetermineDiscountAmount_HappyPath_NoDiscount() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        RentalRequest request = RentalRequest.builder()
                .code(data.getTools().get(0).getCode())
                .checkOutDate(LocalDate.of(2024, 1, 2))
                .discount(BigDecimal.ZERO)
                .rentalDayCount(random.nextInt(1, 10))
                .build();
        // Act
        BigDecimal result = PrivateMethodTester.tester(service, "determineDiscountAmount", request, BigDecimal.ONE);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void test_DetermineChargeAmount_HappyPath_Weekday() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        data.getToolsCharges().add(0, ToolsCharges.builder()
                .dailyCharge(BigDecimal.valueOf(random.nextDouble(0.01, 999.99)))
                .holidayCharge(false)
                .weekDayCharge(true)
                .weekEndCharge(false)
                .build());
        LocalDate weekDay = LocalDate.of(2024, 11, 1);
        // Act
        BigDecimal result = PrivateMethodTester.tester(service, "determineChargeAmount", weekDay, data.getToolsCharges().get(0));
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(data.getToolsCharges().get(0).getDailyCharge());
    }

    @Test
    void test_DetermineChargeAmount_HappyPath_WeekEnd() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        data.getToolsCharges().add(0, ToolsCharges.builder()
                .dailyCharge(BigDecimal.valueOf(random.nextDouble(0.01, 999.99)))
                .holidayCharge(false)
                .weekDayCharge(false)
                .weekEndCharge(true)
                .build());
        LocalDate weekEnd = LocalDate.of(2024, 11, 2);
        // Act
        BigDecimal result = PrivateMethodTester.tester(service, "determineChargeAmount", weekEnd, data.getToolsCharges().get(0));
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(data.getToolsCharges().get(0).getDailyCharge());
    }

    @Test
    void test_DetermineChargeAmount_HappyPath_Holiday() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        data.getToolsCharges().add(0, ToolsCharges.builder()
                .dailyCharge(BigDecimal.valueOf(random.nextDouble(0.01, 999.99)))
                .holidayCharge(true)
                .weekDayCharge(false)
                .weekEndCharge(false)
                .build());
        LocalDate holiday = LocalDate.of(2024, data.getHolidays().get(0).getHolidayMonth(), data.getHolidays().get(0).getHolidayDay());
        // Act
        BigDecimal result = PrivateMethodTester.tester(service, "determineChargeAmount", holiday, data.getToolsCharges().get(0));
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(data.getToolsCharges().get(0).getDailyCharge());
    }

    @Test
    void test_AddToChargeIfCharging_HappyPath() {
        // Arrange
        buildTestingServiceWithData();
        // Act
        BigDecimal result = PrivateMethodTester.tester(service, "addToChargeIfCharging", true, BigDecimal.ONE, BigDecimal.ONE);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(BigDecimal.valueOf(2));
    }

    @Test
    void test_AddToChargeIfCharging_HappyPath_NoAdd() {
        // Arrange
        buildTestingServiceWithData();
        // Act
        BigDecimal result = PrivateMethodTester.tester(service, "addToChargeIfCharging", false, BigDecimal.ONE, BigDecimal.valueOf(10));
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(BigDecimal.ONE);
    }

    @Test
    void test_DetermineIfWeekend_HappyPath_NotWeekend() {
        // Arrange
        buildTestingServiceWithData();
        // Act
        Boolean result = PrivateMethodTester.tester(service, "determineIfWeekend", LocalDate.of(2024, 11, 1));
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(false);
    }

    @Test
    void test_DetermineIfWeekend_HappyPath_Weekend() {
        // Arrange
        buildTestingServiceWithData();
        // Act
        Boolean result = PrivateMethodTester.tester(service, "determineIfWeekend", LocalDate.of(2024, 11, 2));
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(true);
    }

    @Test
    void test_DetermineIfHoliday_HappyPath() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        LocalDate date = LocalDate.of(2024, data.getHolidays().get(0).getHolidayMonth(), data.getHolidays().get(0).getHolidayDay()).plusDays(1);
        // Act
        Boolean result = PrivateMethodTester.tester(service, "determineIfHoliday", date);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(false);
    }

    @Test
    void test_DetermineIfHoliday_HappyPath_HolidayByDate() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        LocalDate date = LocalDate.of(2024, data.getHolidays().get(0).getHolidayMonth(), data.getHolidays().get(0).getHolidayDay());
        // Act
        Boolean result = PrivateMethodTester.tester(service, "determineIfHoliday", date);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(true);
    }

    @Test
    void test_DetermineIfHoliday_HappyPath_HolidayByFrequency() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        data.getHolidays().get(0).setHolidayDay(null);
        data.getHolidays().get(0).setHolidayFrequency("FIRST");
        data.getHolidays().get(0).setDayOfTheWeek(DayOfWeek.MONDAY.toString());
        data.getHolidays().get(0).setHolidayMonth(Month.SEPTEMBER.getValue());
        connector = new ToolRentalConnectorImpl(rentalAgreementRepository, data);
        service = new ToolRentalService(connector);
        LocalDate date = LocalDate.of(2024, 9, 2);
        // Act
        Boolean result = PrivateMethodTester.tester(service, "determineIfHoliday", date);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(true);
    }

    @Test
    void test_DetermineIfMonthMatch_HappyPath() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        LocalDate date = LocalDate.of(2024, data.getHolidays().get(0).getHolidayMonth(), data.getHolidays().get(0).getHolidayDay());
        // Act
        Boolean result = PrivateMethodTester.tester(service, "determineIfMonthMatch", date, data.getHolidays().get(0));
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(true);
    }

    @Test
    void test_DetermineIfMonthMatch_HappyPath_NotMonth() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        LocalDate date = LocalDate.of(2024, data.getHolidays().get(0).getHolidayMonth(), data.getHolidays().get(0).getHolidayDay()).plusDays(32);
        // Act
        Boolean result = PrivateMethodTester.tester(service, "determineIfMonthMatch", date, data.getHolidays().get(0));
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(false);
    }

    @Test
    void test_determineIfDayOfWeekMatch_HappyPath() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        data.getHolidays().get(0).setHolidayDay(null);
        data.getHolidays().get(0).setHolidayFrequency("FIRST");
        data.getHolidays().get(0).setDayOfTheWeek(DayOfWeek.MONDAY.toString());
        data.getHolidays().get(0).setHolidayMonth(Month.SEPTEMBER.getValue());
        connector = new ToolRentalConnectorImpl(rentalAgreementRepository, data);
        service = new ToolRentalService(connector);
        LocalDate date = LocalDate.of(2024, 9, 2);
        // Act
        Boolean result = PrivateMethodTester.tester(service, "determineIfDayOfWeekMatch", date, data.getHolidays().get(0));
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(true);
    }

    @Test
    void test_determineIfDayOfWeekMatch_HappyPath_NotMatch() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        data.getHolidays().get(0).setHolidayDay(null);
        data.getHolidays().get(0).setHolidayFrequency("FIRST");
        data.getHolidays().get(0).setDayOfTheWeek(DayOfWeek.MONDAY.toString());
        data.getHolidays().get(0).setHolidayMonth(Month.SEPTEMBER.getValue());
        connector = new ToolRentalConnectorImpl(rentalAgreementRepository, data);
        service = new ToolRentalService(connector);
        LocalDate date = LocalDate.of(2024, 9, 3);
        // Act
        Boolean result = PrivateMethodTester.tester(service, "determineIfDayOfWeekMatch", date, data.getHolidays().get(0));
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(false);
    }

    @Test
    void test_DetermineIfFirst_HappyPath() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        data.getHolidays().get(0).setHolidayFrequency("FIRST");
        connector = new ToolRentalConnectorImpl(rentalAgreementRepository, data);
        service = new ToolRentalService(connector);
        LocalDate date = LocalDate.of(2024, 9, 2);
        // Act
        Boolean result = PrivateMethodTester.tester(service, "determineIfFirst", date, data.getHolidays().get(0));
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(true);
    }

    @Test
    void test_DetermineIfFirst_HappyPath_NotFirstWeek() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        data.getHolidays().get(0).setHolidayFrequency("FIRST");
        connector = new ToolRentalConnectorImpl(rentalAgreementRepository, data);
        service = new ToolRentalService(connector);
        LocalDate date = LocalDate.of(2024, 9, 9);
        // Act
        Boolean result = PrivateMethodTester.tester(service, "determineIfFirst", date, data.getHolidays().get(0));
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(false);
    }

    @Test
    void test_GetRentalDaysChargeMap_HappyPath() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        LocalDate checkOut = LocalDate.of(2024, 1, 2);
        RentalRequest rentalRequest = RentalRequest.builder()
                .code(data.getTools().get(0).getCode())
                .checkOutDate(checkOut)
                .discount(BigDecimal.valueOf(random.nextDouble(0.01, 99.99)))
                .rentalDayCount(random.nextInt(1, 10))
                .build();
        // Act
        Map<LocalDate, ChargeValues> result = PrivateMethodTester.tester(service, "getRentalDaysChargeMap", rentalRequest);
        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result.keySet().size()).isEqualTo(rentalRequest.getRentalDayCount());
        Assertions.assertThat(result).containsKey(checkOut);
    }

    @Test
    void test_verifyRequestForRental_ForRental_HappyPath() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithDataAndFindingCode(null);
        RentalRequest rentalRequest = RentalRequest.builder()
                .code(data.getTools().get(0).getCode())
                .checkOutDate(LocalDate.of(2024, 1, 2))
                .discount(BigDecimal.valueOf(random.nextDouble(0.01, 99.99)))
                .rentalDayCount(random.nextInt(1, 10))
                .build();
        // Act
        PrivateMethodTester.tester(service, "verifyRequestForRental", rentalRequest);
        // Assert
        assertTrue(true, "We passed verification with no exceptions");
    }

    @Test
    void test_verifyRequestForRental_ForRental_HappyPath_ToolAlreadyRented() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithDataAndFindingCode(1L);
        RentalRequest rentalRequest = RentalRequest.builder()
                .code(data.getTools().get(0).getCode())
                .checkOutDate(LocalDate.of(2024, 1, 2))
                .discount(BigDecimal.valueOf(random.nextDouble(0.01, 99.99)))
                .rentalDayCount(random.nextInt(1, 10))
                .build();
        // Act
        Exception exception = assertThrows(ToolAlreadyRentedException.class, () -> {
            PrivateMethodTester.testerException(service, "verifyRequestForRental", ToolAlreadyRentedException.class, rentalRequest);
        });
        // Assert
        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception).isInstanceOf(ToolAlreadyRentedException.class);
        Assertions.assertThat(exception.getMessage()).isNotNull();
        Assertions.assertThat(exception.getMessage()).isEqualTo(ToolAlreadyRentedException.DEFAULT_MESSAGE);
    }

    @Test
    void test_verifyRequestForRental_HappyPath_RequestForRentalEmpty() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        // Act
        Exception exception = assertThrows(InvalidRentalRequestException.class, () -> {
            PrivateMethodTester.testerException(service, "verifyRequestForRental", InvalidRentalRequestException.class, (Object) null);
        });
        // Assert
        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception).isInstanceOf(InvalidRentalRequestException.class);
        Assertions.assertThat(exception.getMessage()).isNotNull();
        Assertions.assertThat(exception.getMessage()).isEqualTo(InvalidRentalRequestException.DEFAULT_MESSAGE);
    }


    @Test
    void test_verifyRequestForRental_ForRental_HappyPath_InvalidRentalDays() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        RentalRequest rentalRequest = RentalRequest.builder()
                .code(data.getTools().get(0).getCode())
                .checkOutDate(LocalDate.of(2024, 1, 2))
                .discount(BigDecimal.valueOf(random.nextDouble(0.01, 99.99)))
                .rentalDayCount(0)
                .build();
        // Act
        Exception exception = assertThrows(RentalDayCountInvalidException.class, () -> {
            PrivateMethodTester.testerException(service, "verifyRequestForRental", RentalDayCountInvalidException.class, rentalRequest);
        });
        // Assert
        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception).isInstanceOf(RentalDayCountInvalidException.class);
        Assertions.assertThat(exception.getMessage()).isNotNull();
        Assertions.assertThat(exception.getMessage()).isEqualTo(RentalDayCountInvalidException.DEFAULT_MESSAGE);
    }

    @Test
    void test_verifyRequestForRental_ForRental_HappyPath_InvalidDiscount() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        RentalRequest rentalRequest = RentalRequest.builder()
                .code(data.getTools().get(0).getCode())
                .checkOutDate(LocalDate.of(2024, 1, 2))
                .discount(BigDecimal.valueOf(random.nextDouble(100.01, 9999.99)))
                .rentalDayCount(random.nextInt(1, 10))
                .build();
        // Act
        Exception exception = assertThrows(DiscountPercentInvalidException.class, () -> {
            PrivateMethodTester.testerException(service, "verifyRequestForRental", DiscountPercentInvalidException.class, rentalRequest);
        });
        // Assert
        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception).isInstanceOf(DiscountPercentInvalidException.class);
        Assertions.assertThat(exception.getMessage()).isNotNull();
        Assertions.assertThat(exception.getMessage()).isEqualTo(DiscountPercentInvalidException.DEFAULT_MESSAGE);
    }

    @Test
    void test_verifyRequestForReturn_ForRental_HappyPath() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        RentalRequest rentalRequest = RentalRequest.builder()
                .code(data.getTools().get(0).getCode())
                .build();
        // Act
        PrivateMethodTester.tester(service, "verifyRequestForReturn", rentalRequest);
        // Assert
        assertTrue(true, "We passed verification with no exceptions");
    }

    @Test
    void test_verifyRequestForReturn_HappyPath_RequestForRentalEmpty() {
        // Arrange
        ToolRentalData data = buildTestingServiceWithData();
        // Act
        Exception exception = assertThrows(InvalidRentalRequestException.class, () -> {
            PrivateMethodTester.testerException(service, "verifyRequestForReturn", InvalidRentalRequestException.class, (Object) null);
        });
        // Assert
        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception).isInstanceOf(InvalidRentalRequestException.class);
        Assertions.assertThat(exception.getMessage()).isNotNull();
        Assertions.assertThat(exception.getMessage()).isEqualTo(InvalidRentalRequestException.DEFAULT_MESSAGE);
    }

    @Test
    void test_verifyRequestForReturn_ForRental_HappyPat_NoToolCode() {
        // Arrange
        RentalRequest rentalRequest = RentalRequest.builder().build();
        // Act
        Exception exception = assertThrows(ToolCodeNotFoundException.class, () -> {
            PrivateMethodTester.testerException(service, "verifyRequestForReturn", ToolCodeNotFoundException.class, rentalRequest);
        });
        // Assert
        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception).isInstanceOf(ToolCodeNotFoundException.class);
        Assertions.assertThat(exception.getMessage()).isNotNull();
        Assertions.assertThat(exception.getMessage()).isEqualTo(ToolCodeNotFoundException.DEFAULT_MESSAGE);
    }


    /**
     * Build the data connector and inject into service for simple test cases
     *
     * @return Return rental data for any testing needs
     */
    private ToolRentalData buildTestingServiceWithData() {
        ToolRentalData toolRentalData = createTestData();
        rentalAgreementRepository = Mockito.mock(RentalAgreementRepository.class);
        connector = new ToolRentalConnectorImpl(rentalAgreementRepository, toolRentalData);
        service = new ToolRentalService(connector);
        return toolRentalData;
    }

    /**
     * Build the data connector and inject into service for simple test cases
     *
     * @return Return rental data for any testing needs
     */
    private ToolRentalData buildTestingServiceWithDataAndFindingCode(Long rentalId) {
        ToolRentalData toolRentalData = createTestData();
        rentalAgreementRepository = Mockito.mock(RentalAgreementRepository.class);
        Mockito.when(rentalAgreementRepository.findByCode(Mockito.anyString())).thenReturn(rentalId);
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
                .holidayCharge(true)
                .weekDayCharge(true)
                .weekEndCharge(true)
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