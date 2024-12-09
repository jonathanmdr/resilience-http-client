package com.resilience.authorizationapi.authorization.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AuthorizationRequest(
    @NotBlank String authorizationId,
    @NotBlank String orderId,
    @NotBlank String customerId,
    @Positive BigDecimal orderAmount
) {

}
