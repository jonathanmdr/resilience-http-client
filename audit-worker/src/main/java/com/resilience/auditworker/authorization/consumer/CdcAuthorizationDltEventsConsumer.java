package com.resilience.auditworker.authorization.consumer;

import com.resilience.auditworker.authorization.persistence.AuthorizationDltDocument;
import com.resilience.auditworker.authorization.persistence.AuthorizationDltRepository;
import com.resilience.auditworker.common.MessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class CdcAuthorizationDltEventsConsumer implements Consumer<Message<String>> {

    private final AuthorizationDltRepository authorizationDltRepository;

    public CdcAuthorizationDltEventsConsumer(final AuthorizationDltRepository authorizationDltRepository) {
        this.authorizationDltRepository = authorizationDltRepository;
    }

    @Override
    public void accept(final Message<String> event) {
        final String payload = StringUtils.normalizeSpace(event.getPayload());
        final String error = MessageUtils.extractErrorFrom(event);
        final AuthorizationDltDocument document = new AuthorizationDltDocument(payload, error);
        this.authorizationDltRepository.insert(document);
    }

}
