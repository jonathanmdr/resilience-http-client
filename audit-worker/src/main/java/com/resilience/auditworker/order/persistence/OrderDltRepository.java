package com.resilience.auditworker.order.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDltRepository extends MongoRepository<OrderDltDocument, String> {

}
