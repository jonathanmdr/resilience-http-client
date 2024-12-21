package com.resilience.orderapi.order;

import com.resilience.domain.authorization.AuthorizationStatus;
import com.resilience.domain.order.AuthorizationOrderStatusTranslatorService;
import com.resilience.domain.order.Order;
import com.resilience.domain.order.OrderId;
import com.resilience.orderapi.IntegrationTest;
import com.resilience.orderapi.order.persistence.OrderJpaEntity;
import com.resilience.orderapi.order.persistence.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@IntegrationTest
class OrderDatabaseGatewayIntegrationTest {

    @MockitoSpyBean
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDatabaseGateway subject;

    @Test
    void shouldReturnAnOrderWhenOrderIdExists() {
        final Order order = Order.create(UUID.randomUUID().toString(), BigDecimal.TEN);
        final OrderJpaEntity orderJpaEntity = OrderJpaEntity.from(order);
        this.orderRepository.saveAndFlush(orderJpaEntity);

        final Optional<Order> orderRetrieved = this.subject.findById(order.id());

        assertThat(orderRetrieved)
            .isPresent()
            .get()
            .satisfies(actual -> {
                assertThat(actual.id()).isEqualTo(order.id());
                assertThat(actual.customerId()).isEqualTo(order.customerId());
                assertThat(actual.amount())
                    .usingComparator(BigDecimal::compareTo)
                    .isEqualTo(order.amount());
                assertThat(actual.status()).isEqualTo(order.status());
            });

        verify(this.orderRepository).findById(order.id().value());
    }

    @Test
    void shouldReturnEmptyWhenOrderIdNotExists() {
        final OrderId orderId = OrderId.unique();
        final Optional<Order> orderRetrieved = this.subject.findById(orderId);

        assertThat(orderRetrieved)
            .isEmpty();

        verify(this.orderRepository).findById(orderId.value());
    }

    @Test
    void shouldReturnAnOrderWhenOrderHasCreated() {
        final Order order = Order.create(UUID.randomUUID().toString(), BigDecimal.TEN);

        final Order orderCreated = this.subject.create(order);

        assertThat(orderCreated)
            .satisfies(actual -> {
                assertThat(actual.id()).isEqualTo(order.id());
                assertThat(actual.customerId()).isEqualTo(order.customerId());
                assertThat(actual.amount())
                    .usingComparator(BigDecimal::compareTo)
                    .isEqualTo(order.amount());
                assertThat(actual.status()).isEqualTo(order.status());
            });

        verify(this.orderRepository).save(OrderJpaEntity.from(order));
    }

    @Test
    void shouldReturnAnOrderWhenOrderHasUpdated() {
        final Order newOrder = Order.create(UUID.randomUUID().toString(), BigDecimal.TEN);
        final OrderJpaEntity orderJpaEntity = OrderJpaEntity.from(newOrder);
        this.orderRepository.saveAndFlush(orderJpaEntity);

        final Order authorizedOrder = newOrder.authorize(AuthorizationOrderStatusTranslatorService.create(AuthorizationStatus.APPROVED));
        final Order orderUpdated = this.subject.update(authorizedOrder);

        assertThat(orderUpdated)
            .satisfies(actual -> {
                assertThat(actual.id()).isEqualTo(authorizedOrder.id());
                assertThat(actual.customerId()).isEqualTo(authorizedOrder.customerId());
                assertThat(actual.amount())
                    .usingComparator(BigDecimal::compareTo)
                    .isEqualTo(authorizedOrder.amount());
                assertThat(actual.status()).isEqualTo(authorizedOrder.status());
            });

        verify(this.orderRepository).save(OrderJpaEntity.from(authorizedOrder));
    }

}
