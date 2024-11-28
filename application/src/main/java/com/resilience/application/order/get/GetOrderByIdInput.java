package com.resilience.application.order.get;

public record GetOrderByIdInput(String orderId) {

    public static GetOrderByIdInput with(final String orderId) {
        return new GetOrderByIdInput(orderId);
    }

}
