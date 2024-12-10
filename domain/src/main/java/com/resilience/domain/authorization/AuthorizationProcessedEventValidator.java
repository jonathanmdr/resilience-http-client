package com.resilience.domain.authorization;

import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.Validator;

import java.math.BigDecimal;

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
            super.validationHandler().append(new Error("Authorization id must not be null or blank"));
        }
        if (this.authorizationProcessedEvent.orderId() == null || this.authorizationProcessedEvent.orderId().isBlank()) {
            super.validationHandler().append(new Error("Order id must not be null or blank"));
        }
        if (this.authorizationProcessedEvent.orderAmount() == null || this.authorizationProcessedEvent.orderAmount().compareTo(BigDecimal.ZERO) <= 0) {
            super.validationHandler().append(new Error("Order amount must be greater than zero"));
        }
        if (this.authorizationProcessedEvent.status() == null) {
            super.validationHandler().append(new Error("Authorization status must not be null"));
        }
        if (this.authorizationProcessedEvent.occurredOn() == null) {
            super.validationHandler().append(new Error("Occurred on must not be null"));
        }
    }

}
