package com.resilience.domain.order;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @ParameterizedTest
    @CsvSource(
        value = {
            "CREATED, CONFIRMED",
            "CONFIRMED, CONFIRMED",
            "REJECTED, REJECTED"
        }
    )
    void validateTransitionsWhenConfirm(final OrderStatus status, final OrderStatus expected) {
        final OrderStatus actual = status.confirm();
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(
        value = {
            "CREATED, REJECTED",
            "CONFIRMED, CONFIRMED",
            "REJECTED, REJECTED"
        }
    )
    void validateTransitionsWhenReject(final OrderStatus status, final OrderStatus expected) {
        final OrderStatus actual = status.reject();
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(
        value = {
            "CREATED, true",
            "CONFIRMED, false",
            "REJECTED, false"
        }
    )
    void validateHasMoreTransitions(final OrderStatus status, final boolean expected) {
        assertThat(status.hasMoreTransitions()).isEqualTo(expected);
    }

}
