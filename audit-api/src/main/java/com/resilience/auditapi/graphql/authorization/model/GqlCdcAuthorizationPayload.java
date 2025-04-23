package com.resilience.auditapi.graphql.authorization.model;

public record GqlCdcAuthorizationPayload(
    String id,
    GqlAuthorization before,
    GqlAuthorization after,
    CdcSource source,
    String createdAt
) {

    public static GqlCdcAuthorizationPayload with(final String id, final GqlAuthorization before, final GqlAuthorization after, final CdcSource source, final String createdAt) {
        return new GqlCdcAuthorizationPayload(id, before, after, source, createdAt);
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
