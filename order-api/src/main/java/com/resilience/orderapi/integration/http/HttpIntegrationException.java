package com.resilience.orderapi.integration.http;

import com.resilience.domain.exception.NoStacktraceException;
import com.resilience.domain.validation.Error;

import java.util.ArrayList;
import java.util.List;

public final class HttpIntegrationException extends NoStacktraceException {

    private final transient List<Error> errors;

    private HttpIntegrationException(final String message) {
        super(message);
        this.errors = new ArrayList<>();
    }

    private HttpIntegrationException(final String message, final List<Error> errors) {
        super(message);
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    public static HttpIntegrationException with(final Error error) {
        return new HttpIntegrationException(error.message());
    }

    public static HttpIntegrationException with(final String message, final List<Error> errors) {
        return new HttpIntegrationException(message, errors);
    }

    public List<Error> errors() {
        return this.errors;
    }

}
