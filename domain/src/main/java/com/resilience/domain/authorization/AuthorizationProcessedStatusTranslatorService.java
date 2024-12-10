package com.resilience.domain.authorization;

import com.resilience.domain.order.OrderStatus;

public final class AuthorizationProcessedStatusTranslatorService implements AuthorizationProcessedStatusTranslator {

    private final OrderStatus orderStatus;

    private AuthorizationProcessedStatusTranslatorService(final OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public static AuthorizationProcessedStatusTranslatorService create(final OrderStatus orderStatus) {
        return new AuthorizationProcessedStatusTranslatorService(orderStatus);
    }

    @Override
    public AuthorizationStatus get() {
        return switch (this.orderStatus) {
            case CONFIRMED -> AuthorizationStatus.APPROVED;
            case REJECTED -> AuthorizationStatus.REFUSED;
            default -> AuthorizationStatus.PENDING;
        };
    }

}
