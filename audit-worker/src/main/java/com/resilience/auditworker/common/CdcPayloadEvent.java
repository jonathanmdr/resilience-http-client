package com.resilience.auditworker.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record CdcPayloadEvent<T>(
    T before,
    T after,
    CdcSource source,
    CdcOperation op
) {

    public static <T> CdcPayloadEvent<T> with(final T before, final T after, final CdcSource source, final CdcOperation op) {
        return new CdcPayloadEvent<>(before, after, source, op);
    }

    public record CdcSource(
        String db,
        String table,
        String file
    ) {

        public static CdcSource with(final String db, final String table, final String file) {
            return new CdcSource(db, table, file);
        }

    }

    public enum CdcOperation {

        CREATE("c"),
        UPDATE("u"),
        DELETE("d");

        private final String op;

        @JsonCreator
        CdcOperation(final String op) {
            this.op = op;
        }

        @JsonValue
        public String op() {
            return op;
        }

    }

}
