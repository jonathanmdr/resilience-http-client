package com.resiliente.orderapi.order.models;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record CreateOrderRequest(
    @Schema(example = "68d13800-d243-4a35-8177-b9fef6a0f395") String customerId,
    @Schema(example = "11.99") BigDecimal amount
) {

}
