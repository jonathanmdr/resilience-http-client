package com.resilience.domain.exception;

import com.resilience.domain.validation.Error;

public class DomainException extends NoStacktraceException {

    private DomainException(final String message) {
        super(message);
    }

    public static DomainException with(final Error error) {
        return new DomainException(error.message());
    }

}
