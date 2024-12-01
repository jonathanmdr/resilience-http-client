package com.resiliente.orderapi.order.presenter;

import com.resilience.application.order.create.CreateOrderOutput;
import com.resiliente.orderapi.order.models.CreateOrderResponse;

public final class OrderPresenter {

    private OrderPresenter() { }

    public static CreateOrderResponse from(final CreateOrderOutput output) {
        return new CreateOrderResponse(output.orderId());
    }

}
