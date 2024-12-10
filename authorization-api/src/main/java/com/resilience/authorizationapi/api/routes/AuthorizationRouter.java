package com.resilience.authorizationapi.api.routes;

import com.resilience.authorizationapi.api.AuthorizationOpenApi;
import com.resilience.authorizationapi.authorization.handlers.AuthorizeOrderHandler;
import com.resilience.authorizationapi.authorization.handlers.GetAuthorizationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Component
public class AuthorizationRouter {

    @Bean
    @AuthorizationOpenApi
    public RouterFunction<ServerResponse> authorizationRouterFunction(final AuthorizeOrderHandler authorizeOrderHandler, final GetAuthorizationHandler getAuthorizationHandler) {
        return RouterFunctions.route()
            .GET("/v1/authorizations/{authorization_id}", getAuthorizationHandler)
            .POST("/v1/authorizations", authorizeOrderHandler)
            .build();
    }

}
