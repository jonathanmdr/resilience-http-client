package com.resilience.domain.validation.handler;

import com.resilience.domain.exception.DomainException;
import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ThrowableHandlerTest {

    @Test
    void shouldBeInstantiateHandler() {
        assertThat(ThrowableHandler.create()).isNotNull();
    }

     @Test
     void shouldThrowDomainExceptionWhenAppendError() {
         final Error error = Error.of("error");
         final ValidationHandler handler = ThrowableHandler.create();

         assertThatThrownBy(() -> handler.append(error))
             .isInstanceOf(DomainException.class)
             .hasMessage("error");
         assertThat(handler.errors()).isEmpty();
         assertThat(handler.firstError()).isEmpty();
         assertThat(handler.lastError()).isEmpty();
     }

    @Test
    void shouldBeValidateWithDomainAndThrowsDomainException() {
        final ValidationHandler handler = ThrowableHandler.create();
        final StubValidation stubValidation = new StubValidation(StubValidation.ExceptionType.DOMAIN);

        assertThatThrownBy(() -> handler.validate(stubValidation))
            .isInstanceOf(DomainException.class)
            .hasMessage("domain error");
    }

    @Test
    void shouldBeValidateWithRuntimeAndThrowsDomainException() {
        final ValidationHandler handler = ThrowableHandler.create();
        final StubValidation stubValidation = new StubValidation(StubValidation.ExceptionType.RUNTIME);

        assertThatThrownBy(() -> handler.validate(stubValidation))
            .isInstanceOf(DomainException.class)
            .hasMessage("runtime error");
    }

    private record StubValidation(
        ExceptionType type
    ) implements ValidationHandler.Validation {

        @Override
        public void validate(final ValidationHandler handler) {
            switch (type) {
                case DOMAIN -> throw DomainException.with(Error.of("domain error"));
                case RUNTIME -> throw new RuntimeException("runtime error");
            }
        }

        private enum ExceptionType {
            DOMAIN,
            RUNTIME
        }

    }

}
