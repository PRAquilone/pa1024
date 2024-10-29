package com.toolsrus.rentals.service;

import com.toolsrus.rentals.component.ToolRentalData;
import com.toolsrus.rentals.db.models.Holiday;
import com.toolsrus.rentals.db.models.RentalAgreement;
import com.toolsrus.rentals.db.models.Tools;
import com.toolsrus.rentals.db.models.ToolsCharges;
import com.toolsrus.rentals.db.repository.RentalAgreementRepository;
import com.toolsrus.rentals.exception.DiscountPercentInvalidException;
import com.toolsrus.rentals.exception.InvalidRentalRequestException;
import com.toolsrus.rentals.exception.InvalidRentalRequestToolTypeNotFoundException;
import com.toolsrus.rentals.exception.RentalDayCountInvalidException;
import com.toolsrus.rentals.exception.ToolAlreadyRentedException;
import com.toolsrus.rentals.models.ChargeValues;
import com.toolsrus.rentals.models.RentalRequest;
import com.toolsrus.rentals.models.RentalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class ToolRentalService {

    public static final int LESS_THAN = -1;
    public static final int GREATER_THAN = 1;
    private final ToolRentalData data;

    private final RentalAgreementRepository rentalAgreementRepository;

    public ToolRentalService(ToolRentalData data, RentalAgreementRepository rentalAgreementRepository) {
        this.data = data;
        this.rentalAgreementRepository = rentalAgreementRepository;
    }


    /**
     * Rent a tool if possible
     *
     * @param request The rental request
     * @return The rental agreement if the tool can be rented
     */
    public RentalResponse rentalTool(RentalRequest request) throws InvalidRentalRequestException, RentalDayCountInvalidException, DiscountPercentInvalidException, ToolAlreadyRentedException {

        // Verify the request
        verifyRequest(request);

        // Create response object
        RentalResponse response;

        try {

            // Populate data if not already
            data.populateDataFields();

            // Create id for rental agreement
            Long rentalId = rentalAgreementRepository.count() + 1;


            // Create a charge days map
            Map<LocalDate, ChargeValues> charges = getRentalDaysChargeMap(request);

            // Add charges to map
            charges = determineDayCharges(request, charges);

            // Determine the final chage
            ChargeValues finalCharges = buildFinalCharges(charges);

            // Build the rental agreemtn
            RentalAgreement agreement = buildRentalAgreement(request, finalCharges, rentalId);

            // Add the object to the database
            rentalAgreementRepository.save(agreement);

            // Build the response
            response = RentalResponse.builder()
                    .status(HttpStatus.OK)
                    .agreement(agreement)
                    .message(Optional.ofNullable(agreement).map(x -> "Rental Agreement Attached").orElse("Unable to rent tool"))
                    .build();

        } catch (Exception exception) {
            response = RentalResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(exception.getMessage())
                    .build();
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
    private RentalAgreement buildRentalAgreement(RentalRequest request, ChargeValues finalCharges, Long rentalId) {
        return RentalAgreement.builder()
                .rentalId(rentalId.intValue())
                .brand(data.getToolsFromCode(request.getCode()).getBrand())
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
    private Map<LocalDate, ChargeValues> determineDayCharges(RentalRequest request, Map<LocalDate, ChargeValues> charges) throws InvalidRentalRequestToolTypeNotFoundException {
        Set<LocalDate> keyDates = charges.keySet();
        for (LocalDate key : keyDates) {
            ToolsCharges toolCharge = getToolsCharges(request);
            BigDecimal fullDayCharge = determineChargeAmount(key, toolCharge);
            BigDecimal discountDayCharge = determineDiscountAmount(request, fullDayCharge);
            charges.put(key, ChargeValues.builder()
                    .fullCharge(fullDayCharge)
                    .discountedCharge(Optional.ofNullable(discountDayCharge).orElse(fullDayCharge))
                    .chargeDaysCount(Optional.ofNullable(fullDayCharge).map(x -> 1).orElse(0))
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
    private ToolsCharges getToolsCharges(RentalRequest request) throws InvalidRentalRequestToolTypeNotFoundException {
        Tools tool = data.getToolsFromCode(request.getCode());
        ToolsCharges toolCharge = data.getToolsChargesFromType(Optional.ofNullable(tool).map(Tools::getType).orElse(null));
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
    private BigDecimal determineDiscountAmount(RentalRequest request, BigDecimal fullDayCharge) {
        BigDecimal discountDayCharge = BigDecimal.ZERO;
        if ((Optional.ofNullable(fullDayCharge).isPresent()) &&
                (request.getDiscount().compareTo(BigDecimal.ZERO) != 0)) {
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
        for (Holiday holiday : data.getHolidays()) {
            if (Optional.ofNullable(holiday.getHolidayDay()).isPresent()) {
                isHoliday = (date.getDayOfMonth() == holiday.getHolidayDay()) &&
                        (date.getMonth().getValue() == holiday.getHolidayMonth());
            }
            if (Optional.ofNullable(holiday.getDayOfTheWeek()).isPresent()) {
                // Since we only have first, then that is all I am implementing but would implement other if added
                isHoliday = (isHoliday ||
                        (determineIfFirst(date, holiday) &&
                                determineIfDayOfWeekMatch(date, holiday)));
            }
        }
        return isHoliday;
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
    private Map<LocalDate, ChargeValues> getRentalDaysChargeMap(RentalRequest request) {
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
    private void verifyRequest(RentalRequest request) throws InvalidRentalRequestException, RentalDayCountInvalidException, DiscountPercentInvalidException, ToolAlreadyRentedException {
        verifyRequestNotEmpty(request);
        verifyValidRentalNumberOfDays(request);
        verifyCorrectDiscountAmount(request);
        verifyToolNotAlreadyRentedOut(request);
    }

    /**
     * Verify we have a valid discount amount
     *
     * @param request The request object
     * @throws DiscountPercentInvalidException Thrown if discount amount is invalid
     */
    private void verifyCorrectDiscountAmount(RentalRequest request) throws DiscountPercentInvalidException {
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
    private void verifyValidRentalNumberOfDays(RentalRequest request) throws RentalDayCountInvalidException {
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
    private void verifyRequestNotEmpty(RentalRequest request) throws InvalidRentalRequestException {
        if (Optional.ofNullable(request).isEmpty()) {
            throw new InvalidRentalRequestException("Rental Request is empty.");
        }
    }

    /**
     * Determine if the tool is already rented
     *
     * @param request The request
     * @throws ToolAlreadyRentedException The exception to throw if already rented
     */
    private void verifyToolNotAlreadyRentedOut(RentalRequest request) throws ToolAlreadyRentedException {
        Long found = rentalAgreementRepository.findByCode(request.getCode());
        if (Optional.ofNullable(found).isPresent()) {
            throw new ToolAlreadyRentedException();
        }
    }


}
