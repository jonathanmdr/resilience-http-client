package com.resilience.orderworker.authorization;

import com.resilience.domain.authorization.Authorization;
import com.resilience.domain.authorization.AuthorizationProcessedEvent;
import com.resilience.orderworker.KafkaIntegrationTest;
import com.resilience.orderworker.authorization.persistence.AuthorizationProcessedDltJpaEntity;
import com.resilience.orderworker.authorization.persistence.AuthorizationProcessedDltRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@KafkaIntegrationTest
class AuthorizationProcessedDltConsumerIntegrationTest {

    @Autowired
    private StreamBridge streamBridge;

    @MockitoSpyBean
    private AuthorizationProcessedDltConsumer subject;

    @MockitoSpyBean
    private AuthorizationProcessedDltRepository authorizationProcessedDltRepository;

    @Test
    void shouldBeProcessAuthorizationProcessedDltEvent() {
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

        this.streamBridge.send("authorizationProcessedDltConsumer-in-0", message);

        verify(this.subject).accept(argThat(authorizationProcessedEventMessage -> {
            final AuthorizationProcessedEvent currentEvent = authorizationProcessedEventMessage.getPayload();
            assertThat(currentEvent.authorizationId()).isEqualTo(event.authorizationId());
            assertThat(currentEvent.orderId()).isEqualTo(event.orderId());
            assertThat(currentEvent.orderAmount()).isEqualTo(event.orderAmount());
            assertThat(currentEvent.status()).isEqualTo(event.status());
            assertThat(currentEvent.occurredOn())
                .isNotNull()
                .isBefore(Instant.now());
            return true;
        }));

        verify(this.authorizationProcessedDltRepository).save(AuthorizationProcessedDltJpaEntity.from(message));
    }

}
