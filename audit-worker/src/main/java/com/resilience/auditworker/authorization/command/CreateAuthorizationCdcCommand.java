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
public class CreateAuthorizationCdcCommand implements CdcCommandHandler<CdcAuthorizationEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(CreateAuthorizationCdcCommand.class);

    private final AuthorizationRepository authorizationRepository;

    public CreateAuthorizationCdcCommand(final AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
    }

    @Override
    public void handle(final CdcAuthorizationEvent event) {
        LOG.info("Inserting authorization document: {}", event);
        final CdcPayloadEvent<AuthorizationEvent> payload = event.payload();
        final CdcOperation operation = payload.op();
        final CdcSource source = payload.source();
        final AuthorizationEvent after = payload.after();
        final AuthorizationDocument entity = fromEventToDocument(source, operation, after);
        this.authorizationRepository.insert(entity);
    }

    private static AuthorizationDocument fromEventToDocument(final CdcSource source, final CdcOperation operation, final AuthorizationEvent after) {
        final OriginDocument originDocument = new OriginDocument(
            source.db(),
            source.table(),
            source.file(),
            operation.name()
        );
        final AuthorizationDataDocument afterDocument = new AuthorizationDataDocument(
            String.valueOf(after.id()),
            String.valueOf(after.orderId()),
            String.valueOf(after.customerId()),
            after.amount(),
            after.status()
        );
        return new AuthorizationDocument(null, afterDocument, originDocument);
    }

    @Override
    public CdcOperation op() {
        return CdcOperation.CREATE;
    }

}
