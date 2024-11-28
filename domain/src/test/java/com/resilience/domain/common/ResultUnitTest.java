package com.resilience.domain.common;

import com.resilience.domain.exception.DomainException;
import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.handler.NotificationHandler;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResultUnitTest {

    @Test
    void givenASuccessResponse_whenCallsHasSuccess_shouldReturnTrue() {
        final var actual = Result.success("success");
        assertThat(actual.hasSuccess()).isTrue();
    }

    @Test
    void givenAnErrorResponse_whenCallsHasSuccess_shouldReturnFalse() {
        final var handler = NotificationHandler.create();
        handler.append(new com.resilience.domain.validation.Error("error"));
        final var actual = Result.error(handler);
        assertThat(actual.hasSuccess()).isFalse();
    }

    @Test
    void givenASuccessResponse_whenCallsHasError_shouldReturnFalse() {
        final var actual = Result.success("success");
        assertThat(actual.hasError()).isFalse();
    }

    @Test
    void givenAnErrorResponse_whenCallsHasError_shouldReturnTrue() {
        final var handler = NotificationHandler.create();
        handler.append(new com.resilience.domain.validation.Error("error"));
        final var actual = Result.error(handler);
        assertThat(actual.hasError()).isTrue();
    }

    @Test
    void givenASuccessResponse_whenCallsSuccess_shouldReturnSuccessData() {
        final var clientResponse = Result.success("success");
        final var actual = clientResponse.success();
        assertThat(actual).isEqualTo("success");
    }

    @Test
    void givenAnErrorResponse_whenCallsSuccess_shouldThrowsBusinessException() {
        final var handler = NotificationHandler.create();
        handler.append(new com.resilience.domain.validation.Error("error"));
        final var clientResponse = Result.error(handler);

        assertThatThrownBy(clientResponse::success)
            .isInstanceOf(DomainException.class)
            .hasMessage("The 'success' object cannot be invoked when an 'error' object exists");
    }

    @Test
    void givenAnErrorResponse_whenCallsError_shouldReturnErrorData() {
        final var handler = NotificationHandler.create();
        handler.append(new Error("error"));
        final var clientResponse = Result.error(handler);
        final var actual = clientResponse.error();
        assertThat(actual)
            .isNotNull()
            .extracting(ValidationHandler::lastError)
            .extracting(Optional::get)
            .extracting(Error::message)
            .isEqualTo("error");
    }

    @Test
    void givenASuccessResponse_whenCallsError_shouldThrowsBusinessException() {
        final var clientResponse = Result.success("success");

        assertThatThrownBy(clientResponse::error)
            .isInstanceOf(DomainException.class)
            .hasMessage("The 'error' object cannot be invoked when an 'success' object exists");
    }

}
