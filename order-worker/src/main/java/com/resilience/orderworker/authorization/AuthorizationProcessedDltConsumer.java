package com.resilience.orderworker.authorization;

import com.resilience.domain.authorization.AuthorizationProcessedEvent;
import com.resilience.orderworker.authorization.persistence.AuthorizationProcessedDltJpaEntity;
import com.resilience.orderworker.authorization.persistence.AuthorizationProcessedDltRepository;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Component
public class AuthorizationProcessedDltConsumer implements Consumer<Message<AuthorizationProcessedEvent>> {

    private final AuthorizationProcessedDltRepository authorizationProcessedDltRepository;

    public AuthorizationProcessedDltConsumer(final AuthorizationProcessedDltRepository authorizationProcessedDltRepository) {
        this.authorizationProcessedDltRepository = authorizationProcessedDltRepository;
    }

    @Transactional
    @Override
    public void accept(final Message<AuthorizationProcessedEvent> message) {
        final AuthorizationProcessedDltJpaEntity entity = AuthorizationProcessedDltJpaEntity.from(message);
        this.authorizationProcessedDltRepository.save(entity);
    }

}
