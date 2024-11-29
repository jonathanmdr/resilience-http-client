package com.resilience.application.authorization.authorize;

public record AuthorizeOrderInput(
    String orderId
) {

    public static AuthorizeOrderInput with(final String orderId) {
        return new AuthorizeOrderInput(orderId);
    }

}
