package com.toolsrus.rentals.cucumber;

import com.toolsrus.rentals.models.RentalRequest;
import io.cucumber.datatable.DataTable;
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
    public void the_client_receives_status_code_of(int statusCode) throws Throwable {
        HttpStatus currentStatusCode = rentalResponse.getStatus();
        Assertions.assertThat(currentStatusCode).isEqualTo(HttpStatus.OK);
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
        testValues(rows.get(0).get("rentalId"), rentalResponse.getAgreement().getRentalId().toString());
        testValues(rows.get(0).get("code"), rentalResponse.getAgreement().getCode());
        testValues(rows.get(0).get("type"), rentalResponse.getAgreement().getType());
        testValues(rows.get(0).get("brand"), rentalResponse.getAgreement().getBrand());
        testValues(rows.get(0).get("rentalDays"), rentalResponse.getAgreement().getRentalDays().toString());
        testValues(rows.get(0).get("checkOutDate"), rentalResponse.getAgreement().getCheckOutDate().toString());
        testValues(rows.get(0).get("dueDate"), rentalResponse.getAgreement().getDueDate().toString());
        testValues(rows.get(0).get("chargeDays"), rentalResponse.getAgreement().getChargeDays().toString());
        testValues(rows.get(0).get("due"), rentalResponse.getAgreement().getDue().toString());
        testValues(rows.get(0).get("dailyCharge"), rentalResponse.getAgreement().getDailyCharge().toString());
        testValues(rows.get(0).get("preDiscountCharge"), rentalResponse.getAgreement().getPreDiscountCharge().toString());
        testValues(rows.get(0).get("discountPercent"), rentalResponse.getAgreement().getDiscountPercent().toString());
        testValues(rows.get(0).get("discountAmount"), rentalResponse.getAgreement().getDiscountAmount().toString());
        testValues(rows.get(0).get("finalCharge"), rentalResponse.getAgreement().getFinalCharge().toString());
        testValues(rows.get(0).get("toolStatus"), rentalResponse.getAgreement().getToolStatus());
    }

    /**
     * Test the values to make sure that they equal each other.
     *
     * @param result The result from the rest call
     * @param expect The expected result
     */
    private void testValues(String result, String expect) {
        String resultValue = Optional.ofNullable(result).filter(StringUtils::isNotBlank).orElse(null);
        String expectedValue = Optional.ofNullable(expect).filter(StringUtils::isNotBlank).orElse(null);
        Assertions.assertThat(resultValue).isEqualTo(expectedValue);
    }

}
