package com.resilience.application.authorization.authorize;

import com.resilience.domain.authorization.Authorization;
import com.resilience.domain.authorization.AuthorizationGateway;
import com.resilience.domain.common.Result;
import com.resilience.domain.order.Order;
import com.resilience.domain.order.OrderGateway;
import com.resilience.domain.order.OrderId;
import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.handler.NotificationHandler;

import java.util.Optional;

public final class DefaultAuthorizeOrderUseCase extends AuthorizeOrderUseCase {

    public DefaultAuthorizeOrderUseCase(final OrderGateway orderGateway, final AuthorizationGateway authorizationGateway) {
        super(orderGateway, authorizationGateway);
    }

    @Override
    public Result<AuthorizeOrderOutput, ValidationHandler> execute(final AuthorizeOrderInput input) {
        final OrderId orderId = OrderId.from(input.orderId());
        final ValidationHandler handler = NotificationHandler.create();
        final Optional<Order> retrievedOrder = super.orderGateway.findById(orderId);

        if (retrievedOrder.isEmpty()) {
            handler.append(new Error("Order '%s' not found".formatted(orderId.value())));
            return Result.error(handler);
        }

        final Order order = retrievedOrder.get();
        final Authorization pendingAuthorization = Authorization.create(order.id().value(), order.customerId(), order.amount());
        pendingAuthorization.validate(handler);

        if (handler.hasErrors()) {
            return Result.error(handler);
        }

        final Authorization processedAuthorization = super.authorizationGateway.process(pendingAuthorization);
        final AuthorizeOrderOutput output = AuthorizeOrderOutput.with(processedAuthorization.id().value(), processedAuthorization.status().name());

        return Result.success(output);
    }

}
