package com.resilience.auditapi.graphql.order.model;

import java.math.BigDecimal;

public record GqlOrder(
    String id,
    String customerId,
    BigDecimal amount,
    String status
) {

    public static GqlOrder with(
        final String id,
        final String customerId,
        final BigDecimal amount,
        final String status
    ) {
        return new GqlOrder(id, customerId, amount, status);
    }

}
