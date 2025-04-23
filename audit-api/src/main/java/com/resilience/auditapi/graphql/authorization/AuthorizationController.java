package com.resilience.auditapi.graphql.authorization;

import com.resilience.auditapi.graphql.authorization.model.GqlAuthorization;
import com.resilience.auditapi.graphql.common.GqlCdcPayload;
import com.resilience.auditapi.graphql.common.GqlCdcPayload.CdcOperation;
import com.resilience.auditapi.graphql.common.GqlCdcPayload.CdcSource;
import com.resilience.auditapi.persistence.authorization.AuthorizationDataDocument;
import com.resilience.auditapi.persistence.authorization.AuthorizationDocument;
import com.resilience.auditapi.persistence.authorization.AuthorizationRepository;
import com.resilience.auditapi.persistence.common.OriginDocument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.function.Function;

@Controller
public class AuthorizationController {

    private final AuthorizationRepository authorizationRepository;

    public AuthorizationController(final AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
    }

    @QueryMapping
    public List<GqlCdcPayload<GqlAuthorization>> authorizations() {
        final List<AuthorizationDocument> authorizations = this.authorizationRepository.findAll();
        return authorizations.stream()
            .map(toGqlCdcPayload())
            .toList();
    }

    private static Function<AuthorizationDocument, GqlCdcPayload<GqlAuthorization>> toGqlCdcPayload() {
        return document -> {
            final GqlAuthorization before = mapGqlAuthorization().apply(document.getBefore());
            final GqlAuthorization after = mapGqlAuthorization().apply(document.getAfter());
            final CdcSource source = mapGqlSource().apply(document.getOrigin());
            return new GqlCdcPayload<>(document.getId(), before, after, source, String.valueOf(document.getCreatedAt()));
        };
    }

    private static Function<AuthorizationDataDocument, GqlAuthorization> mapGqlAuthorization() {
        return document -> {
            if (document == null) {
                return null;
            }
            return new GqlAuthorization(
                document.id(),
                document.orderId(),
                document.customerId(),
                document.amount(),
                document.status()
            );
        };
    }

    private static Function<OriginDocument, CdcSource> mapGqlSource() {
        return origin -> new CdcSource(origin.db(), origin.table(), origin.file(), CdcOperation.valueOf(origin.op()));
    }

}
