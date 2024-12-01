package com.resiliente.orderapi.order.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderRequest(
    @NotNull UUID customerId,
    @Positive BigDecimal amount
) {

}
