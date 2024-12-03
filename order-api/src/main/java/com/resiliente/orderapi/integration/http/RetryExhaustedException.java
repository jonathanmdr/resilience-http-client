package com.resiliente.orderapi.integration.http;

import com.resilience.domain.exception.NoStacktraceException;

public final class RetryExhaustedException extends NoStacktraceException {

    public RetryExhaustedException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
