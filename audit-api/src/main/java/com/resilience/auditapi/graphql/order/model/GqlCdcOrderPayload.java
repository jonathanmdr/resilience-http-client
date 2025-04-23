package com.resilience.auditapi.graphql.order.model;

public record GqlCdcOrderPayload(
    String id,
    GqlOrder before,
    GqlOrder after,
    CdcSource source,
    String createdAt
) {

    public static GqlCdcOrderPayload with(final String id, final GqlOrder before, final GqlOrder after, final CdcSource source, final String createdAt) {
        return new GqlCdcOrderPayload(id, before, after, source, createdAt);
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
