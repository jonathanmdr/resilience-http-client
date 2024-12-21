package com.resilience.domain.authorization;

import com.resilience.domain.Identifier;
import com.resilience.domain.exception.DomainException;
import com.resilience.domain.validation.Error;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public final class AuthorizationId extends Identifier {

    private final String value;

    private AuthorizationId(final String value) {
        this.value = value;
    }

    public static AuthorizationId unique(final String reference) {
        if (reference == null || reference.isBlank()) {
            throw DomainException.with(Error.of("Reference cannot be null or empty"));
        }

        final UUID uuid = UUID.nameUUIDFromBytes(reference.getBytes(StandardCharsets.UTF_8));
        return AuthorizationId.from(String.valueOf(uuid));
    }

    public static AuthorizationId from(final String unique) {
        return new AuthorizationId(unique);
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final AuthorizationId authorizationId)) return false;
        return Objects.equals(this.value, authorizationId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }

}
