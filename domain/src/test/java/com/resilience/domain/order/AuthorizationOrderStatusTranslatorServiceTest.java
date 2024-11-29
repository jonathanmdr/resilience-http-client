package com.resilience.domain.order;

import com.resilience.domain.authorization.AuthorizationStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationOrderStatusTranslatorServiceTest {

    @ParameterizedTest
    @CsvSource(
        value = {
            "PENDING, CREATED",
            "APPROVED, CONFIRMED",
            "REFUSED, REJECTED"
        }
    )
    void shouldTranslateAuthorizationStatusToOrderStatus(final AuthorizationStatus authorizationStatus, final OrderStatus expectedOrderStatus) {
        final AuthorizationOrderStatusTranslatorService subject = AuthorizationOrderStatusTranslatorService.create(authorizationStatus);
        final OrderStatus orderStatus = subject.get();
        assertThat(orderStatus).isEqualTo(expectedOrderStatus);
    }

}
