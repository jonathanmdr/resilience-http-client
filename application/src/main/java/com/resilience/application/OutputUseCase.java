package com.resilience.application;

import com.resilience.domain.common.Result;
import com.resilience.domain.validation.ValidationHandler;

public abstract class OutputUseCase<O> {

    public abstract Result<O, ValidationHandler> execute();

}
