package com.resilience.auditworker.order.consumer;

import com.resilience.auditworker.common.CdcPayloadEvent;
import com.resilience.auditworker.common.CdcPayloadEvent.CdcOperation;
import com.resilience.auditworker.order.consumer.model.CdcOrderEvent;
import com.resilience.auditworker.order.consumer.model.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class CdcOrderEventsConsumer implements Consumer<CdcOrderEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(CdcOrderEventsConsumer.class);

    private final Map<CdcOperation, Consumer<CdcOrderEvent>> cdcCommandHandlers;

    public CdcOrderEventsConsumer(final Map<CdcOperation, Consumer<CdcOrderEvent>> cdcCommandHandlers) {
        this.cdcCommandHandlers = cdcCommandHandlers;
    }

    @Override
    public void accept(final CdcOrderEvent event) {
        final CdcPayloadEvent<OrderEvent> payload = event.payload();
        final CdcOperation operation = payload.op();
        final Consumer<CdcOrderEvent> handler = this.cdcCommandHandlers.get(operation);
        Optional.ofNullable(handler)
            .ifPresentOrElse(
                consumer -> consumer.accept(event),
                () -> LOG.warn("No handler found for operation: '{}' event: {}", operation, event)
            );
    }

}
