package com.resilience.orderworker.order.persistence;

import com.resilience.domain.order.Order;
import com.resilience.domain.order.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderJpaEntityTest {

    @Test
    void shouldBeCreateAnOrderJpaEntityFromOrder() {
        final Order order = Order.create("1234", BigDecimal.TEN);
        final OrderJpaEntity entity = OrderJpaEntity.from(order);

        assertThat(entity)
            .isNotNull()
            .satisfies(orderJpaEntity -> {
                assertThat(orderJpaEntity.getId()).isEqualTo(order.id().value());
                assertThat(orderJpaEntity.getCustomerId()).isEqualTo(order.customerId());
                assertThat(orderJpaEntity.getAmount()).isEqualTo(order.amount());
                assertThat(orderJpaEntity.getStatus()).isEqualTo(order.status());
            });
    }

    @Test
    void shouldBeTheSameEntity() {
        final Order order = Order.create("1234", BigDecimal.TEN);
        final OrderJpaEntity entityOne = OrderJpaEntity.from(order);
        final OrderJpaEntity entityTwo = OrderJpaEntity.from(order);

        assertThat(entityOne)
            .hasSameHashCodeAs(entityTwo)
            .isEqualTo(entityTwo);
    }

    @Test
    void shouldBeNotSameEntity() {
        final Order order = Order.create("1234", BigDecimal.TEN);
        final OrderJpaEntity entityOne = OrderJpaEntity.from(order);
        final OrderJpaEntity entityTwo = OrderJpaEntity.from(order);

        // by id
        entityOne.setId("12345");
        assertThat(entityOne)
            .isNotEqualTo(entityTwo)
            .doesNotHaveSameHashCodeAs(entityTwo);
        entityOne.setId(null);
        assertThat(entityOne)
            .isNotEqualTo(entityTwo)
            .doesNotHaveSameHashCodeAs(entityTwo);
        entityOne.setId(order.id().value());

        // by customerId
        entityOne.setCustomerId("12345");
        assertThat(entityOne)
            .isNotEqualTo(entityTwo)
            .doesNotHaveSameHashCodeAs(entityTwo);
        entityOne.setCustomerId(null);
        assertThat(entityOne)
            .isNotEqualTo(entityTwo)
            .doesNotHaveSameHashCodeAs(entityTwo);
        entityOne.setCustomerId(order.customerId());

        // by amount
        entityOne.setAmount(BigDecimal.ONE);
        assertThat(entityOne)
            .isNotEqualTo(entityTwo)
            .doesNotHaveSameHashCodeAs(entityTwo);
        entityOne.setAmount(null);
        assertThat(entityOne)
            .isNotEqualTo(entityTwo)
            .doesNotHaveSameHashCodeAs(entityTwo);
        entityOne.setAmount(order.amount());

        // by status
        entityOne.setStatus(OrderStatus.CONFIRMED);
        assertThat(entityOne)
            .isNotEqualTo(entityTwo)
            .doesNotHaveSameHashCodeAs(entityTwo);
        entityOne.setStatus(null);
        assertThat(entityOne)
            .isNotEqualTo(entityTwo)
            .doesNotHaveSameHashCodeAs(entityTwo);
        entityOne.setStatus(order.status());

        // by class
        assertThat(entityOne).isNotEqualTo(order); // NOSONAR
    }

}
