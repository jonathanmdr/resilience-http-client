package com.resilience.application.order.create;

import com.resilience.application.UseCase;
import com.resilience.domain.order.OrderGateway;

public abstract sealed class CreateOrderUseCase extends UseCase<CreateOrderInput, CreateOrderOutput> permits DefaultCreateOrderUseCase {

    protected final OrderGateway orderGateway;

    protected CreateOrderUseCase(final OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

}
