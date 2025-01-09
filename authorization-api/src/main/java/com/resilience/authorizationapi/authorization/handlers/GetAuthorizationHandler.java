package com.resilience.authorizationapi.authorization.handlers;

import com.resilience.authorizationapi.authorization.models.GetAuthorizationResponse;
import com.resilience.authorizationapi.authorization.persistence.AuthorizationJpaEntity;
import com.resilience.authorizationapi.authorization.persistence.AuthorizationRepository;
import com.resilience.domain.exception.NoStacktraceException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Optional;

@Component
public class GetAuthorizationHandler implements HandlerFunction<ServerResponse> {

    private final AuthorizationRepository authorizationRepository;

    public GetAuthorizationHandler(final AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
    }

    @SuppressWarnings("all")
    @Override
    public ServerResponse handle(final ServerRequest serverRequest) {
        final String authorizationId = serverRequest.pathVariable("authorization_id");

        if (authorizationId.isBlank()) {
            throw new NoStacktraceException("Authorization ID is required");
        }

        final Optional<AuthorizationJpaEntity> entityExists = this.authorizationRepository.findById(authorizationId);

        return entityExists.map(entity -> ServerResponse.ok().body(GetAuthorizationResponse.from(entity)))
                .orElseGet(() -> ServerResponse.notFound().build());
    }

}
