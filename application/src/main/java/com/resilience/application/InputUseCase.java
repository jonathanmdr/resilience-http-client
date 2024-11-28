package com.resilience.application;

public abstract class InputUseCase<I> {

    public abstract void execute(final I input);

}
