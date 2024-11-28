package com.resilience.domain.order;

import com.resilience.domain.Identifier;

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

}
