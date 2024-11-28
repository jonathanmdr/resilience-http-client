package com.resilience.domain.order;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderIdTest {

    @Test
    void shouldCreateOrderId() {
        final OrderId orderId = OrderId.unique();
        assertThat(orderId)
            .isNotNull()
            .satisfies(generatedOrderId -> assertThat(generatedOrderId)
                .extracting(OrderId::value)
                .isExactlyInstanceOf(String.class)
                .isNotNull()
            );
    }

    @Test
    void shouldCreateOrderIdFrom() {
        final OrderId orderId = OrderId.from("order-id");
        assertThat(orderId)
            .isNotNull()
            .satisfies(generatedOrderId -> assertThat(generatedOrderId)
                .extracting(OrderId::value)
                .isExactlyInstanceOf(String.class)
                .isEqualTo("order-id")
            );
    }

    @Test
    void shouldReturnOrderIdValue() {
        final OrderId orderId = OrderId.from("order-id");
        assertThat(orderId.value())
            .isExactlyInstanceOf(String.class)
            .isEqualTo("order-id");
    }

}
