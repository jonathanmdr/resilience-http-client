package com.resilience.domain.exception;

import com.resilience.domain.validation.Error;

import java.util.Collections;
import java.util.List;

public class DomainException extends NoStacktraceException {

    private final transient List<Error> errors;

    private DomainException(final String message) {
        super(message);
        this.errors = Collections.emptyList();
    }

    private DomainException(final String message, final List<Error> errors) {
        super(message);
        this.errors = errors != null ? errors : Collections.emptyList();
    }

    public static DomainException with(final Error error) {
        return new DomainException(error.message());
    }

    public static DomainException with(final String message, final List<Error> errors) {
        return new DomainException(message, errors);
    }

    public List<Error> errors() {
        return this.errors;
    }

}
