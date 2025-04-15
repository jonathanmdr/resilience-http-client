package com.resilience.auditworker.authorization.consumer.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.resilience.auditworker.configuration.EncodedBigDecimalDeserializer;

import java.math.BigDecimal;
import java.util.UUID;

public record AuthorizationEvent(
    UUID id,
    UUID orderId,
    UUID customerId,
    @JsonDeserialize(using = EncodedBigDecimalDeserializer.class) BigDecimal amount,
    String status
) {

    public static AuthorizationEvent with(
        final UUID id,
        final UUID orderId,
        final UUID customerId,
        final BigDecimal amount,
        final String status
    ) {
        return new AuthorizationEvent(id, orderId, customerId, amount, status);
    }

}
