package com.resilience.authorizationapi.configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.NamingConvention;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricRegistryConfiguration {

    private static final String NAMESPACE = "authorization-api";

    @Bean
    public TimedAspect timedAspect(final MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer() {
        return registry -> registry.config()
            .namingConvention(namingConvention());
    }

    private NamingConvention namingConvention() {
        return (name, type, baseUnit) -> {
            final var actualMetricName = name.contains(".") ? name.replace(".", "_") : name;
            return StringUtils.joinWith("-", NAMESPACE, actualMetricName);
        };
    }

}
