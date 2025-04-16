package com.resilience.auditworker.order.command;

import com.resilience.auditworker.common.CdcCommandHandler;
import com.resilience.auditworker.common.CdcPayloadEvent;
import com.resilience.auditworker.common.CdcPayloadEvent.CdcOperation;
import com.resilience.auditworker.common.CdcPayloadEvent.CdcSource;
import com.resilience.auditworker.common.OriginDocument;
import com.resilience.auditworker.order.consumer.model.CdcOrderEvent;
import com.resilience.auditworker.order.consumer.model.OrderEvent;
import com.resilience.auditworker.order.persistence.OrderDataDocument;
import com.resilience.auditworker.order.persistence.OrderDocument;
import com.resilience.auditworker.order.persistence.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderCdcCommand implements CdcCommandHandler<CdcOrderEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(CreateOrderCdcCommand.class);

    private final OrderRepository orderRepository;

    public CreateOrderCdcCommand(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void handle(final CdcOrderEvent event) {
        LOG.info("Inserting authorization document: {}", event);
        final CdcPayloadEvent<OrderEvent> payload = event.payload();
        final CdcOperation operation = payload.op();
        final CdcSource source = payload.source();
        final OrderEvent after = payload.after();
        final OrderDocument entity = fromEventToDocument(source, operation, after);
        this.orderRepository.insert(entity);
    }

    @Override
    public CdcOperation op() {
        return CdcOperation.CREATE;
    }

    private static OrderDocument fromEventToDocument(
        final CdcSource source,
        final CdcOperation operation,
        final OrderEvent after
    ) {
        final OriginDocument originDocument = new OriginDocument(
            source.db(),
            source.table(),
            source.file(),
            operation.name()
        );
        final OrderDataDocument afterDocument = new OrderDataDocument(
            String.valueOf(after.id()),
            String.valueOf(after.customerId()),
            after.amount(),
            after.status()
        );
        return new OrderDocument(null, afterDocument, originDocument);
    }

}
