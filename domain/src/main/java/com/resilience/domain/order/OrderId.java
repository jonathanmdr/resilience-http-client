package com.resilience.domain.order;

import com.resilience.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public final class OrderId extends Identifier {

    private final String value;

    private OrderId(final String value) {
        this.value = value;
    }

    public static OrderId unique() {
        return OrderId.from(String.valueOf(UUID.randomUUID()));
    }

    public static OrderId from(final String unique) {
        return new OrderId(unique);
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final OrderId orderId)) return false;
        return Objects.equals(this.value, orderId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }

}
