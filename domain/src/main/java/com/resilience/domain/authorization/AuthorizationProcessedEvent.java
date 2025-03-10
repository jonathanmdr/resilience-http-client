package com.resilience.domain.authorization;

import com.resilience.domain.events.DomainEvent;
import com.resilience.domain.validation.ValidationHandler;

import java.math.BigDecimal;
import java.time.Instant;

public record AuthorizationProcessedEvent(
    String authorizationId,
    String orderId,
    BigDecimal orderAmount,
    AuthorizationStatus status,
    Instant occurredOn
) implements DomainEvent, ValidationHandler.Validation {

    public static AuthorizationProcessedEvent with(
        final String authorizationId,
        final String orderId,
        final BigDecimal orderAmount,
        final AuthorizationStatus status,
        final Instant occurredOn
    ) {
        return new AuthorizationProcessedEvent(authorizationId, orderId, orderAmount, status, occurredOn);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        AuthorizationProcessedEventValidator.create(this, handler).validate();
    }

}
