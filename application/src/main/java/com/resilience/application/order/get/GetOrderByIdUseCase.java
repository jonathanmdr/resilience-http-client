package com.resilience.application.order.get;

import com.resilience.application.UseCase;
import com.resilience.domain.order.OrderGateway;

public abstract sealed class GetOrderByIdUseCase extends UseCase<GetOrderByIdInput, GetOrderByIdOutput> permits DefaultGetOrderByIdUseCase {

    protected final OrderGateway orderGateway;

    protected GetOrderByIdUseCase(final OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

}
