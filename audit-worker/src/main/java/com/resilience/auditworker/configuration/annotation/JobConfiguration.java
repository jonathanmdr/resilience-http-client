package com.resilience.auditworker.configuration.annotation;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Configuration
@Conditional(OnPropertySchedulerEnabledCondition.class)
public @interface JobConfiguration {

}
