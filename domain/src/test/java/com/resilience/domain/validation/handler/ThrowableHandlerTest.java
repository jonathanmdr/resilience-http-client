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
         final Error error = new Error("error");
         final ValidationHandler handler = ThrowableHandler.create();

         assertThatThrownBy(() -> handler.append(error))
             .isInstanceOf(DomainException.class)
             .hasMessage("error");
         assertThat(handler.errors()).isEmpty();
         assertThat(handler.firstError()).isEmpty();
         assertThat(handler.lastError()).isEmpty();
     }

}
