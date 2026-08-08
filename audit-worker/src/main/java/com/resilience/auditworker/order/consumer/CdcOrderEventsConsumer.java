package com.resilience.auditworker.order.consumer;

import com.resilience.auditworker.common.CdcPayloadEvent;
import com.resilience.auditworker.common.CdcPayloadEvent.CdcOperation;
import com.resilience.auditworker.order.consumer.model.CdcOrderEvent;
import com.resilience.auditworker.order.consumer.model.OrderEvent;
import com.resilience.auditworker.transaction.TransactionManager;
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
    private final TransactionManager transactionManager;

    public CdcOrderEventsConsumer(
        final Map<CdcOperation, Consumer<CdcOrderEvent>> cdcCommandHandlers,
        final TransactionManager transactionManager
    ) {
        this.cdcCommandHandlers = cdcCommandHandlers;
        this.transactionManager = transactionManager;
    }

    @Override
    public void accept(final CdcOrderEvent event) {
        final CdcPayloadEvent<OrderEvent> payload = event.payload();
        final CdcOperation operation = payload.op();
        final Consumer<CdcOrderEvent> handler = this.cdcCommandHandlers.get(operation);
        Optional.ofNullable(handler)
            .ifPresentOrElse(
                consumer -> this.transactionManager.runInTransactionContext(() -> consumer.accept(event)),
                () -> LOG.warn("No handler found for operation: '{}' event: {}", operation, event)
            );
    }

}
