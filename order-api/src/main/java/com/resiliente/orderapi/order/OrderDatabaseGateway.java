package com.resiliente.orderapi.order;

import com.resilience.domain.order.Order;
import com.resilience.domain.order.OrderGateway;
import com.resilience.domain.order.OrderId;
import com.resiliente.orderapi.order.persistence.OrderJpaEntity;
import com.resiliente.orderapi.order.persistence.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderDatabaseGateway implements OrderGateway {

    private final OrderRepository orderRepository;

    public OrderDatabaseGateway(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Optional<Order> findById(final OrderId orderId) {
        return this.orderRepository.findById(orderId.value())
            .map(OrderJpaEntity::toAggregate);
    }

    @Override
    public Order create(final Order order) {
        return this.orderRepository.save(OrderJpaEntity.from(order))
            .toAggregate();
    }

    @Override
    public Order update(final Order order) {
        return this.orderRepository.save(OrderJpaEntity.from(order))
            .toAggregate();
    }

}
