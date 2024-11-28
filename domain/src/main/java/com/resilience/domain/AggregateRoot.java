package com.resilience.domain;

public abstract class AggregateRoot<T extends Identifier> extends Entity<T> {

    protected AggregateRoot(final T id) {
        super(id);
    }

}
