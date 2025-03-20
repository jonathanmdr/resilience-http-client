package com.resilience.orderworker.authorization;

import com.resilience.domain.common.CollectionUtils;
import com.resilience.orderworker.authorization.persistence.AuthorizationProcessedDltJpaEntity;
import com.resilience.orderworker.authorization.persistence.AuthorizationProcessedDltRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;

@Component
public class AuthorizationDatabaseGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationDatabaseGateway.class);

    private final AuthorizationProcessedDltRepository authorizationProcessedDltRepository;
    private final TransactionTemplate transactionTemplate;

    public AuthorizationDatabaseGateway(
        final AuthorizationProcessedDltRepository authorizationProcessedDltRepository,
        final TransactionTemplate transactionTemplate
    ) {
        this.authorizationProcessedDltRepository = authorizationProcessedDltRepository;
        this.transactionTemplate = transactionTemplate;
    }

    public void cleanup(final int batchSize) {
        boolean hasEventsToDelete;

        do {
            hasEventsToDelete = Objects.equals(Boolean.TRUE, this.transactionTemplate.execute(findDltEventsAndDelete(batchSize)));
        } while (hasEventsToDelete);
    }

    private TransactionCallback<Boolean> findDltEventsAndDelete(final int batchSize) {
        return transactionStatus -> {
            final List<AuthorizationProcessedDltJpaEntity> entities = this.authorizationProcessedDltRepository.findAllBy(Limit.of(batchSize));

            if (CollectionUtils.isNullOrEmpty(entities)) {
                return false;
            }

            final List<String> idsToRemove = entities.stream()
                .map(AuthorizationProcessedDltJpaEntity::getId)
                .toList();

            this.authorizationProcessedDltRepository.deleteAllByIdInBatch(idsToRemove);

            LOGGER.info("Deleted {} authorizations processed DLT", idsToRemove.size());

            return true;
        };
    }

}
