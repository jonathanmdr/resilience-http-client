package com.resilience.orderapi.authorization.integration;

import com.resilience.domain.common.Result;
import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.orderapi.HttpClientIntegrationTest;
import com.resilience.orderapi.autorization.integration.AuthorizationClient;
import com.resilience.orderapi.autorization.integration.AuthorizationClientConfiguration;
import com.resilience.orderapi.autorization.integration.AuthorizationRequest;
import com.resilience.orderapi.autorization.integration.AuthorizationResponse;
import com.resilience.orderapi.configuration.Json;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.assertj.core.api.ThrowingConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@HttpClientIntegrationTest(
    components = {
        AuthorizationClient.class,
        AuthorizationClientConfiguration.class
    }
)
class AuthorizationClientIntegrationTest {

    @Autowired
    private AuthorizationClient subject;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    void shouldBeProcessAuthorization() {
        final AuthorizationResponse response = AuthorizationResponse.with("APPROVED");
        stubFor(post(urlEqualTo("/v1/authorizations"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withStatus(HttpStatus.OK.value())
                .withBody(Json.writeValueAsString(response))
            )
       );
        final AuthorizationRequest request = AuthorizationRequest.with("1234", "4321", "5678", BigDecimal.valueOf(199.99));
        final Result<AuthorizationResponse, ValidationHandler> result = this.subject.authorize(request);

        assertThat(result).satisfies(actual -> {
            assertThat(actual.hasSuccess()).isTrue();
            assertThat(actual.success()).isEqualTo(response);
        });
        verify(1, postRequestedFor(urlEqualTo("/v1/authorizations"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(matchingJsonPath("$.authorization_id", equalTo("1234")))
            .withRequestBody(matchingJsonPath("$.order_id", equalTo("4321")))
            .withRequestBody(matchingJsonPath("$.customer_id", equalTo("5678")))
            .withRequestBody(matchingJsonPath("$.order_amount", equalTo("199.99")))
        );
    }

    @Test
    void givenAValidHttpStatusToRetryShouldBeApplyRetry() {
        final AuthorizationResponse response = AuthorizationResponse.with("APPROVED");
        stubFor(post(urlEqualTo("/v1/authorizations"))
            .inScenario("RETRY_VALIDATION")
            .willReturn(aResponse()
                .withStatus(HttpStatus.GATEWAY_TIMEOUT.value())
            )
            .willSetStateTo("FIRST_FAILURE")
        );

        stubFor(post(urlEqualTo("/v1/authorizations"))
            .inScenario("RETRY_VALIDATION")
            .whenScenarioStateIs("FIRST_FAILURE")
            .willReturn(aResponse()
                .withStatus(HttpStatus.GATEWAY_TIMEOUT.value())
            )
            .willSetStateTo("SECOND_FAILURE")
        );

        stubFor(post(urlEqualTo("/v1/authorizations"))
            .inScenario("RETRY_VALIDATION")
            .whenScenarioStateIs("SECOND_FAILURE")
            .willReturn(aResponse()
                .withStatus(HttpStatus.GATEWAY_TIMEOUT.value())
            )
            .willSetStateTo("THIRD_FAILURE")
        );

        stubFor(post(urlEqualTo("/v1/authorizations"))
            .inScenario("RETRY_VALIDATION")
            .whenScenarioStateIs("THIRD_FAILURE")
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withStatus(HttpStatus.OK.value())
                .withBody(Json.writeValueAsString(response))
            )
        );
        final AuthorizationRequest request = AuthorizationRequest.with("1234", "4321", "5678", BigDecimal.valueOf(199.99));
        final Result<AuthorizationResponse, ValidationHandler> result = this.subject.authorize(request);

        assertThat(result).satisfies(actual -> {
            assertThat(actual.hasSuccess()).isTrue();
            assertThat(actual.success()).isEqualTo(response);
        });
        verify(4, postRequestedFor(urlEqualTo("/v1/authorizations"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(matchingJsonPath("$.authorization_id", equalTo("1234")))
            .withRequestBody(matchingJsonPath("$.order_id", equalTo("4321")))
            .withRequestBody(matchingJsonPath("$.customer_id", equalTo("5678")))
            .withRequestBody(matchingJsonPath("$.order_amount", equalTo("199.99")))
        );
    }

    @Test
    void givenAInvalidHttpStatusToRetryShouldBeReturnError() {
        stubFor(post(urlEqualTo("/v1/authorizations"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.TOO_MANY_REQUESTS.value())
            )
        );

        final AuthorizationRequest request = AuthorizationRequest.with("1234", "4321", "5678", BigDecimal.valueOf(199.99));
        final Result<AuthorizationResponse, ValidationHandler> result = this.subject.authorize(request);

        assertThat(result).satisfies(actual -> {
            assertThat(actual.hasError()).isTrue();
            assertThat(actual.error()).satisfies(validationHandler -> {
                assertThat(validationHandler.hasErrors()).isTrue();
                assertThat(validationHandler.errors()).hasSize(1);
                assertThat(validationHandler.errors().getFirst().message()).isEqualTo("Authorization client failed with message: '429 Too Many Requests from POST http://localhost:9090/v1/authorizations'");
            });
        });
        verify(1, postRequestedFor(urlEqualTo("/v1/authorizations"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(matchingJsonPath("$.authorization_id", equalTo("1234")))
            .withRequestBody(matchingJsonPath("$.order_id", equalTo("4321")))
            .withRequestBody(matchingJsonPath("$.customer_id", equalTo("5678")))
            .withRequestBody(matchingJsonPath("$.order_amount", equalTo("199.99")))
        );
    }

    @Test
    void givenManyErrorsOnClientResponseThenShouldBeOpenCircuitBreaker() {
        stubFor(post(urlEqualTo("/v1/authorizations"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
            )
        );

        final CircuitBreaker circuitBreaker = this.circuitBreakerRegistry.circuitBreaker("authorizationCircuitBreaker");
        final AuthorizationRequest request = AuthorizationRequest.with("1234", "4321", "5678", BigDecimal.valueOf(199.99));

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
        IntStream.range(0, 5).forEach(i -> this.subject.authorize(request));
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        final AuthorizationResponse response = AuthorizationResponse.with("APPROVED");
        stubFor(post(urlEqualTo("/v1/authorizations"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withStatus(HttpStatus.OK.value())
                .withBody(Json.writeValueAsString(response))
            )
        );

        await()
            .atMost(Duration.ofSeconds(2))
            .pollInterval(Duration.ofSeconds(1))
            .until(() -> {
                this.subject.authorize(request);
                assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN);
                return true;
            });

        await()
            .atMost(Duration.ofSeconds(2))
            .pollInterval(Duration.ofSeconds(1))
            .until(() -> {
                this.subject.authorize(request);
                assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
                return true;
            });

        verify(22, postRequestedFor(urlEqualTo("/v1/authorizations"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(matchingJsonPath("$.authorization_id", equalTo("1234")))
            .withRequestBody(matchingJsonPath("$.order_id", equalTo("4321")))
            .withRequestBody(matchingJsonPath("$.customer_id", equalTo("5678")))
            .withRequestBody(matchingJsonPath("$.order_amount", equalTo("199.99")))
        );
    }

    @Test
    void givenManyConcurrentAndParallelRequestsThenBulkheadShouldPermitOne() {
        final AuthorizationResponse response = AuthorizationResponse.with("APPROVED");
        stubFor(post(urlEqualTo("/v1/authorizations"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withStatus(HttpStatus.OK.value())
                .withBody(Json.writeValueAsString(response))
            )
        );
        final AuthorizationRequest request = AuthorizationRequest.with("1234", "4321", "5678", BigDecimal.valueOf(199.99));
        final CompletableFuture<Result<AuthorizationResponse, ValidationHandler>> futureOne = CompletableFuture.supplyAsync(() -> this.subject.authorize(request));
        final CompletableFuture<Result<AuthorizationResponse, ValidationHandler>> futureTwo = CompletableFuture.supplyAsync(() -> this.subject.authorize(request));
        final CompletableFuture<List<Result<AuthorizationResponse, ValidationHandler>>> futures = CompletableFuture.allOf(futureOne, futureTwo)
            .thenApply(voidable -> Stream.of(futureOne, futureTwo)
                .map(CompletableFuture::join)
                .toList()
            );

        final List<Result<AuthorizationResponse, ValidationHandler>> results = futures.join();

        assertThat(results.getFirst())
            .satisfies(validateResultBy(response));
        assertThat(results.getLast())
            .satisfies(validateResultBy(response));

        verify(1, postRequestedFor(urlEqualTo("/v1/authorizations"))
            .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .withRequestBody(matchingJsonPath("$.authorization_id", equalTo("1234")))
            .withRequestBody(matchingJsonPath("$.order_id", equalTo("4321")))
            .withRequestBody(matchingJsonPath("$.customer_id", equalTo("5678")))
            .withRequestBody(matchingJsonPath("$.order_amount", equalTo("199.99")))
        );
    }

    private static ThrowingConsumer<Result<AuthorizationResponse, ValidationHandler>> validateResultBy(final AuthorizationResponse response) {
        return result -> {
            if (result.hasSuccess()) {
                assertThat(result.success())
                    .isNotNull()
                    .isEqualTo(response);
            } else {
                assertThat(result.error())
                    .isNotNull()
                    .extracting(ValidationHandler::firstError)
                    .extracting(Optional::get)
                    .isEqualTo(new Error("Authorization client failed with message: 'Bulkhead 'authorizationBulkhead' is full and does not permit further calls'"));
            }
        };
    }

}
