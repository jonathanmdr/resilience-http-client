package com.resilience.auditapi.graphql.common;

public record GqlCdcPayload<T>(
    String id,
    T before,
    T after,
    CdcSource source,
    String createdAt
) {

    public static <T> GqlCdcPayload<T> with(final String id, final T before, final T after, final CdcSource source, final String createdAt) {
        return new GqlCdcPayload<>(id, before, after, source, createdAt);
    }

    public record CdcSource(
        String db,
        String table,
        String file,
        CdcOperation op
    ) {

        public static CdcSource with(final String db, final String table, final String file, final CdcOperation op) {
            return new CdcSource(db, table, file, op);
        }

    }

    public enum CdcOperation {

        CREATE, UPDATE, DELETE

    }

}
