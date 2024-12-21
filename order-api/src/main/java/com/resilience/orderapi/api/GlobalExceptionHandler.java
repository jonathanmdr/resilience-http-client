package com.resilience.orderapi.api;

import com.resilience.domain.exception.DomainException;
import com.resilience.domain.validation.Error;
import com.resilience.orderapi.integration.http.HttpIntegrationException;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleUncaughtException(final Throwable throwable) {
        final var responseBody = ApiError.from("An unidentified error has occurred");
        return ResponseEntity.internalServerError()
            .body(responseBody);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(final ConstraintViolationException exception) {
        final var errors = exception.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .toList();
        final var responseBody = ApiError.from(BAD_REQUEST.getReasonPhrase(), errors);
        return ResponseEntity.badRequest()
            .body(responseBody);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleHandlerMethodValidationException(final HandlerMethodValidationException exception) {
        final var errors = exception.getParameterValidationResults()
            .stream()
            .map(ParameterValidationResult::getResolvableErrors)
            .flatMap(List::stream)
            .map(toErrorFieldByField())
            .toList();
        final var responseBody = ApiError.from(BAD_REQUEST.getReasonPhrase(), errors);
        return ResponseEntity.badRequest()
            .body(responseBody);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        final var errors = exception.getBindingResult()
            .getAllErrors()
            .stream()
            .map(toErrorFieldByField())
            .toList();
        final var responseBody = ApiError.from(BAD_REQUEST.getReasonPhrase(), errors);
        return ResponseEntity.badRequest()
            .body(responseBody);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiError> handleMissingRequestHeaderException(final MissingRequestHeaderException exception) {
        final var responseBody = ApiError.from(BAD_REQUEST.getReasonPhrase(), List.of(exception.getMessage()));
        return ResponseEntity.badRequest()
            .body(responseBody);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingServletRequestParameterException(final MissingServletRequestParameterException exception) {
        final var responseBody = ApiError.from(BAD_REQUEST.getReasonPhrase(), List.of(exception.getMessage()));
        return ResponseEntity.badRequest()
            .body(responseBody);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException exception) {
        final var errors = List.of(
            "The parameter '%s' doesn't accept the value '%s'".formatted(exception.getPropertyName(), exception.getValue())
        );
        final var responseBody = ApiError.from(BAD_REQUEST.getReasonPhrase(), errors);
        return ResponseEntity.badRequest()
            .body(responseBody);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException exception) {
        final var responseBody = ApiError.from(BAD_REQUEST.getReasonPhrase(), List.of(exception.getMessage()));
        return ResponseEntity.badRequest()
            .body(responseBody);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleHttpMediaTypeNotSupportedException(final HttpMediaTypeNotSupportedException exception) {
        final var responseBody = ApiError.from(UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(), List.of(exception.getMessage()));
        return ResponseEntity.status(UNSUPPORTED_MEDIA_TYPE)
            .body(responseBody);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(final HttpMessageNotReadableException exception) {
        final var responseBody = ApiError.from("Required request body is missing or invalid");
        return ResponseEntity.badRequest()
            .body(responseBody);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandlerFoundException(final NoHandlerFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(HttpIntegrationException.class)
    public ResponseEntity<ApiError> handleHttpIntegrationException(final HttpIntegrationException exception) {
        final var responseBody = ApiError.from(exception.getMessage(), exception.errors().stream().map(Error::message).toList());
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY)
            .body(responseBody);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomainException(final DomainException exception) {
        final var responseBody = ApiError.from(exception.getMessage(), exception.errors().stream().map(Error::message).toList());
        return ResponseEntity.status(UNPROCESSABLE_ENTITY)
            .body(responseBody);
    }

    public record ApiError(
        @Schema(example = "Order cannot be processed") String detail,
        @ArraySchema(schema = @Schema(example = "Amount must be greater than zero")) List<String> errors
    ) {

        public static ApiError from(final Exception exception) {
            return new ApiError(exception.getMessage(), Collections.emptyList());
        }

        public static ApiError from(final String message) {
            return new ApiError(message, Collections.emptyList());
        }

        public static ApiError from(final String message, final List<String> errors) {
            return new ApiError(message, errors);
        }

    }

    private static Function<MessageSourceResolvable, String> toErrorFieldByField() {
        return error -> {
            if (error instanceof FieldError fieldError) {
                final var fieldName = fieldError.getField();
                return StringUtils.join(fieldName, " ", fieldError.getDefaultMessage());
            }
            return error.getDefaultMessage();
        };
    }

}
