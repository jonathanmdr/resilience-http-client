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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationValidatorTest {

    @Test
    void shouldBeInstantiateAuthorizationValidator() {
        final Authorization authorization = Authorization.create("1234", "4321", BigDecimal.TEN);
        final ValidationHandler validationHandler = ThrowableHandler.create();
        final AuthorizationValidator authorizationValidator = AuthorizationValidator.create(authorization, validationHandler);
        assertThat(authorizationValidator).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAuthorizationToValidate")
    void shouldBeValidateAuthorization(final Authorization authorization, final Error error) {
        final ValidationHandler validationHandler = NotificationHandler.create();
        final AuthorizationValidator authorizationValidator = AuthorizationValidator.create(authorization, validationHandler);
        authorizationValidator.validate();
        assertThat(validationHandler.errors()).containsExactly(error);
    }

    private static Stream<Arguments> provideInvalidAuthorizationToValidate() {
        return Stream.of(
            Arguments.of(Authorization.with(AuthorizationId.unique("1234"), null, "4321", BigDecimal.TEN, AuthorizationStatus.PENDING), Error.of("Order id must not be null or blank")),
            Arguments.of(Authorization.with(AuthorizationId.unique("1234"), " ", "4321", BigDecimal.TEN, AuthorizationStatus.PENDING), Error.of("Order id must not be null or blank")),
            Arguments.of(Authorization.with(AuthorizationId.unique("1234"), "1234", "4321", BigDecimal.TEN, null), Error.of("Authorization status cannot be null")),
            Arguments.of(Authorization.create("1234", null, BigDecimal.TEN), Error.of("Customer id must not be null or blank")),
            Arguments.of(Authorization.create("1234", " ", BigDecimal.TEN), Error.of("Customer id must not be null or blank")),
            Arguments.of(Authorization.create("1234", "4321", null), Error.of("Order amount must be greater than zero")),
            Arguments.of(Authorization.create("1234", "4321", BigDecimal.ZERO), Error.of("Order amount must be greater than zero")),
            Arguments.of(Authorization.create("1234", "4321", BigDecimal.valueOf(-1)), Error.of("Order amount must be greater than zero"))
        );
    }

}
