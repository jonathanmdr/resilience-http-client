package com.resilience.application.order.update;

import com.resilience.application.MockSupportTest;
import com.resilience.domain.authorization.AuthorizationStatus;
import com.resilience.domain.order.AuthorizationOrderStatusTranslatorService;
import com.resilience.domain.order.Order;
import com.resilience.domain.order.OrderGateway;
import com.resilience.domain.order.OrderId;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateOrderUseCaseTest extends MockSupportTest {

    @Mock
    private OrderGateway orderGateway;

    @InjectMocks
    private DefaultUpdateOrderUseCase subject;

    @Override
    protected List<Object> mocks() {
        return List.of(this.orderGateway);
    }

    @Test
    void shouldBeReturnApprovedAuthorization() {
        final Order order = Order.create("1234", BigDecimal.TEN);
        final OrderId orderId = order.id();
        final UpdateOrderInput input = UpdateOrderInput.with(orderId.value(), AuthorizationStatus.APPROVED);
        final Order approvedOrder = order.authorize(AuthorizationOrderStatusTranslatorService.create(input.status()));

        when(this.orderGateway.findById(any(OrderId.class))).thenReturn(Optional.of(order));
        when(this.orderGateway.update(any(Order.class))).thenReturn(approvedOrder);

        this.subject.execute(input);

        verify(this.orderGateway).findById(argThat(orderId::equals));
        verify(this.orderGateway).update(argThat(orderToUpdate -> {
            assertThat(orderToUpdate)
                .usingRecursiveComparison()
                .isEqualTo(approvedOrder);
            return true;
        }));
    }

    @Test
    void shouldBeReturnRefusedAuthorization() {
        final Order order = Order.create("1234", BigDecimal.TEN);
        final OrderId orderId = order.id();
        final UpdateOrderInput input = UpdateOrderInput.with(orderId.value(), AuthorizationStatus.REFUSED);
        final Order refusedOrder = order.authorize(AuthorizationOrderStatusTranslatorService.create(input.status()));

        when(this.orderGateway.findById(any(OrderId.class))).thenReturn(Optional.of(order));
        when(this.orderGateway.update(any(Order.class))).thenReturn(refusedOrder);

        this.subject.execute(input);

        verify(this.orderGateway).findById(argThat(orderId::equals));
        verify(this.orderGateway).update(argThat(orderToUpdate -> {
            assertThat(orderToUpdate)
                .usingRecursiveComparison()
                .isEqualTo(refusedOrder);
            return true;
        }));
    }

    @Test
    void shouldBeDoNothingWhenAuthorizationStatusIsPending() {
        final Order order = Order.create("1234", BigDecimal.TEN);
        final OrderId orderId = order.id();
        final UpdateOrderInput input = UpdateOrderInput.with(orderId.value(), AuthorizationStatus.PENDING);
        final Order pendingOrder = order.authorize(AuthorizationOrderStatusTranslatorService.create(input.status()));

        when(this.orderGateway.findById(any(OrderId.class))).thenReturn(Optional.of(order));
        when(this.orderGateway.update(any(Order.class))).thenReturn(pendingOrder);

        this.subject.execute(input);

        verify(this.orderGateway).findById(argThat(orderId::equals));
        verify(this.orderGateway).update(argThat(orderToUpdate -> {
            assertThat(orderToUpdate)
                .usingRecursiveComparison()
                .isEqualTo(pendingOrder);
            return true;
        }));
    }

    @Test
    void shouldBeDoNothingWhenOrderDoesNotExists() {
        final Order order = Order.create("1234", BigDecimal.TEN);
        final OrderId orderId = order.id();
        final UpdateOrderInput input = UpdateOrderInput.with(orderId.value(), AuthorizationStatus.APPROVED);

        when(this.orderGateway.findById(any(OrderId.class))).thenReturn(Optional.empty());

        this.subject.execute(input);

        verify(this.orderGateway).findById(argThat(orderId::equals));
        verify(this.orderGateway, never()).update(any(Order.class));
    }

}
