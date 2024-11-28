package com.resilience.domain.exception;

public class NoStacktraceException extends RuntimeException {

    private static final boolean ENABLE_SUPPRESSION = true;
    private static final boolean WRITABLE_STACKTRACE = false;

    public NoStacktraceException(final String message) {
        this(message, null);
    }

    public NoStacktraceException(final String message, final Throwable cause) {
        super(message, cause, ENABLE_SUPPRESSION, WRITABLE_STACKTRACE);
    }

}
