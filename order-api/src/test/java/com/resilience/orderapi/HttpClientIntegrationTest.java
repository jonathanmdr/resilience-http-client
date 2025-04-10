package com.resilience.orderapi;

import io.github.resilience4j.springboot3.bulkhead.autoconfigure.BulkheadAutoConfiguration;
import io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakerAutoConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("integration-test")
@RestClientTest
@Import(
    value = {
        CircuitBreakerAutoConfiguration.class,
        BulkheadAutoConfiguration.class
    }
)
@EnableAspectJAutoProxy
@AutoConfigureWireMock(port = 9090)
@ExtendWith(HttpClientExtension.class)
public @interface HttpClientIntegrationTest {

    @AliasFor(annotation = RestClientTest.class, attribute = "components")
    Class<?>[] components() default {};

}
