package com.resilience.domain.authorization;

public interface AuthorizationGateway {

    Authorization authorize(final Authorization authorization);

}
