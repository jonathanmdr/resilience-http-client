package com.resilience.auditworker.analysis.persistence;

import java.math.BigDecimal;

public record StatusAggregation(
    String status,
    BigDecimal total,
    Long count
) {

}
