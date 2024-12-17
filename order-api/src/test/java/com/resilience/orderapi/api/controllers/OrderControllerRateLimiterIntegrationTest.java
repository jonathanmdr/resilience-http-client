package com.resilience.orderapi.api.controllers;

import com.resilience.orderapi.WebIntegrationTest;
import com.resiliente.orderapi.api.GlobalExceptionHandler.ApiError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@WebIntegrationTest
class OrderControllerRateLimiterIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void givenMultipleRequestsToGetOrderByIdThenRefuseRequestByRateLimiter() {
        final String orderId = UUID.randomUUID().toString();
        final String url = "/v1/orders/%s".formatted(orderId);
        final ResponseEntity<ApiError> responseEntityOne = this.testRestTemplate.getForEntity(url, ApiError.class);
        assertThat(responseEntityOne.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(responseEntityOne.getBody())
            .isEqualTo(ApiError.from("Command to retrieval resource cannot be processed", List.of("Order '%s' not found".formatted(orderId))));

        final ResponseEntity<ApiError> responseEntityTwo = this.testRestTemplate.getForEntity(url, ApiError.class);
        assertThat(responseEntityTwo.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(responseEntityTwo.getBody())
            .isEqualTo(ApiError.from("RateLimiter 'getOrderByIdRateLimiter' does not permit further calls"));
    }

}
