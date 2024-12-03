package com.resilience.domain.authorization;

public final class AuthorizationStatusTranslatorService implements AuthorizationStatusTranslator {

    private final String authorizationStatus;

    private AuthorizationStatusTranslatorService(final String authorizationStatus) {
        this.authorizationStatus = authorizationStatus;
    }

    public static AuthorizationStatusTranslatorService create(final String authorizationStatus) {
        return new AuthorizationStatusTranslatorService(authorizationStatus);
    }

    @Override
    public AuthorizationStatus get() {
        return switch (this.authorizationStatus) {
            case "APPROVED" -> AuthorizationStatus.APPROVED;
            case "REFUSED" -> AuthorizationStatus.REFUSED;
            default -> AuthorizationStatus.PENDING;
        };
    }

}
