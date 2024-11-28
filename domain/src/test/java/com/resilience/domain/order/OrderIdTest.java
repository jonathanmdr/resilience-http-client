package com.resilience.domain.order;

import com.resilience.domain.StubId;
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

    @Test
    void shouldBeTheSameOrderId() {
        final OrderId orderId = OrderId.from("order-id");
        final OrderId orderId2 = OrderId.from("order-id");
        assertThat(orderId)
            .isEqualTo(orderId2)
            .hasSameHashCodeAs(orderId2);
    }

    @Test
    void shouldNotBeTheSameOrderId() {
        final OrderId orderId = OrderId.from("order-id");
        final OrderId orderId2 = OrderId.from("order-id-2");
        assertThat(orderId)
            .isNotEqualTo(orderId2)
            .doesNotHaveSameHashCodeAs(orderId2);
    }

    @Test
    void shouldNotBeTheSameOrderIdByInstances() {
        final OrderId orderId = OrderId.from("order-id");
        final StubId stubId = StubId.from("stub-id");
        assertThat(orderId).isNotEqualTo(stubId);
    }

}
