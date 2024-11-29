package com.resilience.domain;

import com.resilience.domain.events.DomainEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot<T extends Identifier> extends Entity<T> {

    protected AggregateRoot(final T id) {
        this(id, new ArrayList<>());
    }

    protected AggregateRoot(final T id, final List<DomainEvent> events) {
        super(id, events);
    }

}
