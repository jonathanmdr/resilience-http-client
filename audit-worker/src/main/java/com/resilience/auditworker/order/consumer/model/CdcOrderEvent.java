package com.resilience.auditworker.order.consumer.model;

import com.resilience.auditworker.common.CdcPayloadEvent;

public record CdcOrderEvent(
    CdcPayloadEvent<OrderEvent> payload
) {

    public static CdcOrderEvent with(final CdcPayloadEvent<OrderEvent> payload) {
        return new CdcOrderEvent(payload);
    }

}
