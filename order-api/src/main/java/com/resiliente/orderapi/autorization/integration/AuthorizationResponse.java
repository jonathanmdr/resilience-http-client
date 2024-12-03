package com.resiliente.orderapi.autorization.integration;

public record AuthorizationResponse(
    String authorizationId,
    String status
) {

    public static AuthorizationResponse with(final String authorizationId, final String status) {
        return new AuthorizationResponse(authorizationId, status);
    }

}
