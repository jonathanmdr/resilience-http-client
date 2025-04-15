package com.resilience.auditworker.order.command;

import com.resilience.auditworker.common.CdcCommandHandler;
import com.resilience.auditworker.common.CdcPayloadEvent;
import com.resilience.auditworker.common.CdcPayloadEvent.CdcOperation;
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
public class DeleteOrderCdcCommand implements CdcCommandHandler<CdcOrderEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteOrderCdcCommand.class);

    private final OrderRepository orderRepository;

    public DeleteOrderCdcCommand(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void handle(final CdcOrderEvent event) {
        LOG.info("Deleting authorization document: {}", event);
        final CdcPayloadEvent<OrderEvent> payload = event.payload();
        final CdcOperation operation = payload.op();
        final CdcPayloadEvent.CdcSource source = payload.source();
        final OrderEvent before = payload.before();
        final OriginDocument originDocument = new OriginDocument(
            source.db(),
            source.table(),
            source.file(),
            operation.name()
        );
        final OrderDocument entity = fromEventToDocument(before, originDocument);
        this.orderRepository.insert(entity);
    }

    @Override
    public CdcOperation op() {
        return CdcOperation.DELETE;
    }

    private static OrderDocument fromEventToDocument(final OrderEvent before, final OriginDocument originDocument) {
        final OrderDataDocument beforeDocument = new OrderDataDocument(
            String.valueOf(before.id()),
            String.valueOf(before.customerId()),
            before.amount(),
            before.status()
        );
        return new OrderDocument(beforeDocument, null, originDocument);
    }

}
