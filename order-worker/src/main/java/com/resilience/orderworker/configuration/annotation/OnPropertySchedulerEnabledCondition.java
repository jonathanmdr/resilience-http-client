package com.resilience.orderworker.configuration.annotation;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OnPropertySchedulerEnabledCondition implements Condition {

    @SuppressWarnings("all")
    @Override
    public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty("application.schedulers.enabled", Boolean.class, true);
    }

}
