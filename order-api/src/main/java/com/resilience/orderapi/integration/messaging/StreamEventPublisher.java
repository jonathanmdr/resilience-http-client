package com.resilience.orderapi.integration.messaging;

import com.resilience.domain.events.DomainEvent;
import com.resilience.domain.events.DomainEventPublisher;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public final class StreamEventPublisher implements DomainEventPublisher {

    private final StreamBridge streamBridge;
    private final StreamBinding streamBinding;

    private StreamEventPublisher(final StreamBridge streamBridge, final StreamBinding streamBinding) {
        this.streamBridge = streamBridge;
        this.streamBinding = streamBinding;
    }

    public static StreamEventPublisher create(final StreamBridge streamBridge, final StreamBinding streamBinding) {
        return new StreamEventPublisher(streamBridge, streamBinding);
    }

    @Override
    public void publish(final DomainEvent event) {
        final Message<DomainEvent> domainEventMessage = MessageBuilder.withPayload(event).build();
        this.streamBridge.send(this.streamBinding.key(), domainEventMessage);
    }

}
