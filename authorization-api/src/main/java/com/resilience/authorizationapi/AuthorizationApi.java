package com.resilience.authorizationapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.math.BigDecimal;

@SpringBootApplication
public class AuthorizationApi {

    private final Logger log = LoggerFactory.getLogger(AuthorizationApi.class);

    public static void main(final String ... args) {
        SpringApplication.run(AuthorizationApi.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route()
            .POST("/v1/authorizations", authorizeOrderHandler())
            .build();
    }

    private HandlerFunction<ServerResponse> authorizeOrderHandler() {
        return request -> {
            final AuthorizationRequest authorizationRequest = request.body(AuthorizationRequest.class);
            log.info("Received authorization request: {}", authorizationRequest);
            return ServerResponse.ok()
                .body(new AuthorizationResponse("APPROVED"));
        };
    }

    public record AuthorizationRequest(String authorizationId, String orderId, String customerId, BigDecimal orderAmount) { }
    public record AuthorizationResponse(String status) { }

}