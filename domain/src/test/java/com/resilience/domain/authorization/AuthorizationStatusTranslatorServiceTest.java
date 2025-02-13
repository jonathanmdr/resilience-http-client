package com.resilience.domain.authorization;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationStatusTranslatorServiceTest {

    @ParameterizedTest
    @CsvSource(
        value = {
            "PENDING, PENDING",
            "ANOTHER, PENDING",
            "null, PENDING",
            "empty, PENDING",
            "APPROVED, APPROVED",
            "REFUSED, REFUSED"
        },
        nullValues = {
            "null"
        },
        emptyValue = "empty"
    )
    void shouldTranslateAuthorizationStatusToOrderStatus(final String authorizationStatus, final AuthorizationStatus expected) {
        final AuthorizationStatusTranslatorService subject = AuthorizationStatusTranslatorService.create(authorizationStatus);
        final AuthorizationStatus status = subject.get();
        assertThat(status).isEqualTo(expected);
    }

}
