package com.resilience.authorizationapi.authorization.models;

import com.resilience.authorizationapi.authorization.persistence.AuthorizationJpaEntity;

public record AuthorizationResponse(String status) {

    public static AuthorizationResponse from(final AuthorizationJpaEntity entity) {
        return new AuthorizationResponse(entity.getStatus().name());
    }

}
