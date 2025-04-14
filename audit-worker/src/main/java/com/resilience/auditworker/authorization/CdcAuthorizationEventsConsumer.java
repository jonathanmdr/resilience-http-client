package com.resilience.auditworker.authorization;

import com.resilience.auditworker.authorization.persistence.AuthorizationAfterDocument;
import com.resilience.auditworker.authorization.persistence.AuthorizationBeforeDocument;
import com.resilience.auditworker.authorization.persistence.AuthorizationDocument;
import com.resilience.auditworker.authorization.persistence.AuthorizationRepository;
import com.resilience.auditworker.common.CdcPayloadEvent;
import com.resilience.auditworker.common.CdcPayloadEvent.CdcOperation;
import com.resilience.auditworker.common.CdcPayloadEvent.CdcSource;
import com.resilience.auditworker.common.OriginDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class CdcAuthorizationEventsConsumer implements Consumer<CdcAuthorizationEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(CdcAuthorizationEventsConsumer.class);

    private final AuthorizationRepository authorizationRepository;

    public CdcAuthorizationEventsConsumer(final AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
    }

    @Override
    public void accept(final CdcAuthorizationEvent event) {
        final CdcPayloadEvent<AuthorizationEvent> payload = event.payload();
        final CdcOperation operation = payload.op();
        final CdcSource source = payload.source();
        final AuthorizationEvent before = payload.before();
        final AuthorizationEvent after = payload.after();
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
                update(after, before, originEntity);
            }
            case DELETE -> {
                LOG.info("Deleting authorization document: {}", before);
                delete(before, originEntity);
            }
        }
    }

    private void create(final AuthorizationEvent after, final OriginDocument originEntity) {
        final AuthorizationAfterDocument afterDocument = new AuthorizationAfterDocument(
            String.valueOf(after.id()),
            String.valueOf(after.orderId()),
            String.valueOf(after.customerId()),
            after.amount(),
            after.status()
        );
        final AuthorizationDocument entity = new AuthorizationDocument(null, afterDocument, originEntity);
        this.authorizationRepository.insert(entity);
    }

    private void update(final AuthorizationEvent before, final AuthorizationEvent after, final OriginDocument originEntity) {
        final AuthorizationBeforeDocument beforeDocument = new AuthorizationBeforeDocument(
            String.valueOf(before.id()),
            String.valueOf(before.orderId()),
            String.valueOf(before.customerId()),
            before.amount(),
            before.status()
        );
        final AuthorizationAfterDocument afterDocument = new AuthorizationAfterDocument(
            String.valueOf(after.id()),
            String.valueOf(after.orderId()),
            String.valueOf(after.customerId()),
            after.amount(),
            after.status()
        );
        final AuthorizationDocument entity = new AuthorizationDocument(beforeDocument, afterDocument, originEntity);
        this.authorizationRepository.insert(entity);
    }

    private void delete(final AuthorizationEvent before, final OriginDocument originEntity) {
        final AuthorizationBeforeDocument beforeDocument = new AuthorizationBeforeDocument(
            String.valueOf(before.id()),
            String.valueOf(before.orderId()),
            String.valueOf(before.customerId()),
            before.amount(),
            before.status()
        );
        final AuthorizationDocument entity = new AuthorizationDocument(beforeDocument, null, originEntity);
        this.authorizationRepository.insert(entity);
    }

}
