package com.resilience.application.order.get;

import com.resilience.domain.common.Result;
import com.resilience.domain.order.Order;
import com.resilience.domain.order.OrderGateway;
import com.resilience.domain.order.OrderId;
import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.handler.NotificationHandler;

import java.util.Optional;

public final class DefaultGetOrderByIdUseCase extends GetOrderByIdUseCase {

    public DefaultGetOrderByIdUseCase(final OrderGateway orderGateway) {
        super(orderGateway);
    }

    @Override
    public Result<GetOrderByIdOutput, ValidationHandler> execute(final GetOrderByIdInput input) {
        final OrderId orderId = OrderId.from(input.orderId());
        final ValidationHandler handler = NotificationHandler.create();
        final Optional<Order> retrievedOrder = super.orderGateway.findById(orderId);

        if (retrievedOrder.isEmpty()) {
            handler.append(new Error("Order '%s' not found".formatted(orderId.value())));
            return Result.error(handler);
        }

        final Order order = retrievedOrder.get();
        final GetOrderByIdOutput output = GetOrderByIdOutput.with(order.id().value(), order.customerId(), order.amount(), order.status().name());

        return Result.success(output);
    }

}
