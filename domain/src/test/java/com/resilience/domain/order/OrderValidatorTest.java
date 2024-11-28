package com.resilience.domain.order;

import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.handler.NotificationHandler;
import com.resilience.domain.validation.handler.ThrowableHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OrderValidatorTest {

    @Test
    void shouldBeInstantiateOrderValidator() {
        final Order order = Order.create("1234", BigDecimal.TEN);
        final ValidationHandler validationHandler = ThrowableHandler.create();
        final OrderValidator orderValidator = OrderValidator.create(order, validationHandler);
        assertThat(orderValidator).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidOrderToValidate")
    void shouldBeValidateOrder(final Order order, final Error error) {
        final ValidationHandler validationHandler = NotificationHandler.create();
        final OrderValidator orderValidator = OrderValidator.create(order, validationHandler);
        orderValidator.validate();
        assertThat(validationHandler.errors()).containsExactly(error);
    }

    private static Stream<Arguments> provideInvalidOrderToValidate() {
        return Stream.of(
            Arguments.of(Order.create(null, BigDecimal.TEN), new Error("Customer id must not be null or blank")),
            Arguments.of(Order.create(" ", BigDecimal.TEN), new Error("Customer id must not be null or blank")),
            Arguments.of(Order.create("1234", null), new Error("Amount must be greater than zero")),
            Arguments.of(Order.create("1234", BigDecimal.ZERO), new Error("Amount must be greater than zero")),
            Arguments.of(Order.create("1234", BigDecimal.valueOf(-1)), new Error("Amount must be greater than zero"))
        );
    }

}
