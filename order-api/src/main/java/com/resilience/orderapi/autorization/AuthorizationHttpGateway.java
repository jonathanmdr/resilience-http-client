package com.resilience.orderapi.autorization;

import com.resilience.domain.authorization.Authorization;
import com.resilience.domain.authorization.AuthorizationGateway;
import com.resilience.domain.authorization.AuthorizationStatusTranslatorService;
import com.resilience.domain.common.Result;
import com.resilience.domain.events.DomainEventPublisher;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.orderapi.autorization.integration.AuthorizationClient;
import com.resilience.orderapi.autorization.integration.AuthorizationRequest;
import com.resilience.orderapi.autorization.integration.AuthorizationResponse;
import com.resilience.orderapi.integration.http.HttpIntegrationException;
import com.resilience.orderapi.integration.messaging.StreamBinding;
import com.resilience.orderapi.integration.messaging.StreamEventPublisher;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationHttpGateway implements AuthorizationGateway {

    private final AuthorizationClient authorizationClient;
    private final StreamBridge streamBridge;

    public AuthorizationHttpGateway(final AuthorizationClient authorizationClient, final StreamBridge streamBridge) {
        this.authorizationClient = authorizationClient;
        this.streamBridge = streamBridge;
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
        final Authorization processedAuthorization = authorization.authorize(AuthorizationStatusTranslatorService.create(response.status()));

        final DomainEventPublisher domainEventPublisher = StreamEventPublisher.create(this.streamBridge, StreamBinding.AUTHORIZATION_ORDER_EVENTS);
        processedAuthorization.dispatch(domainEventPublisher);

        return processedAuthorization;
    }

}
