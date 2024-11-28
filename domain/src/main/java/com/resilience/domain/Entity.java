package com.resilience.domain;

import com.resilience.domain.validation.ValidationHandler;

import java.util.Objects;

public abstract class Entity<T extends Identifier> {

    protected final T id;

    protected Entity(final T id) {
        this.id = Objects.requireNonNull(id, "Id must not be null");
    }

    public abstract void validate(final ValidationHandler handler);

    public T id() {
        return this.id;
    }

}
