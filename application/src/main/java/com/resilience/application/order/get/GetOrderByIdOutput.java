package com.resilience.application.order.get;

import java.math.BigDecimal;

public record GetOrderByIdOutput(
    String orderId,
    String customerId,
    BigDecimal amount
) {

    public static GetOrderByIdOutput with(final String orderId, final String customerId, final BigDecimal amount) {
        return new GetOrderByIdOutput(orderId, customerId, amount);
    }

}
