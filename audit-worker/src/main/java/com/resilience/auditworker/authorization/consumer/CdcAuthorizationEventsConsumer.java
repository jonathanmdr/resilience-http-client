package com.resilience.auditworker.authorization.consumer;

import com.resilience.auditworker.authorization.consumer.model.AuthorizationEvent;
import com.resilience.auditworker.authorization.consumer.model.CdcAuthorizationEvent;
import com.resilience.auditworker.common.CdcPayloadEvent;
import com.resilience.auditworker.common.CdcPayloadEvent.CdcOperation;
import com.resilience.auditworker.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class CdcAuthorizationEventsConsumer implements Consumer<CdcAuthorizationEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(CdcAuthorizationEventsConsumer.class);

    private final Map<CdcOperation, Consumer<CdcAuthorizationEvent>> cdcCommandHandlers;
    private final TransactionManager transactionManager;

    public CdcAuthorizationEventsConsumer(
        final Map<CdcOperation, Consumer<CdcAuthorizationEvent>> cdcCommandHandlers,
        final TransactionManager transactionManager
    ) {
        this.cdcCommandHandlers = cdcCommandHandlers;
        this.transactionManager = transactionManager;
    }

    @Override
    public void accept(final CdcAuthorizationEvent event) {
        final CdcPayloadEvent<AuthorizationEvent> payload = event.payload();
        final CdcOperation operation = payload.op();
        final Consumer<CdcAuthorizationEvent> handler = this.cdcCommandHandlers.get(operation);
        Optional.ofNullable(handler)
            .ifPresentOrElse(
                consumer -> this.transactionManager.runInTransactionContext(() -> consumer.accept(event)),
                () -> LOG.warn("No handler found for operation: '{}' event: {}", operation, event)
            );
    }

}
