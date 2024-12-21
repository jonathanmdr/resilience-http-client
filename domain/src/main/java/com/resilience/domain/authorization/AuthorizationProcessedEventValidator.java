package com.resilience.domain.authorization;

import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.Validator;

import java.math.BigDecimal;
import java.time.Instant;

public final class AuthorizationProcessedEventValidator extends Validator {

    private final AuthorizationProcessedEvent authorizationProcessedEvent;

    private AuthorizationProcessedEventValidator(final AuthorizationProcessedEvent authorizationProcessedEvent, final ValidationHandler handler) {
        super(handler);
        this.authorizationProcessedEvent = authorizationProcessedEvent;
    }

    public static AuthorizationProcessedEventValidator create(final AuthorizationProcessedEvent authorizationProcessedEvent, final ValidationHandler handler) {
        return new AuthorizationProcessedEventValidator(authorizationProcessedEvent, handler);
    }

    @Override
    public void validate() {
        this.checkAuthorizationProcessedEventConstraints();
    }

    private void checkAuthorizationProcessedEventConstraints() {
        if (this.authorizationProcessedEvent.authorizationId() == null || this.authorizationProcessedEvent.authorizationId().isBlank()) {
            super.validationHandler().append(Error.of("Authorization id must not be null or blank"));
        }
        if (this.authorizationProcessedEvent.orderId() == null || this.authorizationProcessedEvent.orderId().isBlank()) {
            super.validationHandler().append(Error.of("Order id must not be null or blank"));
        }
        if (this.authorizationProcessedEvent.orderAmount() == null || this.authorizationProcessedEvent.orderAmount().compareTo(BigDecimal.ZERO) <= 0) {
            super.validationHandler().append(Error.of("Order amount must be greater than zero"));
        }
        if (this.authorizationProcessedEvent.status() == null) {
            super.validationHandler().append(Error.of("Authorization status must not be null"));
        }
        if (this.authorizationProcessedEvent.occurredOn() == null || this.authorizationProcessedEvent.occurredOn().isAfter(Instant.now())) {
            super.validationHandler().append(Error.of("Occurred on must not be null or in the future"));
        }
    }

}
