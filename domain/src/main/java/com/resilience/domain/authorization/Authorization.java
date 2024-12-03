package com.resilience.domain.authorization;

import com.resilience.domain.AggregateRoot;
import com.resilience.domain.validation.ValidationHandler;

import java.math.BigDecimal;
import java.time.Instant;

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

    public static Authorization with(final AuthorizationId id, final String orderId, final String customerId, final BigDecimal orderAmount, final AuthorizationStatus authorizationStatus) {
        return new Authorization(id, orderId, customerId, orderAmount, authorizationStatus);
    }

    public Authorization authorize(final AuthorizationStatusTranslator authorizationStatusTranslator) {
        return switch (authorizationStatusTranslator.get()) {
            case PENDING -> this;
            case APPROVED -> this.approve();
            case REFUSED -> this.refuse();
        };
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

    private Authorization approve() {
        final var authorization = new Authorization(this.id, this.orderId, this.customerId, this.orderAmount, this.authorizationStatus.approve());
        authorization.addEvent(AuthorizationProcessedEvent.with(this.id.value(), this.orderId, this.orderAmount, authorization.status(), Instant.now()));
        return authorization;
    }

    private Authorization refuse() {
        final var authorization = new Authorization(this.id, this.orderId, this.customerId, this.orderAmount, this.authorizationStatus.refuse());
        authorization.addEvent(AuthorizationProcessedEvent.with(this.id.value(), this.orderId, this.orderAmount, authorization.status(), Instant.now()));
        return authorization;
    }

}
