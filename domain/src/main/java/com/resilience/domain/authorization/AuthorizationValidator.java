package com.resilience.domain.authorization;

import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.Validator;

import java.math.BigDecimal;

public final class AuthorizationValidator extends Validator {

    private final Authorization authorization;

    private AuthorizationValidator(final Authorization authorization, final ValidationHandler handler) {
        super(handler);
        this.authorization = authorization;
    }

    public static AuthorizationValidator create(final Authorization authorization, final ValidationHandler handler) {
        return new AuthorizationValidator(authorization, handler);
    }

    @Override
    public void validate() {
        this.checkOrderConstraints();
    }

    private void checkOrderConstraints() {
        if (this.authorization.orderId() == null || this.authorization.orderId().isBlank()) {
            super.validationHandler().append(new Error("Order id must not be null or blank"));
        }
        if (this.authorization.customerId() == null || this.authorization.customerId().isBlank()) {
            super.validationHandler().append(new Error("Customer id must not be null or blank"));
        }
        if (this.authorization.orderAmount() == null || this.authorization.orderAmount().compareTo(BigDecimal.ZERO) <= 0) {
            super.validationHandler().append(new Error("Order amount must be greater than zero"));
        }
    }

}
