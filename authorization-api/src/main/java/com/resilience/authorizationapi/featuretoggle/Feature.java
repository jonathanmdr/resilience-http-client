package com.resilience.authorizationapi.featuretoggle;

public enum Feature {

    LATENCY(4000),
    UNAVAILABLE(503),
    TIMEOUT(504);

    private final int value;

    Feature(final int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

}
