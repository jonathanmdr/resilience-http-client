package com.resilience.orderapi.integration.http;

import com.resilience.orderapi.configuration.Json;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class WebClientTemplate {

    private static final Logger log = LoggerFactory.getLogger(WebClientTemplate.class);
    private static final int DEFAULT_BUFFER_BYTES_SIZE_IN_MB = 1024 * 1024;

    protected final WebClient webClient;
    private final String baseUrl;
    private final List<HttpStatusCode> retrievableHttpStatusCodes;
    private final int retrievableMinBackoffInSeconds;
    private final int retrievableMaxBackoffInSeconds;
    private final int retrievableMaxAttempts;
    private final double retrievableJitterFactor;
    private final int timeoutInSeconds;

    protected WebClientTemplate(final BaseClientProperties properties) throws SSLException {
        this.baseUrl = properties.getBaseUrl();
        this.retrievableHttpStatusCodes = properties.getRetrievableHttpErrors();
        this.retrievableMinBackoffInSeconds = properties.getRetrievableMinBackoffInSeconds();
        this.retrievableMaxBackoffInSeconds = properties.getRetrievableMaxBackoffInSeconds();
        this.retrievableMaxAttempts = properties.getRetrievableMaxAttempts();
        this.retrievableJitterFactor = properties.getRetrievableJitterFactor();
        this.timeoutInSeconds = properties.getTimeoutInSeconds();

        final SslContext sslContext = SslContextBuilder.forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build();

        final HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Duration.ofSeconds(this.timeoutInSeconds).toMillisPart())
            .secure(contextSpec -> contextSpec.sslContext(sslContext))
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(this.timeoutInSeconds));
                connection.addHandlerLast(new WriteTimeoutHandler(this.timeoutInSeconds));
            });


        final var encoder = new Jackson2JsonEncoder(Json.mapper());
        final var decoder = new Jackson2JsonDecoder(Json.mapper());
        final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
            .codecs(clientCodec -> {
                clientCodec.defaultCodecs().jackson2JsonEncoder(encoder);
                clientCodec.defaultCodecs().jackson2JsonDecoder(decoder);
                clientCodec.defaultCodecs().maxInMemorySize(DEFAULT_BUFFER_BYTES_SIZE_IN_MB);
            })
            .build();

        this.webClient = WebClient.builder()
            .baseUrl(properties.getBaseUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(exchangeStrategies)
            .build();
    }

    protected RetryBackoffSpec retryBackoffSpec() {
        return Retry.backoff(this.retrievableMaxAttempts, Duration.ofSeconds(this.retrievableMinBackoffInSeconds))
            .maxBackoff(Duration.ofSeconds(this.retrievableMaxBackoffInSeconds))
            .jitter(this.retrievableJitterFactor)
            .filter(this::allowRetry)
            .doBeforeRetry(retrySignal -> logRetry(retrySignal.totalRetries(), retrySignal.failure()))
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> throwsRetryExhaustedException(retrySignal.totalRetries(), retrySignal.failure()));
    }

    protected Duration timeoutInSeconds() {
        return Duration.ofSeconds(this.timeoutInSeconds);
    }

    protected <T> Function<WebClientResponseException, Mono<T>> fallbackResponseException() {
        return responseException -> {
            if (HttpStatus.NOT_FOUND == responseException.getStatusCode()) {
                return Mono.empty();
            }

            return Mono.error(responseException);
        };
    }

    protected boolean allowRetry(final Throwable ex) {
        if (ex instanceof WebClientResponseException exception) {
            return this.retrievableHttpStatusCodes.contains(exception.getStatusCode());
        }

        return ex instanceof TimeoutException;
    }

    protected void logRetry(final long totalRetries, final Throwable failure) {
        log.warn("Retrying client '{}': {}/{}", this.baseUrl, totalRetries + 1, this.retrievableMaxAttempts, failure);
    }

    protected Throwable throwsRetryExhaustedException(final long totalRetries, final Throwable throwable) {
        final String message = String.format("Retry client '%s' exhausted with %s attempts", this.baseUrl, totalRetries);
        return new RetryExhaustedException(message, throwable);
    }

}
