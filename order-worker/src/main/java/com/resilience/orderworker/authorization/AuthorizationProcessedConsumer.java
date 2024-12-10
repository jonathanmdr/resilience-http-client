package com.resilience.orderworker.authorization;

import com.resilience.application.order.update.UpdateOrderInput;
import com.resilience.application.order.update.UpdateOrderUseCase;
import com.resilience.domain.authorization.AuthorizationProcessedEvent;
import com.resilience.domain.exception.DomainException;
import com.resilience.domain.validation.handler.NotificationHandler;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class AuthorizationProcessedConsumer implements Consumer<AuthorizationProcessedEvent> {

    private final UpdateOrderUseCase updateOrderUseCase;

    public AuthorizationProcessedConsumer(final UpdateOrderUseCase updateOrderUseCase) {
        this.updateOrderUseCase = updateOrderUseCase;
    }

    @Override
    public void accept(final AuthorizationProcessedEvent event) {
        final NotificationHandler handler = NotificationHandler.create();
        event.validate(handler);

        if (handler.hasErrors()) {
            throw DomainException.with("The authorization processed event has validation errors", handler.errors());
        }

        final UpdateOrderInput input = UpdateOrderInput.with(event.orderId(), event.status());
        this.updateOrderUseCase.execute(input);
    }

}
