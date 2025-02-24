package com.resilience.orderapi.integration.messaging;

public enum StreamBinding {

    AUTHORIZATION_ORDER_EVENTS("authorizationOrderEvents-out-0");

    private final String key;

    StreamBinding(final String key) {
        this.key = key;
    }

    public String key() {
        return this.key;
    }

}
