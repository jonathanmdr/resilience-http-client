package com.resilience.auditworker.analysis.persistence;

import com.mongodb.client.AggregateIterable;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.StreamSupport;

@Repository
public class CustomAnalysisRepository implements AnalysisRepository {

    private final MongoTemplate mongoTemplate;

    public CustomAnalysisRepository(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<StatusAggregation> aggregateSalesAndAuthorizationsByStatus() {
        final List<Document> pipeline = getPipelineStages().stream()
            .map(Document::parse)
            .toList();

        final AggregateIterable<Document> result = this.mongoTemplate.getDb()
            .getCollection("orders")
            .aggregate(pipeline);

        return StreamSupport.stream(result.spliterator(), false)
            .map(doc -> new StatusAggregation(
                doc.getString("_id"),
                doc.get("total", Decimal128.class).bigDecimalValue(),
                doc.getInteger("count").longValue()
            ))
            .toList();
    }

}
