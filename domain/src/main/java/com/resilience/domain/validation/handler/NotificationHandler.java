package com.resilience.domain.validation.handler;

import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

public final class NotificationHandler implements ValidationHandler {

    private final List<Error> errors;

    private NotificationHandler(final List<Error> errors) {
        this.errors = errors;
    }

    public static NotificationHandler create() {
        return new NotificationHandler(new ArrayList<>());
    }

    public static NotificationHandler create(final Error error) {
        return create().append(error);
    }

    public static NotificationHandler create(final Throwable throwable) {
        return create().append(new Error(throwable.getMessage()));
    }

    @Override
    public NotificationHandler append(final Error error) {
        this.errors.add(error);
        return this;
    }

    @Override
    public List<Error> errors() {
        return this.errors;
    }

}
