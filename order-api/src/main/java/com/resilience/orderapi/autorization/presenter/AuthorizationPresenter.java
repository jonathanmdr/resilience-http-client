package com.resilience.orderapi.autorization.presenter;

import com.resilience.application.authorization.authorize.AuthorizeOrderOutput;
import com.resilience.orderapi.autorization.models.AuthorizeOrderResponse;

public final class AuthorizationPresenter {

    private AuthorizationPresenter() { }

    public static AuthorizeOrderResponse from(final AuthorizeOrderOutput output) {
        return new AuthorizeOrderResponse(output.authorizationId(), output.status());
    }

}
