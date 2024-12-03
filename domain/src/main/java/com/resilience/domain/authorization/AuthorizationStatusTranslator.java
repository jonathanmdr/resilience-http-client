package com.resilience.domain.authorization;

import java.util.function.Supplier;

@FunctionalInterface
public interface AuthorizationStatusTranslator extends Supplier<AuthorizationStatus> {

}
