package com.resilience.orderapi.autorization.integration;

import com.resilience.domain.validation.ValidationHandler;

public record AuthorizationResponse(
    String status
) implements ValidationHandler.Validation {

    public static AuthorizationResponse with(final String status) {
        return new AuthorizationResponse(status);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        AuthorizationResponseValidator.create(this, handler).validate();
    }

}
