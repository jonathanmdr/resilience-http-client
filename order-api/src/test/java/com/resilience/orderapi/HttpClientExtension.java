package com.resilience.orderapi;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("integration-test")
public class HttpClientExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) {
        if (context.getRequiredTestClass().isAnnotationPresent(WebClientIntegrationTest.class)) {
            WireMock.reset();
        }
    }

}
