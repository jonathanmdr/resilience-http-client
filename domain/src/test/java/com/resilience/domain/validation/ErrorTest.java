package com.resilience.domain.validation;

import com.resilience.domain.exception.NoStacktraceException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ErrorTest {

    @Test
    void shouldThrowNoStacktraceExceptionWhenMessageIsNull() {
        assertThatThrownBy(() -> new Error(null))
            .isInstanceOf(NoStacktraceException.class)
            .hasMessage("Error message must be provided");
    }

    @Test
    void shouldThrowNoStacktraceExceptionWhenMessageIsBlank() {
        assertThatThrownBy(() -> new Error(" "))
            .isInstanceOf(NoStacktraceException.class)
            .hasMessage("Error message must be provided");
    }

}
