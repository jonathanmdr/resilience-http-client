package com.resilience.auditworker.authorization.persistence;

import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

public class AuthorizationDataDocument {

    @Field(value = "id")
    private String id;

    @Field(value = "order_id")
    private String orderId;

    @Field(value = "customer_id")
    private String customerId;

    @Field(value = "product_id")
    private BigDecimal amount;

    @Field(value = "status")
    private String status;

    public AuthorizationDataDocument() { }

    public AuthorizationDataDocument(final String id, final String orderId, final String customerId, final BigDecimal amount, final String status) {
        this.id = id;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
    }

    public String getId() {
        return id;
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
        return customerId;
    }

    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

}
