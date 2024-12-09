package com.resilience.authorizationapi.authorization.handlers;

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
    public ServerResponse handle(final ServerRequest serverRequest) throws Exception {
        final String authorizationId = serverRequest.pathVariable("authorization_id");

        if (authorizationId == null || authorizationId.isEmpty()) {
            throw new NoStacktraceException("Authorization ID is required");
        }

        final Optional<AuthorizationJpaEntity> entityExists = this.authorizationRepository.findById(authorizationId);

        if (entityExists.isPresent()) {
            return ServerResponse.ok()
                .body(entityExists.get().toResponse());
        }

        return ServerResponse.notFound().build();
    }

}
