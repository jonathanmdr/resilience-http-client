package com.resilience.authorizationapi.api.routes;

import com.resilience.authorizationapi.api.FeatureToggleOpenApi;
import com.resilience.authorizationapi.featuretoggle.handlers.DisableFeatureHandler;
import com.resilience.authorizationapi.featuretoggle.handlers.EnableFeatureHandler;
import com.resilience.authorizationapi.featuretoggle.handlers.ListFeatureHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Component
public class FeatureToggleRouter {

    @Bean
    @FeatureToggleOpenApi
    public RouterFunction<ServerResponse> featureToggleRouterFunction(
        final ListFeatureHandler listFeatureHandler,
        final EnableFeatureHandler enableFeatureHandler,
        final DisableFeatureHandler disableFeatureHandler
    ) {
        return RouterFunctions.route()
            .GET("/v1/features", listFeatureHandler)
            .PATCH("/v1/features/{feature}/enable", enableFeatureHandler)
            .PATCH("/v1/features/{feature}/disable", disableFeatureHandler)
            .build();
    }

}
