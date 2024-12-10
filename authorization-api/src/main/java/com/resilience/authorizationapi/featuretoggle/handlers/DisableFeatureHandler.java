package com.resilience.authorizationapi.featuretoggle.handlers;

import com.resilience.authorizationapi.api.GlobalExceptionHandler.ApiError;
import com.resilience.authorizationapi.featuretoggle.Feature;
import com.resilience.authorizationapi.featuretoggle.FeatureToggle;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
public class DisableFeatureHandler implements HandlerFunction<ServerResponse> {

    @SuppressWarnings("all")
    @Override
    public ServerResponse handle(final ServerRequest serverRequest) {
        final String feature = serverRequest.pathVariable("feature");

        if (feature.isBlank()) {
            return ServerResponse.badRequest()
                .body(ApiError.from("Feature name cannot be blank"));
        }

        try {
            FeatureToggle.disable(Feature.valueOf(feature.toUpperCase()));
            return ServerResponse.noContent().build();
        } catch (final IllegalArgumentException ex) {
            return ServerResponse.notFound().build();
        }
    }

}
