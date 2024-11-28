package com.resilience.domain.order;

import java.util.Optional;

public interface OrderGateway {

    Optional<Order> findById(final OrderId orderId);
    Order create(final Order order);

}
