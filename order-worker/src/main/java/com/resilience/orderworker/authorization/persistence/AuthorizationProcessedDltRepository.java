package com.resilience.orderworker.authorization.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationProcessedDltRepository extends JpaRepository<AuthorizationProcessedDltJpaEntity, String> {

}
