package com.resiliente.orderapi.autorization.models;

public record AuthorizeOrderResponse(
    String authorizationId,
    String status
) {

    public static AuthorizeOrderResponse with(final String authorizationId, final String status) {
        return new AuthorizeOrderResponse(authorizationId, status);
    }

}
