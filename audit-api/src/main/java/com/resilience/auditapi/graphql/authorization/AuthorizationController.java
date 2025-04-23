package com.resilience.auditapi.graphql.authorization;

import com.resilience.auditapi.graphql.authorization.model.GqlAuthorization;
import com.resilience.auditapi.graphql.authorization.model.GqlCdcAuthorizationPayload;
import com.resilience.auditapi.graphql.authorization.model.GqlCdcAuthorizationPayload.CdcOperation;
import com.resilience.auditapi.graphql.authorization.model.GqlCdcAuthorizationPayload.CdcSource;
import com.resilience.auditapi.persistence.authorization.AuthorizationDataDocument;
import com.resilience.auditapi.persistence.authorization.AuthorizationDocument;
import com.resilience.auditapi.persistence.authorization.AuthorizationRepository;
import com.resilience.auditapi.persistence.common.OriginDocument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AuthorizationController {

    private final AuthorizationRepository authorizationRepository;

    public AuthorizationController(final AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
    }

    @QueryMapping
    public List<GqlCdcAuthorizationPayload> authorizations() {
        final List<AuthorizationDocument> authorizations = this.authorizationRepository.findAll();
        return authorizations.stream()
                .map(document -> {
                    GqlAuthorization before = null;
                    GqlAuthorization after = null;

                    if (document.getBefore() != null) {
                        final AuthorizationDataDocument beforeDocument = document.getBefore();
                        before = new GqlAuthorization(
                            beforeDocument.id(),
                            beforeDocument.orderId(),
                            beforeDocument.customerId(),
                            beforeDocument.amount(),
                            beforeDocument.status()
                        );
                    }

                    if (document.getAfter() != null) {
                        final AuthorizationDataDocument afterDocument = document.getAfter();
                        after = new GqlAuthorization(
                            afterDocument.id(),
                            afterDocument.orderId(),
                            afterDocument.customerId(),
                            afterDocument.amount(),
                            afterDocument.status()
                        );
                    }
                    final OriginDocument origin = document.getOrigin();
                    final CdcSource source = new CdcSource(origin.getDb(), origin.getTable(), origin.getFile(), CdcOperation.valueOf(origin.getOp()));
                    return new GqlCdcAuthorizationPayload(document.getId(), before, after, source, String.valueOf(document.getCreatedAt()));
                })
                .toList();
    }

}
