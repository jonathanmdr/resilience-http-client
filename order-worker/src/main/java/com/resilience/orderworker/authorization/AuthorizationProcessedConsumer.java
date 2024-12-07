package com.resilience.orderworker.authorization;

import com.resilience.application.order.update.UpdateOrderInput;
import com.resilience.application.order.update.UpdateOrderUseCase;
import com.resilience.domain.authorization.AuthorizationProcessedEvent;
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
        final UpdateOrderInput input = UpdateOrderInput.with(event.orderId(), event.status());
        this.updateOrderUseCase.execute(input);
    }

}
