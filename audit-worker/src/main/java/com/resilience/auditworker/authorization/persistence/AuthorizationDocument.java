package com.resilience.auditworker.authorization.persistence;

import com.resilience.auditworker.common.OriginDocument;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "authorizations")
public class AuthorizationDocument {

    @Id
    private String id;

    @Field(value = "before")
    private AuthorizationDataDocument before;

    @Field(value = "after")
    private AuthorizationDataDocument after;

    @Field(value = "origin")
    private OriginDocument origin;

    @Field(value = "created_at")
    private Instant createdAt;

    public AuthorizationDocument() {
        this.createdAt = Instant.now();
    }

    public AuthorizationDocument(final AuthorizationDataDocument before, final AuthorizationDataDocument after, final OriginDocument origin) {
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

    public AuthorizationDataDocument getBefore() {
        return before;
    }

    public void setBefore(final AuthorizationDataDocument before) {
        this.before = before;
    }

    public AuthorizationDataDocument getAfter() {
        return after;
    }

    public void setAfter(final AuthorizationDataDocument after) {
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
