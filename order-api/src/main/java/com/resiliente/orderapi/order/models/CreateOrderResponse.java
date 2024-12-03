package com.resiliente.orderapi.order.models;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateOrderResponse(
    @Schema(example = "2235cdb8-8382-4f50-952f-1d6f1b621cce") String orderId
) {

}
