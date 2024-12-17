package com.resilience.orderapi;

import com.resiliente.orderapi.OrderApi;
import com.resiliente.orderapi.configuration.ObjectMapperConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("integration-test")
@WebMvcTest
@Import(ObjectMapperConfiguration.class)
@ContextConfiguration(classes = OrderApi.class)
public @interface ApiIntegrationTest {

    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] controllers() default {};

}
