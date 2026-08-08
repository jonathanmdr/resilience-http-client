package com.resilience.auditworker.order.consumer;

import com.resilience.auditworker.common.MessageUtils;
import com.resilience.auditworker.order.persistence.OrderDltDocument;
import com.resilience.auditworker.order.persistence.OrderDltRepository;
import com.resilience.auditworker.transaction.TransactionManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class CdcOrderDltEventsConsumer implements Consumer<Message<String>> {

    private final OrderDltRepository orderDltRepository;
    private final TransactionManager transactionManager;

    public CdcOrderDltEventsConsumer(
        final OrderDltRepository orderDltRepository,
        final TransactionManager transactionManager
    ) {
        this.orderDltRepository = orderDltRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public void accept(final Message<String> event) {
        final String payload = StringUtils.normalizeSpace(event.getPayload());
        final String error = MessageUtils.extractErrorFrom(event);
        final OrderDltDocument document = new OrderDltDocument(payload, error);
        this.transactionManager.runInDefaultContext(() -> this.orderDltRepository.insert(document));
    }

}
