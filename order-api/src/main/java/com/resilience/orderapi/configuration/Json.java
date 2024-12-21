package com.resilience.orderapi.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.resilience.domain.exception.NoStacktraceException;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.concurrent.Callable;

public enum Json {

    INSTANCE;

    public static ObjectMapper mapper() {
        return INSTANCE.objectMapper.copy();
    }

    public static String writeValueAsString(final Object obj) {
        return invoke(() -> INSTANCE.objectMapper.writeValueAsString(obj));
    }

    public static <T> T readValue(final String json, final Class<T> clazz) {
        return invoke(() -> INSTANCE.objectMapper.readValue(json, clazz));
    }

    private final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder()
        .dateFormat(new StdDateFormat())
        .featuresToDisable(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
            DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES,
            SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        )
        .modules(new JavaTimeModule(), new Jdk8Module())
        .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .build();

    private static <T> T invoke(final Callable<T> callable) {
        try {
            return callable.call();
        } catch (final Exception ex) {
            throw new NoStacktraceException("Error in JSON processor", ex);
        }
    }

}
