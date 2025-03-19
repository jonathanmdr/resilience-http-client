package com.resilience.domain.validation;

import com.resilience.domain.common.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface ValidationHandler {

    ValidationHandler append(final Error error);

    void validate(final Validation validation);

    default List<Error> errors() {
        return Collections.emptyList();
    }

    default boolean hasErrors() {
        return CollectionUtils.isNotNullOrEmpty(errors());
    }

    default Optional<Error> firstError() {
        return hasErrors() ? Optional.of(errors().getFirst()) : Optional.empty();
    }

    default Optional<Error> lastError() {
        return hasErrors() ? Optional.of(errors().getLast()) : Optional.empty();
    }

    interface Validation {

        void validate(final ValidationHandler handler);

    }

}
