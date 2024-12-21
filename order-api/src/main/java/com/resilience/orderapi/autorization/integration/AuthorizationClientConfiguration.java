package com.resilience.orderapi.autorization.integration;

import com.resilience.orderapi.integration.http.BaseClientProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Configuration
public class AuthorizationClientConfiguration {

    @Bean
    @Validated
    @AuthorizationClientProperties
    @ConfigurationProperties(prefix = "application.clients.authorization")
    public BaseClientProperties authorizationClientProperties() {
        return new BaseClientProperties();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(
        value = {
            ElementType.FIELD,
            ElementType.METHOD,
            ElementType.PARAMETER,
            ElementType.TYPE,
            ElementType.ANNOTATION_TYPE
        }
    )
    @Qualifier
    public @interface AuthorizationClientProperties { }

}
