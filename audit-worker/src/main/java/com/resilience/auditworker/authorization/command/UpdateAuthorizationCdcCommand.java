package com.resilience.auditworker.authorization.command;

import com.resilience.auditworker.authorization.consumer.model.AuthorizationEvent;
import com.resilience.auditworker.authorization.consumer.model.CdcAuthorizationEvent;
import com.resilience.auditworker.authorization.persistence.AuthorizationDataDocument;
import com.resilience.auditworker.authorization.persistence.AuthorizationDocument;
import com.resilience.auditworker.authorization.persistence.AuthorizationRepository;
import com.resilience.auditworker.common.CdcCommandHandler;
import com.resilience.auditworker.common.CdcPayloadEvent;
import com.resilience.auditworker.common.CdcPayloadEvent.CdcOperation;
import com.resilience.auditworker.common.CdcPayloadEvent.CdcSource;
import com.resilience.auditworker.common.OriginDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UpdateAuthorizationCdcCommand implements CdcCommandHandler<CdcAuthorizationEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateAuthorizationCdcCommand.class);

    private final AuthorizationRepository authorizationRepository;

    public UpdateAuthorizationCdcCommand(final AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
    }

    @Override
    public void handle(final CdcAuthorizationEvent event) {
        LOG.info("Updating authorization document: {}", event);
        final CdcPayloadEvent<AuthorizationEvent> payload = event.payload();
        final CdcOperation operation = payload.op();
        final CdcSource source = payload.source();
        final AuthorizationEvent before = payload.before();
        final AuthorizationEvent after = payload.after();
        final AuthorizationDocument entity = fromEventToDocument(source, operation, before, after);
        this.authorizationRepository.insert(entity);
    }

    @Override
    public CdcOperation op() {
        return CdcOperation.UPDATE;
    }

    private static AuthorizationDocument fromEventToDocument(
        final CdcSource source,
        final CdcOperation operation,
        final AuthorizationEvent before,
        final AuthorizationEvent after
    ) {
        final OriginDocument originDocument = new OriginDocument(
            source.db(),
            source.table(),
            source.file(),
            operation.name()
        );
        final AuthorizationDataDocument beforeDocument = new AuthorizationDataDocument(
            String.valueOf(before.id()),
            String.valueOf(before.orderId()),
            String.valueOf(before.customerId()),
            before.amount(),
            before.status()
        );
        final AuthorizationDataDocument afterDocument = new AuthorizationDataDocument(
            String.valueOf(after.id()),
            String.valueOf(after.orderId()),
            String.valueOf(after.customerId()),
            after.amount(),
            after.status()
        );
        return new AuthorizationDocument(beforeDocument, afterDocument, originDocument);
    }

}
