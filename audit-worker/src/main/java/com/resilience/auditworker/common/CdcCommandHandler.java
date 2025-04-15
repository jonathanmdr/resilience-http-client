package com.resilience.auditworker.common;

import com.resilience.auditworker.common.CdcPayloadEvent.CdcOperation;

public interface CdcCommandHandler<T> {

    void handle(final T event);
    CdcOperation op();

}
