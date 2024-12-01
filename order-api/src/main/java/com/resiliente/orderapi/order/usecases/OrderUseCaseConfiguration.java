package com.resiliente.orderapi.order.usecases;

import com.resilience.application.order.create.CreateOrderUseCase;
import com.resilience.application.order.create.DefaultCreateOrderUseCase;
import com.resilience.application.order.get.DefaultGetOrderByIdUseCase;
import com.resilience.application.order.get.GetOrderByIdUseCase;
import com.resilience.application.order.update.DefaultUpdateOrderUseCase;
import com.resilience.application.order.update.UpdateOrderUseCase;
import com.resilience.domain.order.OrderGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderUseCaseConfiguration {

    private final OrderGateway orderGateway;

    public OrderUseCaseConfiguration(final OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    @Bean
    public GetOrderByIdUseCase getOrderByIdUseCase() {
        return new DefaultGetOrderByIdUseCase(this.orderGateway);
    }

    @Bean
    public CreateOrderUseCase createOrderUseCase() {
        return new DefaultCreateOrderUseCase(this.orderGateway);
    }

    @Bean
    public UpdateOrderUseCase updateOrderUseCase() {
        return new DefaultUpdateOrderUseCase(this.orderGateway);
    }

}
