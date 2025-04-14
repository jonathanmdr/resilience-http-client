package com.resilience.auditworker.order;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.resilience.auditworker.configuration.EncodedBigDecimalDeserializer;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderEvent(
    UUID id,
    UUID customerId,
    @JsonDeserialize(using = EncodedBigDecimalDeserializer.class) BigDecimal amount,
    String status
) {

    public static OrderEvent with(
        final UUID id,
        final UUID customerId,
        final BigDecimal amount,
        final String status
    ) {
        return new OrderEvent(id, customerId, amount, status);
    }

}
