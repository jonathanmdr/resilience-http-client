package com.resilience.auditapi.persistence.authorization;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

public class AuthorizationDataDocument {

    @Field(value = "id")
    private String id;

    @Field(value = "order_id")
    private String orderId;

    @Field(value = "customer_id")
    private String customerId;

    @Field(value = "amount", targetType = FieldType.DECIMAL128)
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

    public String id() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String orderId() {
        return orderId;
    }

    public void setOrderId(final String orderId) {
        this.orderId = orderId;
    }

    public String customerId() {
        return customerId;
    }

    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal amount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public String status() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

}
