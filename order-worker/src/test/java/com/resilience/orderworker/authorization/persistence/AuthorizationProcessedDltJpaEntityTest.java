package com.resilience.orderworker.authorization.persistence;

import com.resilience.domain.authorization.Authorization;
import com.resilience.domain.authorization.AuthorizationProcessedEvent;
import com.resilience.orderworker.configuration.Json;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationProcessedDltJpaEntityTest {

    @Test
    void shouldBeCreateAnAuthorizationProcessedDltJpaEntityFromAuthorization() {
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

        assertThat(entity)
            .isNotNull()
            .satisfies(authorizationProcessedDltJpaEntity -> {
                assertThat(authorizationProcessedDltJpaEntity.getId()).isNotNull();
                assertThat(authorizationProcessedDltJpaEntity.getError()).isEqualTo("a stacktrace");
                assertThat(authorizationProcessedDltJpaEntity.getPayload()).isEqualTo(Json.writeValueAsString(event));
            });
    }

    @Test
    void shouldBeTheSameEntity() {
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
        final Message<AuthorizationProcessedEvent> message = MessageBuilder.withPayload(event).build();
        final AuthorizationProcessedDltJpaEntity entityOne = AuthorizationProcessedDltJpaEntity.from(message);
        final AuthorizationProcessedDltJpaEntity entityTwo = AuthorizationProcessedDltJpaEntity.from(message);

        assertThat(entityOne)
            .hasSameHashCodeAs(entityTwo)
            .isEqualTo(entityTwo);
    }

    @Test
    void shouldBeNotSameEntity() {
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
        final AuthorizationProcessedDltJpaEntity entityOne = AuthorizationProcessedDltJpaEntity.from(message);
        final AuthorizationProcessedDltJpaEntity entityTwo = AuthorizationProcessedDltJpaEntity.from(message);

        // by id
        entityOne.setId("12345");
        assertThat(entityOne)
            .isNotEqualTo(entityTwo)
            .doesNotHaveSameHashCodeAs(entityTwo);
        entityOne.setId(authorization.id().value());
        entityOne.setId(null);
        assertThat(entityOne)
            .isNotEqualTo(entityTwo)
            .doesNotHaveSameHashCodeAs(entityTwo);
        entityOne.setId(authorization.id().value());

        // by error
        entityOne.setError("another error");
        assertThat(entityOne)
            .isNotEqualTo(entityTwo)
            .doesNotHaveSameHashCodeAs(entityTwo);
        entityOne.setError(null);
        assertThat(entityOne)
            .isNotEqualTo(entityTwo)
            .doesNotHaveSameHashCodeAs(entityTwo);
        entityOne.setError("a stacktrace");

        // by payload
        entityOne.setPayload("{}");
        assertThat(entityOne)
            .isNotEqualTo(entityTwo)
            .doesNotHaveSameHashCodeAs(entityTwo);
        entityOne.setPayload(null);
        assertThat(entityOne)
            .isNotEqualTo(entityTwo)
            .doesNotHaveSameHashCodeAs(entityTwo);
        entityOne.setPayload(Json.writeValueAsString(event));

        // by class
        assertThat(entityOne).isNotEqualTo(authorization); // NOSONAR
    }

}
