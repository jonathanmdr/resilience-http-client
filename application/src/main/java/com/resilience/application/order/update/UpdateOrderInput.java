package com.resilience.application.order.update;

import com.resilience.domain.authorization.AuthorizationStatus;

public record UpdateOrderInput(
    String orderId,
    AuthorizationStatus status
) {

    public static UpdateOrderInput with(final String orderId, final AuthorizationStatus status) {
        return new UpdateOrderInput(orderId, status);
    }

}
