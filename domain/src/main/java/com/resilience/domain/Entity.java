package com.resilience.domain;

import com.resilience.domain.events.DomainEvent;
import com.resilience.domain.events.DomainEventPublisher;
import com.resilience.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Entity<T extends Identifier> {

    protected final T id;
    private final List<DomainEvent> events;

    protected Entity(final T id, final List<DomainEvent> events) {
        this.id = Objects.requireNonNull(id, "Id must not be null");
        this.events = Objects.requireNonNullElseGet(events, ArrayList::new);
    }

    public abstract void validate(final ValidationHandler handler);

    public T id() {
        return this.id;
    }

    public List<DomainEvent> events() {
        return Collections.unmodifiableList(this.events);
    }

    public void addEvent(final DomainEvent event) {
        if (Objects.nonNull(event)) {
            this.events.add(event);
        }
    }

    public void dispatch(final DomainEventPublisher publisher) {
        if (Objects.nonNull(publisher)) {
            this.events.forEach(publisher::publish);
            this.events.clear();
        }
    }

}
