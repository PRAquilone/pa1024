package com.toolsrus.rentals.cucumber;

import com.toolsrus.rentals.db.repository.RentalAgreementRepository;
import com.toolsrus.rentals.models.RentalRequest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.micrometer.common.util.StringUtils;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ToolRentalStepDefinitions extends ToolRentalApplicationTest {

    private final RentalAgreementRepository rentalAgreementRepository;

    public ToolRentalStepDefinitions(RentalAgreementRepository rentalAgreementRepository) {
        this.rentalAgreementRepository = rentalAgreementRepository;
    }

    /**
     * Reset the rental agreement repo before each scenario
     */
    @Before
    public void beforeScenario() {
        rentalAgreementRepository.deleteAll();
    }

    /**
     * Execute a rest call to the service to get a response
     *
     * @param code       The code of the tool to rent
     * @param year       The year of the checkout date
     * @param month      The month of the checkout date
     * @param day        The day of the checkout date
     * @param rentalDays The number of rental days
     * @param discount   The discount applied if any
     * @throws Throwable Exceptions can be thrown if an error is encountered
     */
    @When("^the client calls /tools/rental/rent tool code (.+) on checkout of (\\d+)-(\\d+)-(\\d+) for (\\d+) days with a discount of (\\d+) percent$")
    public void RequestRental(String code, Integer year, Integer month, Integer day, Integer rentalDays, Integer discount) throws Throwable {
        executePost(RentalRequest.builder()
                .code(code)
                .checkOutDate(LocalDate.of(year, month, day))
                .discount(BigDecimal.valueOf(discount))
                .rentalDayCount(rentalDays)
                .build());
    }

    /**
     * Verify we got a good OK response
     *
     * @param statusCode The status code from the call
     * @throws Throwable Exceptions can be thrown if an error is encountered
     */
    @Then("^the client receives status code of (\\d+)$")
    public void the_client_receives_status_code_of_ok(int statusCode) throws Throwable {
        HttpStatus currentStatusCode = rentalResponse.getStatus();
        Assertions.assertThat(currentStatusCode).isEqualTo(HttpStatus.OK);
    }

    /**
     * Verify we got a good Error response
     *
     * @param statusCode The status code from the call
     * @throws Throwable Exceptions can be thrown if an error is encountered
     */
    @Then("^the client receives status error code of (\\d+)$")
    public void the_client_receives_status_code_of_error(int statusCode) throws Throwable {
        HttpStatus currentStatusCode = rentalResponse.getStatus();
        Assertions.assertThat(currentStatusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Verify the data from the response to the data table from the feature to ensure we got what we expected
     *
     * @param table The data table that will be populated from the feature file
     * @throws Throwable Exceptions can be thrown if an error is encountered
     */
    @And("^the client receives the following rental agreement$")
    public void the_client_receives_server_version_body(DataTable table) throws Throwable {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        Map<String, String> item = rows.get(0);
        testValues(item.get("rentalId"), rentalResponse.getAgreement().getRentalId().toString());
        testValues(item.get("code"), rentalResponse.getAgreement().getCode());
        testValues(item.get("type"), rentalResponse.getAgreement().getType());
        testValues(item.get("brand"), rentalResponse.getAgreement().getBrand());
        testValues(item.get("rentalDays"), rentalResponse.getAgreement().getRentalDays().toString());
        testValues(item.get("checkOutDate"), rentalResponse.getAgreement().getCheckOutDate().toString());
        testValues(item.get("dueDate"), rentalResponse.getAgreement().getDueDate().toString());
        testValues(item.get("chargeDays"), rentalResponse.getAgreement().getChargeDays().toString());
        testValues(item.get("due"), rentalResponse.getAgreement().getDue().toString());
        testValues(item.get("dailyCharge"), rentalResponse.getAgreement().getDailyCharge().toString());
        testValues(item.get("preDiscountCharge"), rentalResponse.getAgreement().getPreDiscountCharge().toString());
        testValues(item.get("discountPercent"), rentalResponse.getAgreement().getDiscountPercent().toString());
        testValues(item.get("discountAmount"), rentalResponse.getAgreement().getDiscountAmount().toString());
        testValues(item.get("finalCharge"), rentalResponse.getAgreement().getFinalCharge().toString());
        testValues(item.get("toolStatus"), rentalResponse.getAgreement().getToolStatus());
    }

    /**
     * Verify the data from the response to the data table from the feature to ensure we got the expected error
     *
     * @param table The data table that will be populated from the feature file
     * @throws Throwable Exceptions can be thrown if an error is encountered
     */
    @And("^the client receives the following error response for Discount Invalid$")
    public void the_client_receives_server_error_response_discount(DataTable table) throws Throwable {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        testValues(rows.get(0).get("message"), rentalResponse.getMessage());
    }

    /**
     * Verify the data from the response to the data table from the feature to ensure we got the expected error
     *
     * @param table The data table that will be populated from the feature file
     * @throws Throwable Exceptions can be thrown if an error is encountered
     */
    @And("^the client receives the following error response for tool already rented$")
    public void the_client_receives_server_error_response_already_rented(DataTable table) throws Throwable {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        testValues(rows.get(0).get("message"), rentalResponse.getMessage());
    }

    /**
     * Verify the data from the response to the data table from the feature to ensure we got the expected error
     *
     * @param table The data table that will be populated from the feature file
     * @throws Throwable Exceptions can be thrown if an error is encountered
     */
    @And("^the client receives the following error response for Rental Day Count Invalid$")
    public void the_client_receives_server_error_response_rental_day_count(DataTable table) throws Throwable {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        testValues(rows.get(0).get("message"), rentalResponse.getMessage());
    }

    /**
     * Test the values to make sure that they equal each other.
     *
     * @param result The result from the rest call
     * @param expect The expected result
     */
    private void testValues(String expect, String result) {
        String resultValue = Optional.ofNullable(result).filter(StringUtils::isNotBlank).orElse(null);
        String expectedValue = Optional.ofNullable(expect).filter(StringUtils::isNotBlank).orElse(null);
        Assertions.assertThat(resultValue).contains(expectedValue);
    }

}
