package com.resilience.application.order.update;

import com.resilience.domain.order.AuthorizationOrderStatusTranslator;
import com.resilience.domain.order.Order;
import com.resilience.domain.order.OrderGateway;
import com.resilience.domain.order.OrderId;
import com.resilience.domain.order.AuthorizationOrderStatusTranslatorService;

public final class DefaultUpdateOrderUseCase extends UpdateOrderUseCase {

    public DefaultUpdateOrderUseCase(final OrderGateway orderGateway) {
        super(orderGateway);
    }

    @Override
    public void execute(final UpdateOrderInput input) {
        final OrderId orderId = OrderId.from(input.orderId());
        super.orderGateway.findById(orderId)
            .ifPresent(order -> {
                final AuthorizationOrderStatusTranslator translator = AuthorizationOrderStatusTranslatorService.create(input.status());
                final Order orderToUpdate = order.authorize(translator);
                super.orderGateway.update(orderToUpdate);
            });
    }

}
