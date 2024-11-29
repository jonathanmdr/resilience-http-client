package com.resilience.application.authorization.authorize;

import com.resilience.application.UseCase;
import com.resilience.domain.authorization.AuthorizationGateway;
import com.resilience.domain.order.OrderGateway;

public abstract sealed class AuthorizeOrderUseCase extends UseCase<AuthorizeOrderInput, AuthorizeOrderOutput> permits DefaultAuthorizeOrderUseCase {

    protected final OrderGateway orderGateway;
    protected final AuthorizationGateway authorizationGateway;

    protected AuthorizeOrderUseCase(final OrderGateway orderGateway, final AuthorizationGateway authorizationGateway) {
        this.orderGateway = orderGateway;
        this.authorizationGateway = authorizationGateway;
    }

}
