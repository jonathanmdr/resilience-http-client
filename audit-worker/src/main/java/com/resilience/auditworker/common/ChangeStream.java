package com.resilience.auditworker.common;

public enum ChangeStream implements ChangeStreamDefinition {

    ORDER {
        @Override
        public String streamId() {
            return "orders-stream";
        }

        @Override
        public String collectionName() {
            return "orders";
        }
    }

}
