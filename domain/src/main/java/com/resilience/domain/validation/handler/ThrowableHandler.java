package com.resilience.domain.validation.handler;

import com.resilience.domain.exception.DomainException;
import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;

public final class ThrowableHandler implements ValidationHandler {

    private ThrowableHandler() { }

    public static ThrowableHandler create() {
        return new ThrowableHandler();
    }

    @Override
    public ThrowableHandler append(final Error error) {
        throw DomainException.with(error);
    }

    @Override
    public void validate(final Validation validation) {
        try {
            validation.validate(this);
        } catch (final Exception ex) {
            throw DomainException.with(Error.of(ex.getMessage()));
        }
    }

}
