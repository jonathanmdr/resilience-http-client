package com.resilience.auditworker.authorization.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "authorizations-dlt")
public class AuthorizationDltDocument {

    @Id
    private String id;

    @Field(value = "payload")
    private String payload;

    @Field(value = "error")
    private String error;

    @Field(value = "created_at")
    private Instant createdAt;

    public AuthorizationDltDocument() {
        this.createdAt = Instant.now();
    }

    public AuthorizationDltDocument(final String payload, final String error) {
        this.payload = payload;
        this.error = error;
        this.createdAt = Instant.now();
    }

    public String id() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String payload() {
        return payload;
    }

    public void setPayload(final String payload) {
        this.payload = payload;
    }

    public String error() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

}
