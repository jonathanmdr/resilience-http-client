package com.resilience.authorizationapi.featuretoggle.handlers;

import com.resilience.authorizationapi.featuretoggle.FeatureToggle;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
public class ListFeatureHandler implements HandlerFunction<ServerResponse> {

    @SuppressWarnings("all")
    @Override
    public ServerResponse handle(final ServerRequest serverRequest) {
        return ServerResponse.ok()
            .body(FeatureToggle.features());
    }

}
