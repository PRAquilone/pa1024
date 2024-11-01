package com.toolsrus.rentals.service;

import com.toolsrus.rentals.db.connector.ToolRentalConnector;
import com.toolsrus.rentals.db.models.Holiday;
import com.toolsrus.rentals.db.models.RentalAgreement;
import com.toolsrus.rentals.db.models.Tools;
import com.toolsrus.rentals.db.models.ToolsCharges;
import com.toolsrus.rentals.exception.DiscountPercentInvalidException;
import com.toolsrus.rentals.exception.InvalidRentalRequestException;
import com.toolsrus.rentals.exception.InvalidRentalRequestToolTypeNotFoundException;
import com.toolsrus.rentals.exception.RentalDayCountInvalidException;
import com.toolsrus.rentals.exception.ToolAlreadyRentedException;
import com.toolsrus.rentals.exception.ToolCodeNotFoundException;
import com.toolsrus.rentals.models.ChargeValues;
import com.toolsrus.rentals.models.ToolRequest;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class ToolRentalService {

    public static final int LESS_THAN = -1;
    public static final int GREATER_THAN = 1;

    private final ToolRentalConnector connector;

    public ToolRentalService(ToolRentalConnector toolRentalConnector) {
        this.connector = toolRentalConnector;
    }

    /**
     * Rent a tool if possible
     *
     * @param request The rental request
     */
    public void returnRentalTool(ToolRequest request) throws InvalidRentalRequestException, ToolCodeNotFoundException {

        // Verify the request
        verifyRequestForReturn(request);

        // Get the rental id from the table from the tool code
        Long rentalId = connector.findRentalAgreementByCode(request.getCode());

        // Attempt to update the row to be closed
        connector.updateRentalAgreementToClosed(rentalId);

    }

    /**
     * Rent a tool if possible
     *
     * @param request The rental request
     * @return The rental agreement if the tool can be rented
     */
    public RentalAgreement rentalTool(ToolRequest request) throws InvalidRentalRequestException, RentalDayCountInvalidException, DiscountPercentInvalidException, ToolAlreadyRentedException, InvalidRentalRequestToolTypeNotFoundException {

        // Verify the request
        verifyRequestForRental(request);

        // Create response object
        RentalAgreement response;

        try {

            // Create id for rental agreement
            Long rentalId = connector.getTotalAgreements() + 1;

            // Create a charge days map
            Map<LocalDate, ChargeValues> charges = getRentalDaysChargeMap(request);

            // Add charges to map
            charges = determineDayCharges(request, charges);

            // Determine the final chage
            ChargeValues finalCharges = buildFinalCharges(charges);

            // Build the rental agreement
            response = buildRentalAgreement(request, finalCharges, rentalId);

            // Add the object to the database
            connector.saveRentalAgreement(response);

        } catch (Exception exception) {
            log.error("Error encountered attempting to create rental agreement due to " + exception.getMessage());
            throw exception;
        }

        return response;
    }

    /**
     * Build the final charges object for rental agreement
     *
     * @param charges The map of all the charges per day
     * @return The final charges built
     */
    private ChargeValues buildFinalCharges(Map<LocalDate, ChargeValues> charges) {
        Collection<ChargeValues> chargeValuesList = charges.values();
        ChargeValues finalCharges = ChargeValues.builder()
                .fullCharge(BigDecimal.ZERO)
                .discountedCharge(BigDecimal.ZERO)
                .chargeDaysCount(0)
                .build();
        for (ChargeValues values : chargeValuesList) {
            finalCharges.setFullCharge(finalCharges.getFullCharge().add(values.getFullCharge()));
            finalCharges.setDiscountedCharge(finalCharges.getDiscountedCharge().add(values.getDiscountedCharge()));
            finalCharges.setChargeDaysCount(finalCharges.getChargeDaysCount() + values.getChargeDaysCount());
            finalCharges.setToolsCharge(values.getToolsCharge());
        }
        return finalCharges;
    }

    /**
     * Build the rental agreement
     *
     * @param request      The request object
     * @param finalCharges The final charges object
     * @param rentalId     The rental ID
     * @return The built object
     */
    private RentalAgreement buildRentalAgreement(ToolRequest request, ChargeValues finalCharges, Long rentalId) {
        return RentalAgreement.builder()
                .rentalId(rentalId.intValue())
                .brand(connector.getData().getToolsFromCode(request.getCode()).getBrand())
                .code(request.getCode())
                .due(finalCharges.getDiscountedCharge())
                .type(finalCharges.getToolsCharge().getType())
                .chargeDays(finalCharges.getChargeDaysCount())
                .dailyCharge(finalCharges.getToolsCharge().getDailyCharge())
                .checkOutDate(request.getCheckOutDate())
                .discountAmount(finalCharges.getFullCharge().subtract(finalCharges.getDiscountedCharge()))
                .discountPercent(request.getDiscount())
                .dueDate(request.getCheckOutDate().plusDays(request.getRentalDayCount()))
                .finalCharge(finalCharges.getDiscountedCharge())
                .preDiscountCharge(finalCharges.getFullCharge())
                .rentalDays(request.getRentalDayCount())
                .toolStatus("ACTIVE")
                .build();
    }

    /**
     * Determine the charge per day this includes checking if it is a weekend or holiday and applying any discount
     *
     * @param request The request object
     * @param charges The rental charge days map
     * @return The map of charges
     */
    private Map<LocalDate, ChargeValues> determineDayCharges(ToolRequest request, Map<LocalDate, ChargeValues> charges) throws InvalidRentalRequestToolTypeNotFoundException {
        Set<LocalDate> keyDates = charges.keySet();
        ToolsCharges toolCharge = getToolsCharges(request);
        for (LocalDate key : keyDates) {
            BigDecimal fullDayCharge = determineChargeAmount(key, toolCharge);
            BigDecimal discountDayCharge = determineDiscountAmount(request, fullDayCharge);
            charges.put(key, ChargeValues.builder()
                    .fullCharge(fullDayCharge)
                    .discountedCharge(Optional.ofNullable(discountDayCharge).filter(x -> x.compareTo(BigDecimal.ZERO) != 0).orElse(fullDayCharge))
                    .chargeDaysCount(Optional.ofNullable(fullDayCharge).filter(x -> x.compareTo(BigDecimal.ZERO) != 0).map(x -> 1).orElse(0))
                    .toolsCharge(toolCharge)
                    .build());
        }
        return charges;
    }

    /**
     * Get the tool charges
     *
     * @param request The request object
     * @return The tool charges if found, throws exception if not
     * @throws InvalidRentalRequestToolTypeNotFoundException Throws if tool charges for this tool code/type not found
     */
    private ToolsCharges getToolsCharges(ToolRequest request) throws InvalidRentalRequestToolTypeNotFoundException {
        Tools tool = connector.getData().getToolsFromCode(request.getCode());
        ToolsCharges toolCharge = connector.getData().getToolsChargesFromType(Optional.ofNullable(tool).map(Tools::getType).orElse(null));
        if (Optional.ofNullable(toolCharge).isEmpty()) {
            throw new InvalidRentalRequestToolTypeNotFoundException();
        }
        return toolCharge;
    }

    /**
     * Determine the discount amount
     * Note: The discount charge amount will equal the full charge if the discount request is 0
     *
     * @param request       The request object
     * @param fullDayCharge The full charge already determined
     * @return The discount charge amount
     */
    private BigDecimal determineDiscountAmount(ToolRequest request, BigDecimal fullDayCharge) {
        BigDecimal discountDayCharge = BigDecimal.ZERO;
        if (request.getDiscount().compareTo(BigDecimal.ZERO) != 0) {
            discountDayCharge = fullDayCharge.subtract(fullDayCharge.multiply(request.getDiscount().divide(BigDecimal.valueOf(100))));
        }
        return discountDayCharge;
    }

    /**
     * Determine the charge amount
     *
     * @param key        The date to check
     * @param toolCharge The tool charge object containing the value to charge and when to charge
     * @return The updated charges is valid to charge
     */
    private BigDecimal determineChargeAmount(LocalDate key, ToolsCharges toolCharge) {
        BigDecimal fullDayCharge = BigDecimal.ZERO;
        if (determineIfHoliday(key)) {
            fullDayCharge = addToChargeIfCharging(toolCharge.getHolidayCharge(), fullDayCharge, toolCharge.getDailyCharge());
        } else if (determineIfWeekend(key)) {
            fullDayCharge = addToChargeIfCharging(toolCharge.getWeekEndCharge(), fullDayCharge, toolCharge.getDailyCharge());
        } else {
            fullDayCharge = addToChargeIfCharging(toolCharge.getWeekDayCharge(), fullDayCharge, toolCharge.getDailyCharge());
        }
        return fullDayCharge;
    }

    /**
     * Add to the charges if we are charging this day
     *
     * @param chargeThisDay Flag for if we are charging this day
     * @param charge        The current day charge
     * @param valueToAdd    The charge value to add if we are charging
     * @return The day charge possibly updated for new added value
     */
    private BigDecimal addToChargeIfCharging(Boolean chargeThisDay, BigDecimal charge, BigDecimal valueToAdd) {
        // Add to charge
        return Optional.ofNullable(charge)
                .filter(x -> chargeThisDay)
                .map(x -> x.add(valueToAdd))
                .orElse(charge);
    }

    /**
     * Determine if we are a weekend date
     *
     * @param date The date to check
     * @return True if yes, false otherwise
     */
    private Boolean determineIfWeekend(LocalDate date) {
        return date.getDayOfWeek().getValue() >= 6;
    }

    /**
     * Determine if the date is a holiday
     *
     * @param date The date to check
     * @return True if it is and false otherwise
     */
    private Boolean determineIfHoliday(LocalDate date) {
        Boolean isHoliday = false;
        for (Holiday holiday : connector.getData().getHolidays()) {
            if ((Optional.ofNullable(holiday.getHolidayDay()).isPresent()) &&
                    (Optional.ofNullable(holiday.getHolidayMonth()).isPresent())) {
                isHoliday = (date.getDayOfMonth() == holiday.getHolidayDay()) &&
                        (date.getMonth().getValue() == holiday.getHolidayMonth());
            }
            if ((Optional.ofNullable(holiday.getDayOfTheWeek()).isPresent()) &&
                    (Optional.ofNullable(holiday.getHolidayFrequency()).isPresent()) &&
                    (Optional.ofNullable(holiday.getHolidayMonth()).isPresent())) {
                // Since we only have first, then that is all I am implementing but would implement other if added
                isHoliday = (isHoliday || (determineIfFirst(date, holiday) && determineIfMonthMatch(date, holiday) && determineIfDayOfWeekMatch(date, holiday)));
            }
        }
        return isHoliday;
    }

    /**
     * Determine if the month matches
     *
     * @param date    The date to check
     * @param holiday the holiday object
     * @return True if yes, false otherwise
     */
    private boolean determineIfMonthMatch(LocalDate date, Holiday holiday) {
        return Integer.valueOf(date.getMonth().getValue()).equals(Month.of(holiday.getHolidayMonth()).getValue());
    }

    /**
     * Determine if the day of week matches
     *
     * @param date    The date to check
     * @param holiday The holiday listing
     * @return True if yes, false otherwise
     */
    private Boolean determineIfDayOfWeekMatch(LocalDate date, Holiday holiday) {
        return date.getDayOfWeek().toString().equalsIgnoreCase(holiday.getDayOfTheWeek());
    }

    /**
     * Determine if first day of week
     *
     * @param date    The date to check
     * @param holiday The holiday object
     * @return True if it is the first time the day of the week appears and false otherwise
     */
    private Boolean determineIfFirst(LocalDate date, Holiday holiday) {
        Boolean isHoliday = false;
        if (holiday.getHolidayFrequency().equalsIgnoreCase("first")) {
            isHoliday = (date.getDayOfMonth() <= 7);
        }
        return isHoliday;
    }

    /**
     * Create a map of the dates that will be rented for determining the charge applied that day
     *
     * @param request The request containing the check out date and number of rental days
     * @return The created map with the dates as keys and null values to be populated
     */
    private Map<LocalDate, ChargeValues> getRentalDaysChargeMap(ToolRequest request) {
        LocalDate checkout = request.getCheckOutDate();
        Map<LocalDate, ChargeValues> charges = new HashMap<>();
        for (int i = 0; i < request.getRentalDayCount(); i++) {
            LocalDate rentalDay = checkout.plusDays(i);
            charges.put(rentalDay, null);
        }
        return charges;
    }

    /**
     * Verify the rental request object is valid for processing
     *
     * @param request The rental request
     * @throws InvalidRentalRequestException   Throws if request is empty
     * @throws RentalDayCountInvalidException  Throws if rental day coint is less than 1
     * @throws DiscountPercentInvalidException Throws if discount is not between 0-100
     */
    private void verifyRequestForRental(ToolRequest request) throws InvalidRentalRequestException, RentalDayCountInvalidException, DiscountPercentInvalidException, ToolAlreadyRentedException {
        verifyRequestNotEmpty(request);
        verifyValidRentalNumberOfDays(request);
        verifyCorrectDiscountAmount(request);
        verifyToolNotAlreadyRentedOut(request);
    }

    /**
     * Verify the rental request object is valid for processing
     *
     * @param request The rental request
     * @throws InvalidRentalRequestException Throws if request is empty
     * @throws ToolCodeNotFoundException     Throws if rental day coint is less than 1
     */
    private void verifyRequestForReturn(ToolRequest request) throws InvalidRentalRequestException, ToolCodeNotFoundException {
        verifyRequestNotEmpty(request);
        verifyRequestToolCodeNotEmpty(request);
    }

    /**
     * Verify we have a valid discount amount
     *
     * @param request The request object
     * @throws DiscountPercentInvalidException Thrown if discount amount is invalid
     */
    private void verifyCorrectDiscountAmount(ToolRequest request) throws DiscountPercentInvalidException {
        if ((Optional.ofNullable(request.getDiscount()).isEmpty()) ||
                (request.getDiscount().compareTo(BigDecimal.ZERO) == LESS_THAN) ||
                (request.getDiscount().compareTo(BigDecimal.valueOf(100)) == GREATER_THAN)) {
            throw new DiscountPercentInvalidException();
        }
    }

    /**
     * Verify we have a valid rental number of days
     *
     * @param request The request object
     * @throws RentalDayCountInvalidException Thrown if rental days count is less than or equal to 1
     */
    private void verifyValidRentalNumberOfDays(ToolRequest request) throws RentalDayCountInvalidException {
        if ((Optional.ofNullable(request.getRentalDayCount()).isEmpty()) ||
                (request.getRentalDayCount() < 1)) {
            throw new RentalDayCountInvalidException();
        }
    }

    /**
     * Verify that the request is not empty
     *
     * @param request The request object
     * @throws InvalidRentalRequestException Thrown if the request is empty
     */
    private void verifyRequestNotEmpty(ToolRequest request) throws InvalidRentalRequestException {
        if (Optional.ofNullable(request).isEmpty()) {
            throw new InvalidRentalRequestException();
        }
    }

    /**
     * Verify that the request is not empty
     *
     * @param request The request object
     * @throws ToolCodeNotFoundException Thrown if the request is empty
     */
    private void verifyRequestToolCodeNotEmpty(ToolRequest request) throws ToolCodeNotFoundException {
        if ((Optional.ofNullable(request).isPresent()) &&
                (StringUtils.isBlank(request.getCode()))) {
            throw new ToolCodeNotFoundException();
        }
    }

    /**
     * Determine if the tool is already rented
     *
     * @param request The request
     * @throws ToolAlreadyRentedException The exception to throw if already rented
     */
    private void verifyToolNotAlreadyRentedOut(ToolRequest request) throws ToolAlreadyRentedException {
        Long found = connector.findRentalAgreementByCode(request.getCode());
        if (Optional.ofNullable(found).isPresent()) {
            throw new ToolAlreadyRentedException();
        }
    }


}
