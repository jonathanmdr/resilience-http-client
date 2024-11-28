package com.resilience.application.order.create;

import java.math.BigDecimal;

public record CreateOrderInput(
    String customerId,
    BigDecimal amount
) {

    public static CreateOrderInput with(final String customerId, final BigDecimal amount) {
        return new CreateOrderInput(customerId, amount);
    }

}
