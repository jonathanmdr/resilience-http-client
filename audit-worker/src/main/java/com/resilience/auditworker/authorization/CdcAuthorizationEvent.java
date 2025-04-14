package com.resilience.auditworker.authorization;

import com.resilience.auditworker.common.CdcPayloadEvent;

public record CdcAuthorizationEvent(
    CdcPayloadEvent<AuthorizationEvent> payload
) {

    public static CdcAuthorizationEvent with(final CdcPayloadEvent<AuthorizationEvent> payload) {
        return new CdcAuthorizationEvent(payload);
    }

}
