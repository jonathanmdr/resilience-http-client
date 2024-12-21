package com.resilience.orderapi.autorization.integration;

import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.Validator;

public final class AuthorizationResponseValidator extends Validator {

    private final AuthorizationResponse authorizationResponse;

    private AuthorizationResponseValidator(final AuthorizationResponse authorizationResponse, final ValidationHandler handler) {
        super(handler);
        this.authorizationResponse = authorizationResponse;
    }

    public static AuthorizationResponseValidator create(final AuthorizationResponse authorizationResponse, final ValidationHandler handler) {
        return new AuthorizationResponseValidator(authorizationResponse, handler);
    }

    @Override
    public void validate() {
        this.checkAuthorizationResponseConstraints();
    }

    private void checkAuthorizationResponseConstraints() {
        if (this.authorizationResponse.status() == null || this.authorizationResponse.status().isBlank()) {
            super.validationHandler().append(new Error("Authorization status must not be null or blank"));
        }
    }

}
