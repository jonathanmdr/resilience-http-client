package com.resilience.orderworker.order.persistence;

import com.resilience.domain.order.Order;
import com.resilience.domain.order.OrderId;
import com.resilience.domain.order.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.util.Objects;

@Table(
    name = "orders",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "pk_orders",
            columnNames = {
                "id"
            }
        )
    }
)
@Entity(name = "Order")
public class OrderJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    protected String id;

    @Column(name = "customer_id", nullable = false, updatable = false)
    private String customerId;

    @Column(name = "amount", nullable = false, updatable = false)
    private BigDecimal amount;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public OrderJpaEntity() { }

    private OrderJpaEntity(final String id, final String customerId, final BigDecimal amount, final OrderStatus status) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
    }

    public static OrderJpaEntity from(final Order order) {
        return new OrderJpaEntity(order.id().value(), order.customerId(), order.amount(), order.status());
    }

    public Order toAggregate() {
        return Order.with(OrderId.from(id), customerId, amount, status);
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public void setStatus(final OrderStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final OrderJpaEntity that)) return false;
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.customerId, that.customerId)
            && Objects.nonNull(this.amount) && this.amount.compareTo(that.amount) == 0
            && this.status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.customerId, this.amount, this.status);
    }

}
