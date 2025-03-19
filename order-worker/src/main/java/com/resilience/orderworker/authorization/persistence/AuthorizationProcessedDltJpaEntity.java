package com.resilience.orderworker.authorization.persistence;

import com.resilience.domain.authorization.AuthorizationProcessedEvent;
import com.resilience.domain.common.ByteArrayUtils;
import com.resilience.domain.exception.NoStacktraceException;
import com.resilience.orderworker.configuration.Json;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.Message;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.UUID;

@Table(
    name = "authorizations_processed_dlt",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "pk_authorizations_processed_dlt",
            columnNames = {
                "id"
            }
        )
    }
)
@Entity(name = "AuthorizationProcessedDlt")
public class AuthorizationProcessedDltJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    protected String id;

    @Column(name = "payload", nullable = false, updatable = false)
    private String payload;

    @Column(name = "error", nullable = false, updatable = false)
    private String error;

    public AuthorizationProcessedDltJpaEntity() { }

    private AuthorizationProcessedDltJpaEntity(final String payload, final String error) {
        this.payload = StringUtils.normalizeSpace(payload);
        this.error = StringUtils.normalizeSpace(error);
        this.id = this.generateUniqueIdentifier();
    }

    public static AuthorizationProcessedDltJpaEntity from(final Message<AuthorizationProcessedEvent> message) {
        return new AuthorizationProcessedDltJpaEntity(Json.writeValueAsString(message.getPayload()), extractErrorFrom(message));
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getPayload() {
        return this.payload;
    }

    public void setPayload(final String payload) {
        this.payload = payload;
    }

    public String getError() {
        return this.error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final AuthorizationProcessedDltJpaEntity that)) return false;
        return Objects.equals(this.id, that.id)
            && Objects.equals(this.payload, that.payload)
            && Objects.equals(this.error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.payload, this.error);
    }

    private String generateUniqueIdentifier() {
        try {
            final var messageDigest = MessageDigest.getInstance("SHA-256");
            final var jsonEventBytes = this.payload.getBytes(StandardCharsets.UTF_8);
            final var bytes = messageDigest.digest(jsonEventBytes);
            return UUID.nameUUIDFromBytes(bytes).toString();
        } catch (final Exception ex) {
            throw new NoStacktraceException("The unique identifier generation failed", ex);
        }
    }

    private static String extractErrorFrom(final Message<AuthorizationProcessedEvent> message) {
        final byte[] stackTraceByteArray = message.getHeaders().get("x-exception-stacktrace", byte[].class);

        if (ByteArrayUtils.isNotNullOrEmpty(stackTraceByteArray)) {
            return new String(stackTraceByteArray, StandardCharsets.UTF_8);
        }

        return "The root cause cannot be obtained from exceptions raised during processing";
    }

}
