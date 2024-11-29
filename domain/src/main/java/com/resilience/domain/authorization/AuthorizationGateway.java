package com.resilience.domain.authorization;

public interface AuthorizationGateway {

    Authorization process(final Authorization authorization);

}
