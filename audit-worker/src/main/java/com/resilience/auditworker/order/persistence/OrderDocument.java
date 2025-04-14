package com.resilience.auditworker.order.persistence;

import com.resilience.auditworker.common.OriginDocument;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "orders")
public class OrderDocument {

    @Id
    private String id;

    @Field(value = "before")
    private OrderBeforeDocument before;

    @Field(value = "after")
    private OrderAfterDocument after;

    @Field(value = "origin")
    private OriginDocument origin;

    @Field(value = "created_at")
    private Instant createdAt;

    public OrderDocument() {
        this.createdAt = Instant.now();
    }

    public OrderDocument(final OrderBeforeDocument before, final OrderAfterDocument after, final OriginDocument origin) {
        this.before = before;
        this.after = after;
        this.origin = origin;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public OrderBeforeDocument getBefore() {
        return before;
    }

    public void setBefore(final OrderBeforeDocument before) {
        this.before = before;
    }

    public OrderAfterDocument getAfter() {
        return after;
    }

    public void setAfter(final OrderAfterDocument after) {
        this.after = after;
    }

    public OriginDocument getOrigin() {
        return origin;
    }

    public void setOrigin(final OriginDocument origin) {
        this.origin = origin;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

}
