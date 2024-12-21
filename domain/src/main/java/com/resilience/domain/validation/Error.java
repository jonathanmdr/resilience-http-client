package com.resilience.domain.validation;

import com.resilience.domain.exception.NoStacktraceException;

public record Error(String message) {

    public Error {
        if (message == null || message.isBlank()) {
            throw new NoStacktraceException("Error message must be provided");
        }
    }

    public static Error of(final String message) {
        return new Error(message);
    }

}
