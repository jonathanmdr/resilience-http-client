package com.resilience.application.order.create;

public record CreateOrderOutput(
    String orderId
) {

    public static CreateOrderOutput with(final String orderId) {
        return new CreateOrderOutput(orderId);
    }

}
