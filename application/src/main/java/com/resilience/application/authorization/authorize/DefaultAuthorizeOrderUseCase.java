package com.resilience.application.authorization.authorize;

import com.resilience.domain.authorization.Authorization;
import com.resilience.domain.authorization.AuthorizationGateway;
import com.resilience.domain.authorization.AuthorizationProcessedStatusTranslator;
import com.resilience.domain.authorization.AuthorizationProcessedStatusTranslatorService;
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
        final Authorization authorization = Authorization.create(order.id().value(), order.customerId(), order.amount());

        if (order.isFinalized()) {
            final AuthorizationProcessedStatusTranslator translator = AuthorizationProcessedStatusTranslatorService.create(order.status());
            return Result.success(AuthorizeOrderOutput.with(authorization.id().value(), translator.get().name()));
        }

        authorization.validate(handler);

        if (handler.hasErrors()) {
            return Result.error(handler);
        }

        final Authorization processedAuthorization = super.authorizationGateway.process(authorization);
        final AuthorizeOrderOutput output = AuthorizeOrderOutput.with(processedAuthorization.id().value(), processedAuthorization.status().name());

        return Result.success(output);
    }

}
