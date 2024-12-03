package com.resiliente.orderapi.integration.http;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.http.HttpStatusCode;

import java.util.List;

public class BaseClientProperties {

    private static final List<Integer> DEFAULT_RETRIEVABLE_HTTP_STATUS_CODES = List.of(500, 502, 503, 504);
    private static final int DEFAULT_MIN_BACKOFF_IN_SECONDS = 2;
    private static final int DEFAULT_MAX_BACKOFF_IN_SECONDS = 10;
    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final double DEFAULT_JITTER_FACTOR = 0.75;
    private static final int DEFAULT_TIMEOUT_IN_SECONDS = 5;
    private static final double EPSILON = 1e-9;

    @NotBlank
    @URL
    private String baseUrl;

    private List<Integer> retrievableHttpStatusCodes;
    private int retrievableMinBackoffInSeconds;
    private int retrievableMaxBackoffInSeconds;
    private int retrievableMaxAttempts;
    private double retrievableJitterFactor;
    private int timeoutInSeconds;

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public List<Integer> getRetrievableHttpStatusCodes() {
        if (this.retrievableHttpStatusCodes == null || this.retrievableHttpStatusCodes.isEmpty()) {
            return DEFAULT_RETRIEVABLE_HTTP_STATUS_CODES;
        }

        return this.retrievableHttpStatusCodes;
    }

    public List<HttpStatusCode> getRetrievableHttpErrors() {
        if (this.retrievableHttpStatusCodes == null || this.retrievableHttpStatusCodes.isEmpty()) {
            return DEFAULT_RETRIEVABLE_HTTP_STATUS_CODES.stream()
                .map(HttpStatusCode::valueOf)
                .toList();
        }

        return this.retrievableHttpStatusCodes.stream()
            .map(HttpStatusCode::valueOf)
            .toList();
    }

    public int getRetrievableMinBackoffInSeconds() {
        return this.retrievableMinBackoffInSeconds == 0 ? DEFAULT_MIN_BACKOFF_IN_SECONDS : this.retrievableMinBackoffInSeconds;
    }

    public int getRetrievableMaxBackoffInSeconds() {
        return this.retrievableMaxBackoffInSeconds == 0 ? DEFAULT_MAX_BACKOFF_IN_SECONDS : this.retrievableMaxBackoffInSeconds;
    }

    public int getRetrievableMaxAttempts() {
        return this.retrievableMaxAttempts == 0 ? DEFAULT_MAX_ATTEMPTS : this.retrievableMaxAttempts;
    }

    public double getRetrievableJitterFactor() {
        return Math.abs(this.retrievableJitterFactor - 0.0) < EPSILON ? DEFAULT_JITTER_FACTOR : this.retrievableJitterFactor;
    }

    public int getTimeoutInSeconds() {
        return this.timeoutInSeconds == 0 ? DEFAULT_TIMEOUT_IN_SECONDS : this.timeoutInSeconds;
    }

    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setRetrievableHttpStatusCodes(final List<Integer> retrievableHttpStatusCodes) {
        this.retrievableHttpStatusCodes = retrievableHttpStatusCodes;
    }

    public void setRetrievableMinBackoffInSeconds(final int retrievableMinBackoffInSeconds) {
        this.retrievableMinBackoffInSeconds = retrievableMinBackoffInSeconds;
    }

    public void setRetrievableMaxBackoffInSeconds(final int retrievableMaxBackoffInSeconds) {
        this.retrievableMaxBackoffInSeconds = retrievableMaxBackoffInSeconds;
    }

    public void setRetrievableMaxAttempts(final int retrievableMaxAttempts) {
        this.retrievableMaxAttempts = retrievableMaxAttempts;
    }

    public void setRetrievableJitterFactor(final double retrievableJitterFactor) {
        this.retrievableJitterFactor = retrievableJitterFactor;
    }

    public void setTimeoutInSeconds(final int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }

}
