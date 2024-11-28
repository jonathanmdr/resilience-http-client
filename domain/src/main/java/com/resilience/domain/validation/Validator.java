package com.resilience.domain.validation;

public abstract class Validator {

    private final ValidationHandler validationHandler;

    protected Validator(final ValidationHandler validationHandler) {
        this.validationHandler = validationHandler;
    }

    public abstract void validate();

    protected ValidationHandler validationHandler() {
        return this.validationHandler;
    }

}
