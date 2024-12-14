package com.resilience.domain.validation.handler;

import com.resilience.domain.exception.DomainException;
import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationHandlerTest {

    @Test
    void shouldBeInstantiateHandler() {
        final ValidationHandler handler = NotificationHandler.create();

        assertThat(handler).isNotNull();
        assertThat(handler.errors()).isEmpty();
        assertThat(handler.firstError()).isEmpty();
        assertThat(handler.lastError()).isEmpty();
    }

    @Test
    void shouldBeInstantiateHandlerWithOneError() {
        final Error error = new Error("error");

        final ValidationHandler handler = NotificationHandler.create(error);

        assertThat(handler).isNotNull();
        assertThat(handler.errors()).containsExactly(error);
        assertThat(handler.firstError()).contains(error);
        assertThat(handler.lastError()).contains(error);
    }

    @Test
    void shouldBeInstantiateHandlerWithOneThrowable() {
        final Error error = new Error("error");
        final Throwable throwable = new Throwable("error");

        final ValidationHandler handler = NotificationHandler.create(throwable);

        assertThat(handler).isNotNull();
        assertThat(handler.errors()).containsExactly(error);
        assertThat(handler.firstError()).contains(error);
        assertThat(handler.lastError()).contains(error);
    }

    @Test
    void shouldBeAppendError() {
        final Error errorOne = new Error("error one");
        final Error errorTwo = new Error("error two");
        final ValidationHandler handler = NotificationHandler.create();

        handler.append(errorOne);
        handler.append(errorTwo);

        assertThat(handler).isNotNull();
        assertThat(handler.errors()).containsExactly(errorOne, errorTwo);
        assertThat(handler.firstError()).contains(errorOne);
        assertThat(handler.lastError()).contains(errorTwo);
    }

    @Test
    void shouldBeValidateStubValidationRuntimeException() {
        final Error error = new Error("runtime error");
        final ValidationHandler handler = NotificationHandler.create();
        final StubValidation stubValidation = new StubValidation(StubValidation.ExceptionType.RUNTIME);

        handler.validate(stubValidation);

        assertThat(handler).isNotNull();
        assertThat(handler.errors()).containsExactly(error);
        assertThat(handler.firstError()).contains(error);
        assertThat(handler.lastError()).contains(error);
    }

    @Test
    void shouldBeValidateStubValidationDomainException() {
        final Error error = new Error("domain error");
        final ValidationHandler handler = NotificationHandler.create();
        final StubValidation stubValidation = new StubValidation(StubValidation.ExceptionType.DOMAIN);

        handler.validate(stubValidation);

        assertThat(handler).isNotNull();
        assertThat(handler.errors()).containsExactly(error);
        assertThat(handler.firstError()).contains(error);
        assertThat(handler.lastError()).contains(error);
    }

    private record StubValidation(
        ExceptionType type
    ) implements ValidationHandler.Validation {

        @Override
        public void validate(final ValidationHandler handler) {
            switch (type) {
                case DOMAIN -> throw DomainException.with("domain error", List.of(new Error("domain error")));
                case RUNTIME -> throw new RuntimeException("runtime error");
            }
        }

        private enum ExceptionType {
            DOMAIN,
            RUNTIME
        }

    }

}
