package com.resilience.domain;

import java.util.Objects;
import java.util.UUID;

public final class StubId extends Identifier {

    private final String value;

    private StubId(final String value) {
        this.value = value;
    }

    public static StubId unique() {
        return StubId.from(String.valueOf(UUID.randomUUID()));
    }

    public static StubId from(final String unique) {
        return new StubId(unique);
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final StubId stubId)) return false;
        return Objects.equals(this.value, stubId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }

}
