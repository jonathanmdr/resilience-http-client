package com.resilience.auditworker.analysis.persistence;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public interface AnalysisRepository {

    String MATCH_STAGE = """
        {
          "$match": {
            "origin.operation": { "$in": ["CREATE", "UPDATE"] },
            "created_at": {
              "$gte": $$NOW_DAY_START,
              "$lt": $$NOW_DAY_END
            }
          }
        }
        """;
    String PROJECT_STAGE = """
        {
          "$project": {
            "status": "$after.status",
            "amount": "$after.amount"
          }
        }
        """;
    String UNION_WITH_STAGE = """
        {
          "$unionWith": {
            "coll": "authorizations",
            "pipeline": [
              {
                "$match": {
                  "origin.operation": { "$in": ["CREATE", "UPDATE"] },
                  "created_at": {
                    "$gte": $$NOW_DAY_START,
                    "$lt": $$NOW_DAY_END
                  }
                }
              },
              {
                "$project": {
                  "status": "$after.status",
                  "amount": "$after.amount"
                }
              }
            ]
          }
        }
        """;
    String GROUP_STAGE = """
        {
          "$group": {
            "_id": "$status",
            "total": { "$sum": "$amount" },
            "count": { "$sum": 1 }
          }
        }
        """;
    String SORT_STAGE = """
        {
          "$sort": { "total": -1 }
        }
        """;

    default List<String> getPipelineStages() {
        return List.of(
            replaceDatePlaceholders(MATCH_STAGE),
            PROJECT_STAGE,
            replaceDatePlaceholders(UNION_WITH_STAGE),
            GROUP_STAGE,
            SORT_STAGE
        );
    }

    List<StatusAggregation> aggregateSalesAndAuthorizationsByStatus();

    private Date getUtcStartOfDay() {
        return Date.from(ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS).toInstant());
    }

    private Date getUtcEndOfDay() {
        return Date.from(ZonedDateTime.now(ZoneOffset.UTC).plusDays(1).truncatedTo(ChronoUnit.DAYS).toInstant());
    }

    private String replaceDatePlaceholders(final String json) {
        return json.replace("$$NOW_DAY_START", toJsonDate(getUtcStartOfDay())).replace("$$NOW_DAY_END", toJsonDate(getUtcEndOfDay()));
    }

    private String toJsonDate(final Date date) {
        return """
            { "$date": "%s" }
        """.formatted(date.toInstant().toString());
    }

}
