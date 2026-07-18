package com.resilience.auditworker.common;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

public interface ChangeStreamDefinition {

    String streamId();

    String collectionName();

    default Criteria criteria() {
        return Criteria.where("operationType").in("insert", "update", "delete");
    }

    default MatchOperation matchOperation() {
        return Aggregation.match(criteria());
    }

    default Aggregation aggregation() {
        return Aggregation.newAggregation(matchOperation());
    }

}
