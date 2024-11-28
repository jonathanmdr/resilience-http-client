package com.resilience.domain.validation.handler;

import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import org.junit.jupiter.api.Test;

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

}
