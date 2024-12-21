package com.resilience.orderapi.order.presenter;

import com.resilience.application.order.create.CreateOrderOutput;
import com.resilience.application.order.get.GetOrderByIdOutput;
import com.resilience.orderapi.order.models.CreateOrderResponse;
import com.resilience.orderapi.order.models.GetOrderByIdResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class OrderPresenterTest {

    @Test
    void givenAnOrderOutputWhenFromIsCalledThenReturnCreateOrderResponse() {
        final CreateOrderOutput output = new CreateOrderOutput("1234");
        final CreateOrderResponse response = OrderPresenter.from(output);

        assertThat(response.orderId()).isEqualTo(output.orderId());
    }

    @Test
    void givenAGetOrderByIdOutputWhenFromIsCalledThenReturnGetOrderByIdResponse() {
        final GetOrderByIdOutput output = new GetOrderByIdOutput("1234", "4321", BigDecimal.TEN, "APPROVED");
        final GetOrderByIdResponse response = OrderPresenter.from(output);

        assertSoftly(softly -> {
            softly.assertThat(response.orderId()).isEqualTo(output.orderId());
            softly.assertThat(response.customerId()).isEqualTo(output.customerId());
            softly.assertThat(response.amount()).isEqualTo(output.amount());
            softly.assertThat(response.status()).isEqualTo(output.status());
        });
    }

}
