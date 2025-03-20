package com.resilience.orderworker.authorization.persistence;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorizationProcessedDltRepository extends JpaRepository<AuthorizationProcessedDltJpaEntity, String> {

    String JAKARTA_SKIP_LOCKED_VALUE = "-2";

    @QueryHints(
        value = @QueryHint(
            name = AvailableSettings.JAKARTA_LOCK_TIMEOUT,
            value = JAKARTA_SKIP_LOCKED_VALUE
        )
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<AuthorizationProcessedDltJpaEntity> findAllBy(final Limit limit);

}
