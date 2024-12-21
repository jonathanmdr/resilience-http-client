package com.resilience.orderapi.order.persistence;

import com.resilience.domain.order.Order;
import com.resilience.orderapi.DatabaseRepositoryIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DatabaseRepositoryIntegrationTest
class OrderRepositoryIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldBeSaveAnOrder() {
        final Order order = Order.create("1234", BigDecimal.TEN);
        final OrderJpaEntity entity = OrderJpaEntity.from(order);

        final OrderJpaEntity actual = this.orderRepository.saveAndFlush(entity);

        assertThat(actual.toAggregate())
            .isNotNull()
            .usingRecursiveComparison()
            .isEqualTo(order);
    }

    @Test
    void shouldBeDoNotSaveAnOrderWithoutId() {
        final Order order = Order.create("1234", BigDecimal.TEN);
        final OrderJpaEntity entity = OrderJpaEntity.from(order);
        entity.setId(null);

        assertThatThrownBy(() -> this.orderRepository.saveAndFlush(entity))
            .isExactlyInstanceOf(JpaSystemException.class)
            .hasMessage("Identifier of entity 'com.resilience.orderapi.order.persistence.OrderJpaEntity' must be manually assigned before calling 'persist()'");
    }

    @Test
    void shouldBeDoNotSaveAnOrderWithoutCustomerId() {
        final Order order = Order.create("1234", BigDecimal.TEN);
        final OrderJpaEntity entity = OrderJpaEntity.from(order);
        entity.setCustomerId(null);

        assertThatThrownBy(() -> this.orderRepository.saveAndFlush(entity))
            .isExactlyInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("could not execute batch [NULL not allowed for column \"customer_id\"");
    }

    @Test
    void shouldBeDoNotSaveAnOrderWithoutAmount() {
        final Order order = Order.create("1234", BigDecimal.TEN);
        final OrderJpaEntity entity = OrderJpaEntity.from(order);
        entity.setAmount(null);

        assertThatThrownBy(() -> this.orderRepository.saveAndFlush(entity))
            .isExactlyInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("could not execute batch [NULL not allowed for column \"amount\"");
    }

    @Test
    void shouldBeDoNotSaveAnOrderWithoutStatus() {
        final Order order = Order.create("1234", BigDecimal.TEN);
        final OrderJpaEntity entity = OrderJpaEntity.from(order);
        entity.setStatus(null);

        assertThatThrownBy(() -> this.orderRepository.saveAndFlush(entity))
            .isExactlyInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("could not execute batch [NULL not allowed for column \"status\"");
    }

}
