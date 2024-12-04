package com.resiliente.orderapi.autorization.models;

import com.resilience.domain.authorization.AuthorizationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record AuthorizeOrderResponse(
    @Schema(example = "ef44b755-9f6b-4f53-a5d7-11955b100488") String authorizationId,
    @Schema(example = "APPROVED", implementation = AuthorizationStatus.class) String status
) {

    public static AuthorizeOrderResponse with(final String authorizationId, final String status) {
        return new AuthorizeOrderResponse(authorizationId, status);
    }

}
