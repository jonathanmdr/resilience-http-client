package com.resilience.domain.common;

public final class ByteArrayUtils {

    private ByteArrayUtils() { }

    public static boolean isNullOrEmpty(final byte[] bytes) {
        return bytes == null || bytes.length == 0;
    }

    public static boolean isNotNullOrEmpty(final byte[] bytes) {
        return !isNullOrEmpty(bytes);
    }

}
