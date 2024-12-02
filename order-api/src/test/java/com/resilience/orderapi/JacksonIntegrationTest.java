package com.resilience.orderapi;

import com.resiliente.orderapi.OrderApi;
import com.resiliente.orderapi.configuration.ObjectMapperConfiguration;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
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
@JsonTest(
    includeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = ObjectMapperConfiguration.class
        )
    }
)
@ContextConfiguration(
    classes = {
        OrderApi.class
    }
)
public @interface JacksonIntegrationTest {

}
