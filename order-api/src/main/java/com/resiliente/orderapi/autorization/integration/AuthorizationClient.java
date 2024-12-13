package com.resiliente.orderapi.autorization.integration;

import com.resilience.domain.common.Result;
import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.handler.NotificationHandler;
import com.resiliente.orderapi.autorization.integration.AuthorizationClientConfiguration.AuthorizationClientProperties;
import com.resiliente.orderapi.integration.http.BaseClientProperties;
import com.resiliente.orderapi.integration.http.WebClientTemplate;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.net.ssl.SSLException;
import java.util.Optional;

@Component
public class AuthorizationClient extends WebClientTemplate {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationClient.class);

    public AuthorizationClient(@AuthorizationClientProperties final BaseClientProperties properties) throws SSLException {
        super(properties);
    }

    @Bulkhead(name = "authorizationBulkhead", fallbackMethod = "authorizeFallback")
    @CircuitBreaker(name = "authorizationCircuitBreaker", fallbackMethod = "authorizeFallback")
    public Result<AuthorizationResponse, ValidationHandler> authorize(final AuthorizationRequest request) {
        try {
            final Optional<AuthorizationResponse> authorization = this.webClient.post()
                .uri("/v1/authorizations")
                .body(Mono.just(request), AuthorizationRequest.class)
                .retrieve()
                .bodyToMono(AuthorizationResponse.class)
                .timeout(timeoutInSeconds())
                .retryWhen(retryBackoffSpec())
                .onErrorResume(WebClientResponseException.class, fallbackResponseException())
                .blockOptional();

            return authorization.map(Result::success)
                .orElseGet(() -> Result.error(NotificationHandler.create(new Error("Authorization client failed with message: 'No response from authorization service'"))));
        } catch (final Exception ex) {
            log.error("Authorization client failed with message: '{}'", ex.getMessage(), ex);
            final Error error = new Error("Authorization client failed with message: '%s'".formatted(ex.getMessage()));
            return Result.error(NotificationHandler.create(error));
        }
    }

    private Result<AuthorizationResponse, ValidationHandler> authorizeFallback(final Throwable throwable) {
        final Error error = new Error("Authorization client failed with message: '%s'".formatted(throwable.getMessage()));
        return Result.error(NotificationHandler.create(error));
    }

}
