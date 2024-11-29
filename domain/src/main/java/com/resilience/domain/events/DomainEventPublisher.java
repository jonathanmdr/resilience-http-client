package com.resilience.domain.events;

@FunctionalInterface
public interface DomainEventPublisher {

    void publish(final DomainEvent event);

}
