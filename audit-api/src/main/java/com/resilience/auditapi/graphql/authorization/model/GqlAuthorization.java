package com.resilience.auditapi.graphql.authorization.model;

import java.math.BigDecimal;

public record GqlAuthorization(
    String id,
    String orderId,
    String customerId,
    BigDecimal amount,
    String status
) {

    public static GqlAuthorization with(
        final String id,
        final String orderId,
        final String customerId,
        final BigDecimal amount,
        final String status
    ) {
        return new GqlAuthorization(id, orderId, customerId, amount, status);
    }

}
