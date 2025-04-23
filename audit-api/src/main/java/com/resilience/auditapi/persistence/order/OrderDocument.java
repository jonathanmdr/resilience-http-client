package com.resilience.auditapi.persistence.order;

import com.resilience.auditapi.persistence.common.OriginDocument;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "orders")
public class OrderDocument {

    @Id
    private String id;

    @Field(value = "before")
    private OrderDataDocument before;

    @Field(value = "after")
    private OrderDataDocument after;

    @Field(value = "origin")
    private OriginDocument origin;

    @Field(value = "created_at")
    private Instant createdAt;

    public OrderDocument() { }

    public OrderDocument(final String id, final OrderDataDocument before, final OrderDataDocument after, final OriginDocument origin, final Instant createdAt) {
        this.id = id;
        this.before = before;
        this.after = after;
        this.origin = origin;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public OrderDataDocument getBefore() {
        return before;
    }

    public void setBefore(final OrderDataDocument before) {
        this.before = before;
    }

    public OrderDataDocument getAfter() {
        return after;
    }

    public void setAfter(final OrderDataDocument after) {
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
