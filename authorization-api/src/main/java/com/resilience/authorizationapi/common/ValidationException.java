package com.resilience.authorizationapi.common;

import com.resilience.domain.exception.NoStacktraceException;
import org.springframework.validation.BindingResult;

public final class ValidationException extends NoStacktraceException {

    private final transient BindingResult bindingResult;

    public ValidationException(final BindingResult bindingResult) {
        super("Validation failed");
        this.bindingResult = bindingResult;
    }

    public ValidationException(final BindingResult bindingResult, final Throwable cause) {
        super("Validation failed", cause);
        this.bindingResult = bindingResult;
    }

    public BindingResult bindingResult() {
        return this.bindingResult;
    }

}
