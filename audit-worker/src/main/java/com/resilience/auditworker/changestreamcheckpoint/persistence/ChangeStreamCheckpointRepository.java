package com.resilience.auditworker.changestreamcheckpoint.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeStreamCheckpointRepository extends MongoRepository<ChangeStreamCheckpointDocument, String> {

}
