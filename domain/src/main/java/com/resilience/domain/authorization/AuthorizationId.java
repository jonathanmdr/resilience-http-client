package com.resilience.domain.authorization;

import com.resilience.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public final class AuthorizationId extends Identifier {

    private final String value;

    private AuthorizationId(final String value) {
        this.value = value;
    }

    public static AuthorizationId unique() {
        return AuthorizationId.from(String.valueOf(UUID.randomUUID()));
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
