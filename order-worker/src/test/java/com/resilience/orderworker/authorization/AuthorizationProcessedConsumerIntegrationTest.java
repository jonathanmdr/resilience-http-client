package com.resilience.orderworker.authorization;

import com.resilience.application.order.update.UpdateOrderInput;
import com.resilience.application.order.update.UpdateOrderUseCase;
import com.resilience.domain.authorization.Authorization;
import com.resilience.domain.authorization.AuthorizationProcessedEvent;
import com.resilience.domain.authorization.AuthorizationStatusTranslatorService;
import com.resilience.domain.order.Order;
import com.resilience.orderworker.KafkaIntegrationTest;
import com.resilience.orderworker.order.persistence.OrderJpaEntity;
import com.resilience.orderworker.order.persistence.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@KafkaIntegrationTest
class AuthorizationProcessedConsumerIntegrationTest {

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoSpyBean
    private UpdateOrderUseCase updateOrderUseCase;

    @MockitoSpyBean
    private AuthorizationProcessedConsumer subject;

    @Test
    void shouldBeProcessAuthorizationProcessedEvent() {
        final Order order = Order.create(UUID.randomUUID().toString(), BigDecimal.TEN);
        this.orderRepository.saveAndFlush(OrderJpaEntity.from(order));

        final Authorization authorization = Authorization.create(order.id().value(), order.customerId(), order.amount());
        final Authorization approvedAuthorization = authorization.authorize(AuthorizationStatusTranslatorService.create("APPROVED"));
        approvedAuthorization.events().forEach(event -> this.streamBridge.send("authorizationProcessedConsumer-in-0", event));

        verify(this.subject).accept(argThat(authorizationProcessedEvent -> {
            assertThat(authorizationProcessedEvent.authorizationId()).isEqualTo(approvedAuthorization.id().value());
            assertThat(authorizationProcessedEvent.orderId()).isEqualTo(approvedAuthorization.orderId());
            assertThat(authorizationProcessedEvent.orderAmount()).isEqualTo(approvedAuthorization.orderAmount());
            assertThat(authorizationProcessedEvent.status()).isEqualTo(approvedAuthorization.status());
            assertThat(authorizationProcessedEvent.occurredOn())
                .isNotNull()
                .isBefore(Instant.now());
            return true;
        }));

        verify(this.updateOrderUseCase).execute(argThat(updateOrderInput -> {
            assertThat(updateOrderInput.orderId()).isEqualTo(approvedAuthorization.orderId());
            assertThat(updateOrderInput.status()).isEqualTo(approvedAuthorization.status());
            return true;
        }));
    }

    @Test
    void shouldBeDoNotProcessAnInvalidAuthorizationProcessedEvent() {
        final AuthorizationProcessedEvent event = AuthorizationProcessedEvent.with(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            BigDecimal.TEN,
            null,
            null
        );

        assertThatThrownBy(() -> this.streamBridge.send("authorizationProcessedConsumer-in-0", event))
            .isExactlyInstanceOf(MessageHandlingException.class);

        verify(this.subject).accept(argThat(authorizationProcessedEvent -> {
            assertThat(authorizationProcessedEvent.authorizationId()).isEqualTo(event.authorizationId());
            assertThat(authorizationProcessedEvent.orderId()).isEqualTo(event.orderId());
            assertThat(authorizationProcessedEvent.orderAmount()).isEqualTo(event.orderAmount());
            assertThat(authorizationProcessedEvent.status()).isNull();
            assertThat(authorizationProcessedEvent.occurredOn()).isNull();
            return true;
        }));

        verify(this.updateOrderUseCase, never()).execute(any(UpdateOrderInput.class));
    }

}
