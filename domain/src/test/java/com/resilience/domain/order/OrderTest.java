package com.resilience.domain.order;

import com.resilience.domain.authorization.AuthorizationStatus;
import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.handler.NotificationHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class OrderTest {

    @Test
    void shouldBeCreateNewOrder() {
        final var order = Order.create("1234", BigDecimal.TEN);
        assertSoftly(softly -> {
            softly.assertThat(order.id()).isNotNull();
            softly.assertThat(order.customerId()).isEqualTo("1234");
            softly.assertThat(order.amount()).isEqualTo(BigDecimal.TEN);
            softly.assertThat(order.status()).isEqualTo(OrderStatus.CREATED);
            softly.assertThat(order.events()).isEmpty();
        });
    }

    @Test
    void shouldBeDoNothingWhenAuthorizeWithPendingAuthorizationStatus() {
        final var order = Order.create("1234", BigDecimal.TEN)
            .authorize(AuthorizationOrderStatusTranslatorService.create(AuthorizationStatus.PENDING));
        assertSoftly(softly -> {
            softly.assertThat(order.id()).isNotNull();
            softly.assertThat(order.customerId()).isEqualTo("1234");
            softly.assertThat(order.amount()).isEqualTo(BigDecimal.TEN);
            softly.assertThat(order.status()).isEqualTo(OrderStatus.CREATED);
            softly.assertThat(order.events()).isEmpty();
        });
    }

    @Test
    void shouldBeConfirmOrder() {
        final var order = Order.create("1234", BigDecimal.TEN)
            .authorize(AuthorizationOrderStatusTranslatorService.create(AuthorizationStatus.APPROVED));
        assertSoftly(softly -> {
            softly.assertThat(order.id()).isNotNull();
            softly.assertThat(order.customerId()).isEqualTo("1234");
            softly.assertThat(order.amount()).isEqualTo(BigDecimal.TEN);
            softly.assertThat(order.status()).isEqualTo(OrderStatus.CONFIRMED);
            softly.assertThat(order.events()).isEmpty();
        });
    }

    @Test
    void shouldBeRejectOrder() {
        final var order = Order.create("1234", BigDecimal.TEN)
            .authorize(AuthorizationOrderStatusTranslatorService.create(AuthorizationStatus.REFUSED));
        assertSoftly(softly -> {
            softly.assertThat(order.id()).isNotNull();
            softly.assertThat(order.customerId()).isEqualTo("1234");
            softly.assertThat(order.amount()).isEqualTo(BigDecimal.TEN);
            softly.assertThat(order.status()).isEqualTo(OrderStatus.REJECTED);
            softly.assertThat(order.events()).isEmpty();
        });
    }

    @ParameterizedTest
    @CsvSource(
        value = {
            "CREATED, false",
            "CONFIRMED, true",
            "REJECTED, true"
        }
    )
    void shouldBeValidateIsFinalizedOrder(final OrderStatus status, final boolean expected) {
        final Order order = Order.with(OrderId.unique(), "1234", BigDecimal.TEN, status);
        assertThat(order.isFinalized()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidOrderToValidate")
    void shouldBeValidateOrder(final Order order, final Error error) {
        final ValidationHandler validationHandler = NotificationHandler.create();
        order.validate(validationHandler);
        assertSoftly(softly -> {
            softly.assertThat(validationHandler.hasErrors()).isTrue();
            softly.assertThat(validationHandler.errors()).isEqualTo(List.of(error));
            softly.assertThat(order.events()).isEmpty();
        });
    }

    private static Stream<Arguments> provideInvalidOrderToValidate() {
        return Stream.of(
            Arguments.of(Order.create(null, BigDecimal.TEN), Error.of("Customer id must not be null or blank")),
            Arguments.of(Order.create(" ", BigDecimal.TEN), Error.of("Customer id must not be null or blank")),
            Arguments.of(Order.create("1234", null), Error.of("Amount must be greater than zero")),
            Arguments.of(Order.create("1234", BigDecimal.ZERO), Error.of("Amount must be greater than zero")),
            Arguments.of(Order.create("1234", BigDecimal.valueOf(-1)), Error.of("Amount must be greater than zero"))
        );
    }

}
