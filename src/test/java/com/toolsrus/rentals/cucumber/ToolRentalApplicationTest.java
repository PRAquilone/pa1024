package com.toolsrus.rentals.cucumber;

import com.toolsrus.rentals.ToolsRUsApplication;
import com.toolsrus.rentals.models.RentalRequest;
import com.toolsrus.rentals.models.RentalResponse;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@CucumberContextConfiguration
@SpringBootTest(classes = ToolsRUsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ToolRentalApplicationTest {

    private final static String url = "http://localhost:8080/tools/rental/rent";

    public static RentalResponse rentalResponse;

    protected RestTemplate restTemplate = new RestTemplate();

    /**
     * Execute the rest post call to the service
     *
     * @param rentalRequest The request we are making
     * @throws IOException If we encounter an exception will be thrown
     */
    public void executePost(RentalRequest rentalRequest) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON));
        HttpEntity<RentalRequest> request = new HttpEntity<>(rentalRequest, headers);
        try {
            rentalResponse = restTemplate.postForObject(url, request, RentalResponse.class);
        } catch (Exception exception) {
            log.error("Encountered exception " + exception.getMessage(), exception);
            rentalResponse = RentalResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(exception.getMessage())
                    .build();
        }
    }

}
