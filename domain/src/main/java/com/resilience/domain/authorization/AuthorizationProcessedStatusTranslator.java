package com.resilience.domain.authorization;

import java.util.function.Supplier;

@FunctionalInterface
public interface AuthorizationProcessedStatusTranslator extends Supplier<AuthorizationStatus> {

}
