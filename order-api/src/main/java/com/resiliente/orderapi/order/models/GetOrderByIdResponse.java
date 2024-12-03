package com.resiliente.orderapi.order.models;

import com.resilience.domain.order.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record GetOrderByIdResponse(
    @Schema(example = "48a4bb4b-5a6d-4ede-9e4c-1609136d72a8") String orderId,
    @Schema(example = "68d13800-d243-4a35-8177-b9fef6a0f395") String customerId,
    @Schema(example = "11.99") BigDecimal amount,
    @Schema(example = "CONFIRMED", implementation = OrderStatus.class) String status
) {

}
