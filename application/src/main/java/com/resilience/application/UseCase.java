package com.resilience.application;

import com.resilience.domain.common.Result;
import com.resilience.domain.validation.ValidationHandler;

public abstract class UseCase<I, O> {

    public abstract Result<O, ValidationHandler> execute(final I input);

}
