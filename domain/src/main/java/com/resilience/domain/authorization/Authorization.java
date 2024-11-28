package com.resilience.domain.authorization;

import com.resilience.domain.AggregateRoot;
import com.resilience.domain.validation.ValidationHandler;

import java.math.BigDecimal;

public final class Authorization extends AggregateRoot<AuthorizationId> {

    private final String orderId;
    private final String customerId;
    private final BigDecimal orderAmount;
    private final AuthorizationStatus authorizationStatus;

    private Authorization(
        final AuthorizationId id,
        final String orderId,
        final String customerId,
        final BigDecimal orderAmount,
        final AuthorizationStatus authorizationStatus
    ) {
        super(id);
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderAmount = orderAmount;
        this.authorizationStatus = authorizationStatus;
    }

    public static Authorization create(final String orderId, final String customerId, final BigDecimal orderAmount) {
        return new Authorization(AuthorizationId.unique(), orderId, customerId, orderAmount, AuthorizationStatus.PENDING);
    }

    public Authorization approve() {
        return new Authorization(this.id, this.orderId, this.customerId, this.orderAmount, this.authorizationStatus.approve());
    }

    public Authorization refuse() {
        return new Authorization(this.id, this.orderId, this.customerId, this.orderAmount, this.authorizationStatus.refuse());
    }

    @Override
    public void validate(final ValidationHandler handler) {
        AuthorizationValidator.create(this, handler).validate();
    }

    public String orderId() {
        return this.orderId;
    }

    public String customerId() {
        return this.customerId;
    }

    public BigDecimal orderAmount() {
        return this.orderAmount;
    }

    public AuthorizationStatus status() {
        return this.authorizationStatus;
    }

}