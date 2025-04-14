package com.resilience.auditworker.order;

import com.resilience.auditworker.common.CdcPayloadEvent;
import com.resilience.auditworker.common.CdcPayloadEvent.CdcOperation;
import com.resilience.auditworker.common.CdcPayloadEvent.CdcSource;
import com.resilience.auditworker.common.OriginDocument;
import com.resilience.auditworker.order.persistence.OrderAfterDocument;
import com.resilience.auditworker.order.persistence.OrderBeforeDocument;
import com.resilience.auditworker.order.persistence.OrderDocument;
import com.resilience.auditworker.order.persistence.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class CdcOrderEventsConsumer implements Consumer<CdcOrderEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(CdcOrderEventsConsumer.class);

    private final OrderRepository orderRepository;

    public CdcOrderEventsConsumer(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void accept(final CdcOrderEvent event) {
        final CdcPayloadEvent<OrderEvent> payload = event.payload();
        final CdcOperation operation = payload.op();
        final CdcSource source = payload.source();
        final OrderEvent before = payload.before();
        final OrderEvent after = payload.after();
        final OriginDocument originEntity = new OriginDocument(
            source.db(),
            source.table(),
            source.file(),
            operation.name()
        );

        switch (operation) {
            case CREATE -> {
                LOG.info("Inserting authorization document: {}", after);
                create(after, originEntity);
            }
            case UPDATE -> {
                LOG.info("Updating authorization document: {}", after);
                update(before, after, originEntity);
            }
            case DELETE -> {
                LOG.info("Deleting authorization document: {}", before);
                delete(before, originEntity);
            }
        }
    }

    private void create(final OrderEvent after, final OriginDocument originEntity) {
        final OrderAfterDocument afterDocument = new OrderAfterDocument(
            String.valueOf(after.id()),
            String.valueOf(after.customerId()),
            after.amount(),
            after.status()
        );
        final OrderDocument entity = new OrderDocument(null, afterDocument, originEntity);
        this.orderRepository.insert(entity);
    }

    private void update(final OrderEvent before, final OrderEvent after, final OriginDocument originEntity) {
        final OrderBeforeDocument beforeDocument = new OrderBeforeDocument(
            String.valueOf(before.id()),
            String.valueOf(before.customerId()),
            before.amount(),
            before.status()
        );
        final OrderAfterDocument afterDocument = new OrderAfterDocument(
            String.valueOf(after.id()),
            String.valueOf(after.customerId()),
            after.amount(),
            after.status()
        );
        final OrderDocument entity = new OrderDocument(beforeDocument, afterDocument, originEntity);
        this.orderRepository.insert(entity);
    }

    private void delete(final OrderEvent before, final OriginDocument originEntity) {
        final OrderBeforeDocument beforeDocument = new OrderBeforeDocument(
            String.valueOf(before.id()),
            String.valueOf(before.customerId()),
            before.amount(),
            before.status()
        );
        final OrderDocument entity = new OrderDocument(beforeDocument, null, originEntity);
        this.orderRepository.insert(entity);
    }

}
