package com.resilience.application.order.get;

import java.math.BigDecimal;

public record GetOrderByIdOutput(
    String orderId,
    String customerId,
    BigDecimal amount,
    String status
) {

    public static GetOrderByIdOutput with(final String orderId, final String customerId, final BigDecimal amount, final String status) {
        return new GetOrderByIdOutput(orderId, customerId, amount, status);
    }

}
