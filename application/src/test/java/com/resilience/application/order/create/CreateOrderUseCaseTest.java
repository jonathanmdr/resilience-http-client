package com.resilience.application.order.create;

import com.resilience.application.MockSupportTest;
import com.resilience.domain.common.Result;
import com.resilience.domain.order.Order;
import com.resilience.domain.order.OrderGateway;
import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateOrderUseCaseTest extends MockSupportTest {

    @Mock
    private OrderGateway orderGateway;

    @InjectMocks
    private DefaultCreateOrderUseCase subject;

    @Override
    protected List<Object> mocks() {
        return List.of(this.orderGateway);
    }

    @Test
    void shouldBeCreateNewOrder() {
        final var customerId = UUID.randomUUID().toString();
        final var amount = BigDecimal.TEN;
        final var createOrderInput = CreateOrderInput.with(customerId, amount);
        final var order = Order.create(customerId, amount);

        when(this.orderGateway.create(any(Order.class))).thenReturn(order);

        final Result<CreateOrderOutput, ValidationHandler> result = this.subject.execute(createOrderInput);

        assertSoftly(softly -> {
            softly.assertThat(result).isNotNull();
            softly.assertThat(result.hasSuccess()).isTrue();
            softly.assertThat(result.success())
                .isNotNull()
                .isEqualTo(CreateOrderOutput.with(order.id().value()));
        });
        verify(this.orderGateway).create(argThat(orderArgument -> {
            assertThat(orderArgument)
                .isNotNull()
                .satisfies(orderArgumentValue -> {
                    assertThat(orderArgumentValue.id()).isNotNull();
                    assertThat(orderArgumentValue.customerId()).isEqualTo(customerId);
                    assertThat(orderArgumentValue.amount()).isEqualTo(amount);
                });
            return true;
        }));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldBeReturnErrorWhenCustomerIdIsInvalid(final String customerId) {
        final var amount = BigDecimal.TEN;
        final var createOrderInput = CreateOrderInput.with(customerId, amount);
        final var error = new Error("Customer id must not be null or blank");

        final Result<CreateOrderOutput, ValidationHandler> createOrderResult = this.subject.execute(createOrderInput);

        assertSoftly(softly -> {
            softly.assertThat(createOrderResult).isNotNull();
            softly.assertThat(createOrderResult.hasError()).isTrue();
            softly.assertThat(createOrderResult.error())
                .isNotNull()
                .satisfies(handler -> {
                    softly.assertThat(handler).isNotNull();
                    softly.assertThat(handler.hasErrors()).isTrue();
                    softly.assertThat(handler.errors()).containsExactly(error);
                });
        });
        verify(this.orderGateway, never()).create(any(Order.class));
    }

    @ParameterizedTest
    @CsvSource(
        value = {
            "null",
            "0",
            "-1",
        },
        nullValues = {
            "null"
        }
    )
    void shouldBeReturnErrorWhenAmountIsInvalid(final BigDecimal amount) {
        final var customerId = UUID.randomUUID().toString();
        final var createOrderInput = CreateOrderInput.with(customerId, amount);
        final var error = new Error("Amount must be greater than zero");

        final Result<CreateOrderOutput, ValidationHandler> createOrderResult = this.subject.execute(createOrderInput);

        assertSoftly(softly -> {
            softly.assertThat(createOrderResult).isNotNull();
            softly.assertThat(createOrderResult.hasError()).isTrue();
            softly.assertThat(createOrderResult.error())
                .isNotNull()
                .satisfies(handler -> {
                    softly.assertThat(handler).isNotNull();
                    softly.assertThat(handler.hasErrors()).isTrue();
                    softly.assertThat(handler.errors()).containsExactly(error);
                });
        });
        verify(this.orderGateway, never()).create(any(Order.class));
    }

}
