package com.resilience.orderapi.order.presenter;

import com.resilience.application.order.create.CreateOrderOutput;
import com.resilience.application.order.get.GetOrderByIdOutput;
import com.resilience.orderapi.order.models.CreateOrderResponse;
import com.resilience.orderapi.order.models.GetOrderByIdResponse;

public final class OrderPresenter {

    private OrderPresenter() { }

    public static CreateOrderResponse from(final CreateOrderOutput output) {
        return new CreateOrderResponse(output.orderId());
    }

    public static GetOrderByIdResponse from(final GetOrderByIdOutput output) {
        return new GetOrderByIdResponse(output.orderId(), output.customerId(), output.amount(), output.status());
    }

}
