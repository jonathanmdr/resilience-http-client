package com.resiliente.orderapi.autorization.usecases;

import com.resilience.application.authorization.authorize.AuthorizeOrderUseCase;
import com.resilience.application.authorization.authorize.DefaultAuthorizeOrderUseCase;
import com.resilience.domain.authorization.AuthorizationGateway;
import com.resilience.domain.order.OrderGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorizationUseCaseConfiguration {

    private final OrderGateway orderGateway;
    private final AuthorizationGateway authorizationGateway;

    public AuthorizationUseCaseConfiguration(final OrderGateway orderGateway, final AuthorizationGateway authorizationGateway) {
        this.orderGateway = orderGateway;
        this.authorizationGateway = authorizationGateway;
    }

    @Bean
    public AuthorizeOrderUseCase authorizeOrderUseCase() {
        return new DefaultAuthorizeOrderUseCase(this.orderGateway, this.authorizationGateway);
    }

}
