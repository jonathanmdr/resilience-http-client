package com.resilience.auditapi.graphql.order;

import com.resilience.auditapi.persistence.order.OrderDataDocument;
import com.resilience.auditapi.persistence.order.OrderDocument;
import com.resilience.auditapi.persistence.order.OrderRepository;
import com.resilience.auditapi.persistence.common.OriginDocument;
import com.resilience.auditapi.graphql.order.model.GqlCdcOrderPayload;
import com.resilience.auditapi.graphql.order.model.GqlCdcOrderPayload.CdcOperation;
import com.resilience.auditapi.graphql.order.model.GqlCdcOrderPayload.CdcSource;
import com.resilience.auditapi.graphql.order.model.GqlOrder;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @QueryMapping
    public List<GqlCdcOrderPayload> orders() {
        final List<OrderDocument> orders = this.orderRepository.findAll();
        return orders.stream()
                .map(document -> {
                    GqlOrder before = null;
                    GqlOrder after = null;

                    if (document.getBefore() != null) {
                        final OrderDataDocument beforeDocument = document.getBefore();
                        before = new GqlOrder(
                            beforeDocument.getId(),
                            beforeDocument.getCustomerId(),
                            beforeDocument.getAmount(),
                            beforeDocument.getStatus()
                        );
                    }

                    if (document.getAfter() != null) {
                        final OrderDataDocument afterDocument = document.getAfter();
                        after = new GqlOrder(
                            afterDocument.getId(),
                            afterDocument.getCustomerId(),
                            afterDocument.getAmount(),
                            afterDocument.getStatus()
                        );
                    }
                    final OriginDocument origin = document.getOrigin();
                    final CdcSource source = new CdcSource(origin.getDb(), origin.getTable(), origin.getFile(), CdcOperation.valueOf(origin.getOp()));
                    return new GqlCdcOrderPayload(document.getId(), before, after, source, String.valueOf(document.getCreatedAt()));
                })
                .toList();
    }

}
