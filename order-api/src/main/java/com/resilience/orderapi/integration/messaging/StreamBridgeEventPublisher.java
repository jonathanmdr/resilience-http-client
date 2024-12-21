package com.resilience.orderapi.integration.messaging;

import com.resilience.domain.events.DomainEvent;
import com.resilience.domain.events.DomainEventPublisher;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public final class StreamBridgeEventPublisher implements DomainEventPublisher {

    private final StreamBridge streamBridge;

    public StreamBridgeEventPublisher(final StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Override
    public void publish(final DomainEvent event) {
        final Message<DomainEvent> domainEventMessage = MessageBuilder.withPayload(event).build();
        this.streamBridge.send(StreamBridgeBinding.AUTHORIZATION_ORDER_EVENTS.key(), domainEventMessage);
    }

}
