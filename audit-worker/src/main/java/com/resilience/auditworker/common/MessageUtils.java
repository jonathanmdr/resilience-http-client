package com.resilience.auditworker.common;

import com.resilience.domain.common.ByteArrayUtils;
import org.springframework.messaging.Message;

import java.nio.charset.StandardCharsets;

public final class MessageUtils {

    private MessageUtils() { }

    public static String extractErrorFrom(final Message<String> message) {
        final byte[] stackTraceByteArray = message.getHeaders().get("x-exception-stacktrace", byte[].class);

        if (ByteArrayUtils.isNotNullOrEmpty(stackTraceByteArray)) {
            return new String(stackTraceByteArray, StandardCharsets.UTF_8);
        }

        return "The root cause cannot be obtained from exceptions raised during processing";
    }

}
