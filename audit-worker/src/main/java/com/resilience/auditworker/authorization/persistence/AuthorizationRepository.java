package com.resilience.auditworker.authorization.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationRepository extends MongoRepository<AuthorizationDocument, String> {

}
