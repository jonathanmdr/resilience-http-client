package com.resilience.domain.order;

import java.util.function.Supplier;

@FunctionalInterface
public interface AuthorizationOrderStatusTranslator extends Supplier<OrderStatus> {

}
