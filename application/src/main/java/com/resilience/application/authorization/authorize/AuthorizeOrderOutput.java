package com.resilience.application.authorization.authorize;

public record AuthorizeOrderOutput(
    String authorizationId,
    String status
) {

    public static AuthorizeOrderOutput with(final String authorizationId, final String status) {
        return new AuthorizeOrderOutput(authorizationId, status);
    }

}
