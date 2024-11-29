package com.resilience.application.authorization.authorize;

import com.resilience.application.MockSupportTest;
import com.resilience.domain.authorization.Authorization;
import com.resilience.domain.authorization.AuthorizationGateway;
import com.resilience.domain.authorization.AuthorizationId;
import com.resilience.domain.common.Result;
import com.resilience.domain.order.Order;
import com.resilience.domain.order.OrderGateway;
import com.resilience.domain.validation.ValidationHandler;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthorizeOrderUseCaseTest extends MockSupportTest {

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private AuthorizationGateway authorizationGateway;

    @InjectMocks
    private DefaultAuthorizeOrderUseCase subject;

    @Override
    protected List<Object> mocks() {
        return List.of(this.orderGateway, this.authorizationGateway);
    }

    @Test
    void shouldBeReturnApprovedAuthorization() {
        final var customerId = UUID.randomUUID().toString();
        final var amount = BigDecimal.TEN;
        final var order = Order.create(customerId, amount);
        final var pendingAuthorization = Authorization.create(order.id().value(), order.customerId(), order.amount());
        final var approvedAuthorization = pendingAuthorization.approve();

        when(this.orderGateway.findById(order.id())).thenReturn(Optional.of(order));
        when(this.authorizationGateway.process(any(Authorization.class))).thenReturn(approvedAuthorization);

        final AuthorizeOrderInput input = AuthorizeOrderInput.with(order.id().value());
        final Result<AuthorizeOrderOutput, ValidationHandler> result = this.subject.execute(input);

        assertSoftly(softly -> {
            softly.assertThat(result).isNotNull();
            softly.assertThat(result.hasSuccess()).isTrue();
            softly.assertThat(result.success())
                .isNotNull()
                .satisfies(output -> {
                    softly.assertThat(output.authorizationId()).isNotBlank();
                    softly.assertThat(output.status()).isEqualTo(approvedAuthorization.status().name());
                });
        });
        verify(this.orderGateway).findById(order.id());
        verify(this.authorizationGateway).process(argThat(authorization -> {
            assertSoftly(softly -> {
                softly.assertThat(authorization).isNotNull();
                softly.assertThat(authorization.id())
                    .isNotNull()
                    .isExactlyInstanceOf(AuthorizationId.class);
                softly.assertThat(authorization.orderId()).isEqualTo(pendingAuthorization.orderId());
                softly.assertThat(authorization.customerId()).isEqualTo(pendingAuthorization.customerId());
                softly.assertThat(authorization.orderAmount()).isEqualTo(pendingAuthorization.orderAmount());
                softly.assertThat(authorization.status()).isEqualTo(pendingAuthorization.status());
            });
            return true;
        }));
    }

    @Test
    void shouldBeReturnRefusedAuthorization() {
        final var customerId = UUID.randomUUID().toString();
        final var amount = BigDecimal.TEN;
        final var order = Order.create(customerId, amount);
        final var pendingAuthorization = Authorization.create(order.id().value(), order.customerId(), order.amount());
        final var refusedAuthorization = pendingAuthorization.refuse();

        when(this.orderGateway.findById(order.id())).thenReturn(Optional.of(order));
        when(this.authorizationGateway.process(any(Authorization.class))).thenReturn(refusedAuthorization);

        final AuthorizeOrderInput input = AuthorizeOrderInput.with(order.id().value());
        final Result<AuthorizeOrderOutput, ValidationHandler> result = this.subject.execute(input);

        assertSoftly(softly -> {
            softly.assertThat(result).isNotNull();
            softly.assertThat(result.hasSuccess()).isTrue();
            softly.assertThat(result.success())
                .isNotNull()
                .satisfies(output -> {
                    softly.assertThat(output.authorizationId()).isNotBlank();
                    softly.assertThat(output.status()).isEqualTo(refusedAuthorization.status().name());
                });
        });
        verify(this.orderGateway).findById(order.id());
        verify(this.authorizationGateway).process(argThat(authorization -> {
            assertSoftly(softly -> {
                softly.assertThat(authorization).isNotNull();
                softly.assertThat(authorization.id())
                    .isNotNull()
                    .isExactlyInstanceOf(AuthorizationId.class);
                softly.assertThat(authorization.orderId()).isEqualTo(pendingAuthorization.orderId());
                softly.assertThat(authorization.customerId()).isEqualTo(pendingAuthorization.customerId());
                softly.assertThat(authorization.orderAmount()).isEqualTo(pendingAuthorization.orderAmount());
                softly.assertThat(authorization.status()).isEqualTo(pendingAuthorization.status());
            });
            return true;
        }));
    }

}
