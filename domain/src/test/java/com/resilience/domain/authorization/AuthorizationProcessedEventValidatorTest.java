package com.resilience.domain.authorization;

import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.handler.NotificationHandler;
import com.resilience.domain.validation.handler.ThrowableHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class AuthorizationProcessedEventValidatorTest {

    @Test
    void shouldBeInstantiateAuthorizationProcessedEventValidator() {
        final AuthorizationProcessedEvent event = AuthorizationProcessedEvent.with(
            "authorizationId",
            "orderId",
            BigDecimal.TEN,
            AuthorizationStatus.APPROVED,
            Instant.now()
        );
        final ValidationHandler handler = ThrowableHandler.create();
        final AuthorizationProcessedEventValidator validator = AuthorizationProcessedEventValidator.create(event, handler);
        assertThat(validator).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAuthorizationProcessedEventToValidate")
    void shouldBeValidateAuthorizationProcessedEvent(final AuthorizationProcessedEvent event, final Error error) {
        final ValidationHandler handler = NotificationHandler.create();
        event.validate(handler);
        assertSoftly(softly -> {
            softly.assertThat(handler.hasErrors()).isTrue();
            softly.assertThat(handler.errors()).isEqualTo(List.of(error));
        });
    }

    private static Stream<Arguments> provideInvalidAuthorizationProcessedEventToValidate() {
        return Stream.of(
            Arguments.of(AuthorizationProcessedEvent.with(null, "4321", BigDecimal.TEN, AuthorizationStatus.APPROVED, Instant.now()), new Error("Authorization id must not be null or blank")),
            Arguments.of(AuthorizationProcessedEvent.with(" ", "4321", BigDecimal.TEN, AuthorizationStatus.APPROVED, Instant.now()), new Error("Authorization id must not be null or blank")),
            Arguments.of(AuthorizationProcessedEvent.with("1234", null, BigDecimal.TEN, AuthorizationStatus.APPROVED, Instant.now()), new Error("Order id must not be null or blank")),
            Arguments.of(AuthorizationProcessedEvent.with("1234", " ", BigDecimal.TEN, AuthorizationStatus.APPROVED, Instant.now()), new Error("Order id must not be null or blank")),
            Arguments.of(AuthorizationProcessedEvent.with("1234", "4321", null, AuthorizationStatus.APPROVED, Instant.now()), new Error("Order amount must be greater than zero")),
            Arguments.of(AuthorizationProcessedEvent.with("1234", "4321", BigDecimal.ZERO, AuthorizationStatus.APPROVED, Instant.now()), new Error("Order amount must be greater than zero")),
            Arguments.of(AuthorizationProcessedEvent.with("1234", "4321", BigDecimal.valueOf(-1), AuthorizationStatus.APPROVED, Instant.now()), new Error("Order amount must be greater than zero")),
            Arguments.of(AuthorizationProcessedEvent.with("1234", "4321", BigDecimal.TEN, null, Instant.now()), new Error("Authorization status must not be null")),
            Arguments.of(AuthorizationProcessedEvent.with("1234", "4321", BigDecimal.TEN, AuthorizationStatus.APPROVED, null), new Error("Occurred on must not be null or in the future")),
            Arguments.of(AuthorizationProcessedEvent.with("1234", "4321", BigDecimal.TEN, AuthorizationStatus.APPROVED, Instant.now().plusSeconds(60)), new Error("Occurred on must not be null or in the future"))
        );
    }

}
