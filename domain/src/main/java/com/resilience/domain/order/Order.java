package com.resilience.domain.order;

import com.resilience.domain.AggregateRoot;
import com.resilience.domain.validation.ValidationHandler;

import java.math.BigDecimal;

public final class Order extends AggregateRoot<OrderId> {

    private final String customerId;
    private final BigDecimal amount;
    private final OrderStatus orderStatus;

    private Order(final OrderId id, final String customerId, final BigDecimal amount, final OrderStatus status) {
        super(id);
        this.customerId = customerId;
        this.amount = amount;
        this.orderStatus = status;
    }

    public static Order create(final String customerId, final BigDecimal amount) {
        return new Order(OrderId.unique(), customerId, amount, OrderStatus.CREATED);
    }

    public static Order with(final OrderId id, final String customerId, final BigDecimal amount, final OrderStatus status) {
        return new Order(id, customerId, amount, status);
    }

    public Order authorize(final AuthorizationOrderStatusTranslator authorizationOrderStatusTranslator) {
        return switch (authorizationOrderStatusTranslator.get()) {
            case CREATED -> this;
            case CONFIRMED -> this.confirm();
            case REJECTED -> this.reject();
        };
    }

    public boolean isFinalized() {
        return !this.orderStatus.hasMoreTransitions();
    }

    @Override
    public void validate(final ValidationHandler handler) {
        OrderValidator.create(this, handler).validate();
    }

    public String customerId() {
        return this.customerId;
    }

    public BigDecimal amount() {
        return this.amount;
    }

    public OrderStatus status() {
        return this.orderStatus;
    }

    private Order confirm() {
        return new Order(this.id, this.customerId, this.amount, this.orderStatus.confirm());
    }

    private Order reject() {
        return new Order(this.id, this.customerId, this.amount, this.orderStatus.reject());
    }

}
