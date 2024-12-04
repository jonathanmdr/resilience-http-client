package com.resiliente.orderapi.integration.messaging;

public enum StreamBridgeBinding {

    AUTHORIZATION_ORDER_EVENTS("authorizationOrderEvents-out-0");

    private final String key;

    StreamBridgeBinding(final String key) {
        this.key = key;
    }

    public String key() {
        return this.key;
    }

}
