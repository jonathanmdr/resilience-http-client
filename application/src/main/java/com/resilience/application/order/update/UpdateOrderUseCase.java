package com.resilience.application.order.update;

import com.resilience.application.InputUseCase;
import com.resilience.domain.order.OrderGateway;

public abstract sealed class UpdateOrderUseCase extends InputUseCase<UpdateOrderInput> permits DefaultUpdateOrderUseCase {

    protected final OrderGateway orderGateway;

    protected UpdateOrderUseCase(final OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

}
