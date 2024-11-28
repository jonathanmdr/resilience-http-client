package com.resilience.domain.order;

import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.Validator;

import java.math.BigDecimal;

public final class OrderValidator extends Validator {

    private final Order order;

    private OrderValidator(final Order order, final ValidationHandler handler) {
        super(handler);
        this.order = order;
    }

    public static OrderValidator create(final Order order, final ValidationHandler handler) {
        return new OrderValidator(order, handler);
    }

    @Override
    public void validate() {
        this.checkOrderConstraints();
    }

    private void checkOrderConstraints() {
        if (this.order.customerId() == null || this.order.customerId().isBlank()) {
            super.validationHandler().append(new Error("Customer id must not be null or blank"));
        }
        if (this.order.amount() == null || this.order.amount().compareTo(BigDecimal.ZERO) <= 0) {
            super.validationHandler().append(new Error("Amount must be greater than zero"));
        }
    }

}
