package com.resilience.application.order.get;

import com.resilience.application.MockSupportTest;
import com.resilience.domain.common.Result;
import com.resilience.domain.order.Order;
import com.resilience.domain.order.OrderGateway;
import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetOrderByIdUseCaseTest extends MockSupportTest {

    @Mock
    private OrderGateway orderGateway;

    @InjectMocks
    private DefaultGetOrderByIdUseCase subject;

    @Override
    protected List<Object> mocks() {
        return List.of(this.orderGateway);
    }

    @Test
    void shouldBeReturnOrderIfOrderExists() {
        final var customerId = UUID.randomUUID().toString();
        final var amount = BigDecimal.TEN;
        final var order = Order.create(customerId, amount);

        when(this.orderGateway.findById(order.id())).thenReturn(Optional.of(order));

        final GetOrderByIdInput input = GetOrderByIdInput.with(order.id().value());
        final Result<GetOrderByIdOutput, ValidationHandler> result = this.subject.execute(input);

        assertSoftly(softly -> {
            softly.assertThat(result).isNotNull();
            softly.assertThat(result.hasSuccess()).isTrue();
            softly.assertThat(result.success())
                .isNotNull()
                .isEqualTo(GetOrderByIdOutput.with(order.id().value(), order.customerId(), order.amount(), order.status().name()));
        });
        verify(this.orderGateway).findById(order.id());
    }

    @Test
    void shouldBeReturnErrorIfOrderNotExists() {
        final var customerId = UUID.randomUUID().toString();
        final var amount = BigDecimal.TEN;
        final var order = Order.create(customerId, amount);
        final var error = new Error("Order '%s' not found".formatted(order.id().value()));

        when(this.orderGateway.findById(order.id())).thenReturn(Optional.empty());

        final GetOrderByIdInput input = GetOrderByIdInput.with(order.id().value());
        final Result<GetOrderByIdOutput, ValidationHandler> result = this.subject.execute(input);

        assertSoftly(softly -> {
            softly.assertThat(result).isNotNull();
            softly.assertThat(result.hasError()).isTrue();
            softly.assertThat(result.error())
                .isNotNull()
                .satisfies(handler -> {
                    softly.assertThat(handler).isNotNull();
                    softly.assertThat(handler.hasErrors()).isTrue();
                    softly.assertThat(handler.errors()).containsExactly(error);
                });
        });
        verify(this.orderGateway).findById(order.id());
    }

}
