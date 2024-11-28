package com.resilience.domain.authorization;

import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.handler.NotificationHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class AuthorizationTest {

    @Test
    void shouldBeCreateNewAuthorization() {
        final var authorization = Authorization.create("1234", "4321", BigDecimal.TEN);
        assertSoftly(softly -> {
            softly.assertThat(authorization.id()).isNotNull();
            softly.assertThat(authorization.orderId()).isEqualTo("1234");
            softly.assertThat(authorization.customerId()).isEqualTo("4321");
            softly.assertThat(authorization.orderAmount()).isEqualTo(BigDecimal.TEN);
            softly.assertThat(authorization.status()).isEqualTo(AuthorizationStatus.PENDING);
        });
    }

    @Test
    void shouldBeApproveAuthorization() {
        final var authorization = Authorization.create("1234", "4321", BigDecimal.TEN).approve();
        assertSoftly(softly -> {
            softly.assertThat(authorization.id()).isNotNull();
            softly.assertThat(authorization.orderId()).isEqualTo("1234");
            softly.assertThat(authorization.customerId()).isEqualTo("4321");
            softly.assertThat(authorization.orderAmount()).isEqualTo(BigDecimal.TEN);
            softly.assertThat(authorization.status()).isEqualTo(AuthorizationStatus.APPROVED);
        });
    }

    @Test
    void shouldBeRefuseAuthorization() {
        final var authorization = Authorization.create("1234", "4321", BigDecimal.TEN).refuse();
        assertSoftly(softly -> {
            softly.assertThat(authorization.id()).isNotNull();
            softly.assertThat(authorization.orderId()).isEqualTo("1234");
            softly.assertThat(authorization.customerId()).isEqualTo("4321");
            softly.assertThat(authorization.orderAmount()).isEqualTo(BigDecimal.TEN);
            softly.assertThat(authorization.status()).isEqualTo(AuthorizationStatus.REFUSED);
        });
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAuthorizationToValidate")
    void shouldBeValidateAuthorization(final Authorization authorization, final Error error) {
        final ValidationHandler validationHandler = NotificationHandler.create();
        authorization.validate(validationHandler);
        assertSoftly(softly -> {
            softly.assertThat(validationHandler.hasErrors()).isTrue();
            softly.assertThat(validationHandler.errors()).isEqualTo(List.of(error));
        });
    }

    private static Stream<Arguments> provideInvalidAuthorizationToValidate() {
        return Stream.of(
            Arguments.of(Authorization.create(null, "4321", BigDecimal.TEN), new Error("Order id must not be null or blank")),
            Arguments.of(Authorization.create(" ", "4321", BigDecimal.TEN), new Error("Order id must not be null or blank")),
            Arguments.of(Authorization.create("1234", null, BigDecimal.TEN), new Error("Customer id must not be null or blank")),
            Arguments.of(Authorization.create("1234", " ", BigDecimal.TEN), new Error("Customer id must not be null or blank")),
            Arguments.of(Authorization.create("1234", "4321", null), new Error("Order amount must be greater than zero")),
            Arguments.of(Authorization.create("1234", "4321", BigDecimal.ZERO), new Error("Order amount must be greater than zero")),
            Arguments.of(Authorization.create("1234", "4321", BigDecimal.valueOf(-1)), new Error("Order amount must be greater than zero"))
        );
    }

}
