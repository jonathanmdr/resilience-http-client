package com.resilience.application.order.create;

import com.resilience.domain.common.Result;
import com.resilience.domain.order.Order;
import com.resilience.domain.order.OrderGateway;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.handler.NotificationHandler;

public final class DefaultCreateOrderUseCase extends CreateOrderUseCase {

    public DefaultCreateOrderUseCase(final OrderGateway orderGateway) {
        super(orderGateway);
    }

    @Override
    public Result<CreateOrderOutput, ValidationHandler> execute(final CreateOrderInput input) {
        final Order order = Order.create(input.customerId(), input.amount());
        final ValidationHandler handler = NotificationHandler.create();
        order.validate(handler);

        if (handler.hasErrors()) {
            return Result.error(handler);
        }

        final var newOrder = super.orderGateway.create(order);
        final var orderId = newOrder.id().value();
        final var output = CreateOrderOutput.with(orderId);

        return Result.success(output);
    }

}
