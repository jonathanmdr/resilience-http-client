package com.resilience.domain.validation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface ValidationHandler {

    ValidationHandler append(final Error error);

    default List<Error> errors() {
        return Collections.emptyList();
    }

    default boolean hasErrors() {
        return !errors().isEmpty();
    }

    default Optional<Error> firstError() {
        return hasErrors() ? Optional.of(errors().getFirst()) : Optional.empty();
    }

    default Optional<Error> lastError() {
        return hasErrors() ? Optional.of(errors().getLast()) : Optional.empty();
    }

}
