package com.resilience.orderworker.authorization.persistence;

import com.resilience.domain.authorization.Authorization;
import com.resilience.domain.authorization.AuthorizationProcessedEvent;
import com.resilience.orderworker.DatabaseRepositoryIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.orm.jpa.JpaSystemException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DatabaseRepositoryIntegrationTest
class AuthorizationProcessedDltRepositoryIntegrationTest {

    @Autowired
    private AuthorizationProcessedDltRepository authorizationProcessedDltRepository;

    @Test
    void shouldBeSaveAnAuthorizationProcessedDlt() {
        final String orderId = UUID.randomUUID().toString();
        final String customerId = UUID.randomUUID().toString();
        final Authorization authorization = Authorization.create(orderId, customerId, BigDecimal.TEN);
        final AuthorizationProcessedEvent event = AuthorizationProcessedEvent.with(
            authorization.id().value(),
            authorization.orderId(),
            authorization.orderAmount(),
            authorization.status(),
            Instant.now()
        );
        final Message<AuthorizationProcessedEvent> message = MessageBuilder.withPayload(event)
            .setHeader("x-exception-stacktrace", "a stacktrace".getBytes(StandardCharsets.UTF_8))
            .build();
        final AuthorizationProcessedDltJpaEntity entity = AuthorizationProcessedDltJpaEntity.from(message);

        final AuthorizationProcessedDltJpaEntity actual = this.authorizationProcessedDltRepository.saveAndFlush(entity);

        assertThat(actual)
            .isNotNull()
            .usingRecursiveComparison()
            .isEqualTo(entity);
    }

    @Test
    void shouldBeDoNotSaveAnAuthorizationProcessedDltWithoutId() {
        final String orderId = UUID.randomUUID().toString();
        final String customerId = UUID.randomUUID().toString();
        final Authorization authorization = Authorization.create(orderId, customerId, BigDecimal.TEN);
        final AuthorizationProcessedEvent event = AuthorizationProcessedEvent.with(
            authorization.id().value(),
            authorization.orderId(),
            authorization.orderAmount(),
            authorization.status(),
            Instant.now()
        );
        final Message<AuthorizationProcessedEvent> message = MessageBuilder.withPayload(event)
            .setHeader("x-exception-stacktrace", "a stacktrace".getBytes(StandardCharsets.UTF_8))
            .build();
        final AuthorizationProcessedDltJpaEntity entity = AuthorizationProcessedDltJpaEntity.from(message);
        entity.setId(null);

        assertThatThrownBy(() -> this.authorizationProcessedDltRepository.saveAndFlush(entity))
            .isExactlyInstanceOf(JpaSystemException.class)
            .hasMessage("Identifier of entity 'com.resilience.orderworker.authorization.persistence.AuthorizationProcessedDltJpaEntity' must be manually assigned before calling 'persist()'");
    }

    @Test
    void shouldBeDoNotSaveAnAuthorizationProcessedDltWithoutError() {
        final String orderId = UUID.randomUUID().toString();
        final String customerId = UUID.randomUUID().toString();
        final Authorization authorization = Authorization.create(orderId, customerId, BigDecimal.TEN);
        final AuthorizationProcessedEvent event = AuthorizationProcessedEvent.with(
            authorization.id().value(),
            authorization.orderId(),
            authorization.orderAmount(),
            authorization.status(),
            Instant.now()
        );
        final Message<AuthorizationProcessedEvent> message = MessageBuilder.withPayload(event)
            .setHeader("x-exception-stacktrace", "a stacktrace".getBytes(StandardCharsets.UTF_8))
            .build();
        final AuthorizationProcessedDltJpaEntity entity = AuthorizationProcessedDltJpaEntity.from(message);
        entity.setError(null);

        assertThatThrownBy(() -> this.authorizationProcessedDltRepository.saveAndFlush(entity))
            .isExactlyInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("could not execute batch [NULL not allowed for column \"error\"");
    }

    @Test
    void shouldBeDoNotSaveAnAuthorizationProcessedDltWithoutPayload() {
        final String orderId = UUID.randomUUID().toString();
        final String customerId = UUID.randomUUID().toString();
        final Authorization authorization = Authorization.create(orderId, customerId, BigDecimal.TEN);
        final AuthorizationProcessedEvent event = AuthorizationProcessedEvent.with(
            authorization.id().value(),
            authorization.orderId(),
            authorization.orderAmount(),
            authorization.status(),
            Instant.now()
        );
        final Message<AuthorizationProcessedEvent> message = MessageBuilder.withPayload(event)
            .setHeader("x-exception-stacktrace", "a stacktrace".getBytes(StandardCharsets.UTF_8))
            .build();
        final AuthorizationProcessedDltJpaEntity entity = AuthorizationProcessedDltJpaEntity.from(message);
        entity.setPayload(null);

        assertThatThrownBy(() -> this.authorizationProcessedDltRepository.saveAndFlush(entity))
            .isExactlyInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("could not execute batch [NULL not allowed for column \"payload\"");
    }

}
