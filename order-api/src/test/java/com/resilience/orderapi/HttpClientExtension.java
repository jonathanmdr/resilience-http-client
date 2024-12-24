package com.resilience.orderapi;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("integration-test")
public class HttpClientExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) {
        if (context.getRequiredTestClass().isAnnotationPresent(HttpClientIntegrationTest.class)) {
            WireMock.reset();
        }

        final ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        applicationContext.getBean(CircuitBreakerRegistry.class)
            .getAllCircuitBreakers()
            .forEach(CircuitBreaker::reset);
    }

}
