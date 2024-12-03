package com.resiliente.orderapi.autorization.integration;

import java.math.BigDecimal;

public record AuthorizationRequest(
    String authorizationId,
    String orderId,
    String customerId,
    BigDecimal orderAmount
) {

    public static AuthorizationRequest with(final String authorizationId, final String orderId, final String customerId, final BigDecimal orderAmount) {
        return new AuthorizationRequest(authorizationId, orderId, customerId, orderAmount);
    }

}
