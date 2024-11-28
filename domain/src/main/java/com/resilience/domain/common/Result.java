package com.resilience.domain.common;

import com.resilience.domain.exception.DomainException;
import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;

public final class Result<S, E extends ValidationHandler> {

    private final S success;
    private final E error;

    private Result(final S success, final E error) {
        this.success = success;
        this.error = error;
    }

    public static <S, E extends ValidationHandler> Result<S, E> success(final S sucess) {
        return new Result<>(sucess, null);
    }

    public static <S, E extends ValidationHandler> Result<S, E> error(final E error) {
        return new Result<>(null, error);
    }

    public S success() {
        if (hasError()) {
            throw DomainException.with(new Error("The 'success' object cannot be invoked when an 'error' object exists"));
        }

        return this.success;
    }

    public E error() {
        if (hasSuccess()) {
            throw DomainException.with(new Error("The 'error' object cannot be invoked when an 'success' object exists"));
        }

        return this.error;
    }

    public boolean hasSuccess() {
        return this.success != null;
    }

    public boolean hasError() {
        return this.error != null;
    }

}
