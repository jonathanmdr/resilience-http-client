package com.resilience.auditapi.graphql.order;

import com.resilience.auditapi.graphql.common.GqlCdcPayload;
import com.resilience.auditapi.graphql.common.GqlCdcPayload.CdcOperation;
import com.resilience.auditapi.graphql.common.GqlCdcPayload.CdcSource;
import com.resilience.auditapi.graphql.order.model.GqlOrder;
import com.resilience.auditapi.persistence.common.OriginDocument;
import com.resilience.auditapi.persistence.order.OrderDataDocument;
import com.resilience.auditapi.persistence.order.OrderDocument;
import com.resilience.auditapi.persistence.order.OrderRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.function.Function;

@Controller
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @QueryMapping
    public List<GqlCdcPayload<GqlOrder>> orders() {
        final List<OrderDocument> orders = this.orderRepository.findAll();
        return orders.stream()
            .map(toGqlCdcPayload())
            .toList();
    }

    private static Function<OrderDocument, GqlCdcPayload<GqlOrder>> toGqlCdcPayload() {
        return document -> {
            final GqlOrder before = mapGqlOrder().apply(document.getBefore());
            final GqlOrder after = mapGqlOrder().apply(document.getAfter());
            final CdcSource source = mapGqlSource().apply(document.getOrigin());
            return new GqlCdcPayload<>(document.getId(), before, after, source, String.valueOf(document.getCreatedAt()));
        };
    }

    private static Function<OrderDataDocument, GqlOrder> mapGqlOrder() {
        return document -> {
            if (document == null) {
                return null;
            }
            return new GqlOrder(
                document.id(),
                document.customerId(),
                document.amount(),
                document.status()
            );
        };
    }

    private static Function<OriginDocument, CdcSource> mapGqlSource() {
        return origin -> new CdcSource(origin.db(), origin.table(), origin.file(), CdcOperation.valueOf(origin.op()));
    }

}
