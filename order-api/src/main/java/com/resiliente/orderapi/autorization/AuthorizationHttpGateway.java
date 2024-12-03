package com.resiliente.orderapi.autorization;

import com.resilience.domain.authorization.Authorization;
import com.resilience.domain.authorization.AuthorizationGateway;
import com.resilience.domain.authorization.AuthorizationStatusTranslatorService;
import com.resilience.domain.common.Result;
import com.resilience.domain.validation.ValidationHandler;
import com.resiliente.orderapi.autorization.integration.AuthorizationClient;
import com.resiliente.orderapi.autorization.integration.AuthorizationRequest;
import com.resiliente.orderapi.autorization.integration.AuthorizationResponse;
import com.resiliente.orderapi.integration.http.HttpIntegrationException;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationHttpGateway implements AuthorizationGateway {

    private final AuthorizationClient authorizationClient;

    public AuthorizationHttpGateway(final AuthorizationClient authorizationClient) {
        this.authorizationClient = authorizationClient;
    }

    @Override
    public Authorization process(final Authorization authorization) {
        final AuthorizationRequest request = AuthorizationRequest.with(authorization.id().value(), authorization.orderId(), authorization.customerId(), authorization.orderAmount());
        final Result<AuthorizationResponse, ValidationHandler> result = this.authorizationClient.authorize(request);

        if (result.hasError()) {
            final ValidationHandler handler = result.error();
            throw HttpIntegrationException.with("Error processing authorization", handler.errors());
        }

        final AuthorizationResponse response = result.success();

        return authorization.authorize(AuthorizationStatusTranslatorService.create(response.status()));
    }

}
