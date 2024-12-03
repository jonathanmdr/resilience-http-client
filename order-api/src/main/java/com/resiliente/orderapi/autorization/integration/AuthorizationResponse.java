package com.resiliente.orderapi.autorization.integration;

public record AuthorizationResponse(
    String status
) {

    public static AuthorizationResponse with(final String status) {
        return new AuthorizationResponse(status);
    }

}
