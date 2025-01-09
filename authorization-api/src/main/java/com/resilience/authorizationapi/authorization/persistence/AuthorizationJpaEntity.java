package com.resilience.authorizationapi.authorization.persistence;

import com.resilience.authorizationapi.authorization.models.AuthorizationRequest;
import com.resilience.domain.authorization.AuthorizationStatus;
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
    name = "authorizations",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "pk_authorizations",
            columnNames = {
                "id"
            }
        )
    }
)
@Entity(name = "Authorization")
public class AuthorizationJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    protected String id;

    @Column(name = "order_id", nullable = false, updatable = false)
    private String orderId;

    @Column(name = "customer_id", nullable = false, updatable = false)
    private String customerId;

    @Column(name = "amount", nullable = false, updatable = false)
    private BigDecimal amount;

    @Column(name = "status", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private AuthorizationStatus status;

    public AuthorizationJpaEntity() { }

    private AuthorizationJpaEntity(final String id, final String orderId, final String customerId, final BigDecimal amount, final AuthorizationStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
    }

    public static AuthorizationJpaEntity approve(final AuthorizationRequest request) {
        return new AuthorizationJpaEntity(request.authorizationId(), request.orderId(), request.customerId(), request.orderAmount(), AuthorizationStatus.APPROVED);
    }

    public static AuthorizationJpaEntity refuse(final AuthorizationRequest request) {
        return new AuthorizationJpaEntity(request.authorizationId(), request.orderId(), request.customerId(), request.orderAmount(), AuthorizationStatus.REFUSED);
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(final String orderId) {
        this.orderId = orderId;
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

    public AuthorizationStatus getStatus() {
        return this.status;
    }

    public void setStatus(final AuthorizationStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final AuthorizationJpaEntity that)) return false;
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.orderId, that.orderId)
            && Objects.equals(this.customerId, that.customerId)
            && Objects.nonNull(this.amount) && this.amount.compareTo(that.amount) == 0
            && this.status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.orderId, this.customerId, this.amount, this.status);
    }

}
