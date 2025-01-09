package com.resilience.authorizationapi.authorization.models;

import com.resilience.authorizationapi.authorization.persistence.AuthorizationJpaEntity;

import java.math.BigDecimal;

public record GetAuthorizationResponse(
    String orderId,
    String customerId,
    BigDecimal amount,
    String status
) {

    public static GetAuthorizationResponse from(final AuthorizationJpaEntity entity) {
        return new GetAuthorizationResponse(
            entity.getOrderId(),
            entity.getCustomerId(),
            entity.getAmount(),
            entity.getStatus().name()
        );
    }

}
