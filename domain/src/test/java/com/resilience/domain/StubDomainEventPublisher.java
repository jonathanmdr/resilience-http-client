package com.resilience.domain;

import com.resilience.domain.events.DomainEvent;
import com.resilience.domain.events.DomainEventPublisher;

public final class StubDomainEventPublisher implements DomainEventPublisher {

    @Override
    public void publish(final DomainEvent event) {
        // I do nothing because I'm a single stub
    }

}
