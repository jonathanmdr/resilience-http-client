package com.resilience.domain.order;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    void whenCreatedHasConfirmedThenReturnConfirmed() {
        final OrderStatus status = OrderStatus.CREATED;
        final OrderStatus actual = status.confirm();
        assertThat(actual).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void whenCreatedHasRejectedThenReturnRejected() {
        final OrderStatus status = OrderStatus.CREATED;
        final OrderStatus actual = status.reject();
        assertThat(actual).isEqualTo(OrderStatus.REJECTED);
    }

    @Test
    void whenConfirmedHasConfirmedThenReturnConfirmed() {
        final OrderStatus status = OrderStatus.CONFIRMED;
        final OrderStatus actual = status.confirm();
        assertThat(actual).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void whenConfirmedHasRejectedThenReturnConfirmed() {
        final OrderStatus status = OrderStatus.CONFIRMED;
        final OrderStatus actual = status.reject();
        assertThat(actual).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void whenRejectedHasConfirmedThenReturnRejected() {
        final OrderStatus status = OrderStatus.REJECTED;
        final OrderStatus actual = status.confirm();
        assertThat(actual).isEqualTo(OrderStatus.REJECTED);
    }

    @Test
    void whenRejectedHasRejectedThenReturnRejected() {
        final OrderStatus status = OrderStatus.REJECTED;
        final OrderStatus actual = status.reject();
        assertThat(actual).isEqualTo(OrderStatus.REJECTED);
    }

}
