package com.resilience.domain.authorization;

import com.resilience.domain.order.OrderStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationProcessedStatusTranslatorServiceTest {

    @ParameterizedTest
    @CsvSource(
        value = {
            "CONFIRMED, APPROVED",
            "REJECTED, REFUSED",
            "CREATED, PENDING",
            "null, PENDING"
        },
        nullValues = {
            "null"
        }
    )
    void shouldTranslateProcessedAuthorizationOrderStatusToAuthorizationStatus(final OrderStatus orderStatus, final AuthorizationStatus expected) {
        final AuthorizationProcessedStatusTranslator subject = AuthorizationProcessedStatusTranslatorService.create(orderStatus);
        final AuthorizationStatus status = subject.get();
        assertThat(status).isEqualTo(expected);
    }

}
