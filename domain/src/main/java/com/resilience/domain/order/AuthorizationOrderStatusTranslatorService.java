package com.resilience.domain.order;

import com.resilience.domain.authorization.AuthorizationStatus;

public final class AuthorizationOrderStatusTranslatorService implements AuthorizationOrderStatusTranslator {

    private final AuthorizationStatus authorizationStatus;

    private AuthorizationOrderStatusTranslatorService(final AuthorizationStatus authorizationStatus) {
        this.authorizationStatus = authorizationStatus;
    }

    public static AuthorizationOrderStatusTranslatorService create(final AuthorizationStatus authorizationStatus) {
        return new AuthorizationOrderStatusTranslatorService(authorizationStatus);
    }

    @Override
    public OrderStatus get() {
        return switch (this.authorizationStatus) {
            case PENDING -> OrderStatus.CREATED;
            case APPROVED -> OrderStatus.CONFIRMED;
            case REFUSED -> OrderStatus.REJECTED;
        };
    }

}
