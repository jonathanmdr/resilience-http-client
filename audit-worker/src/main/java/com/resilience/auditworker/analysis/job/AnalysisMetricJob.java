package com.resilience.auditworker.analysis.job;

import com.resilience.auditworker.analysis.persistence.AnalysisRepository;
import com.resilience.auditworker.analysis.persistence.StatusAggregation;
import com.resilience.auditworker.configuration.annotation.Job;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

@Job
public class AnalysisMetricJob {

    private static final Logger LOG = LoggerFactory.getLogger(AnalysisMetricJob.class);

    private final AnalysisRepository analysisRepository;
    private final MeterRegistry meterRegistry;
    private final Map<String, AtomicReference<BigDecimal>> totalByStatus;
    private final Map<String, AtomicLong> countByStatus;

    public AnalysisMetricJob(final AnalysisRepository analysisRepository, final MeterRegistry meterRegistry) {
        this.analysisRepository = analysisRepository;
        this.meterRegistry = meterRegistry;
        this.totalByStatus = new ConcurrentHashMap<>();
        this.countByStatus = new ConcurrentHashMap<>();
    }

    @Scheduled(
        cron = "${application.schedulers.jobs.analysis-job.cron}",
        zone = "${application.schedulers.jobs.analysis-job.zone}"
    )
    public void job() {
        final List<StatusAggregation> statuses = this.analysisRepository.aggregateSalesAndAuthorizationsByStatus();
        updateStatusMetrics(statuses);
        LOG.info("Results of sales and authorizations analysis: {}", statuses);
    }

    private void updateStatusMetrics(final List<StatusAggregation> statuses) {
        for (final StatusAggregation currentStatus : statuses) {
            final String status = currentStatus.status();
            final BigDecimal total = currentStatus.total();
            final long count = currentStatus.count();

            this.totalByStatus.computeIfAbsent(status, registerGaugeForStatusTotal()).set(total);
            this.countByStatus.computeIfAbsent(status, registerGaugeForStatusCount()).set(count);
        }
    }

    private Function<String, AtomicLong> registerGaugeForStatusCount() {
        return status -> {
            final AtomicLong ref = new AtomicLong(0);
            Gauge.builder("sales_status_count", ref, AtomicLong::doubleValue)
                .tag("status", status)
                .register(this.meterRegistry);
            return ref;
        };
    }

    private Function<String, AtomicReference<BigDecimal>> registerGaugeForStatusTotal() {
        return status -> {
            final AtomicReference<BigDecimal> ref = new AtomicReference<>(BigDecimal.ZERO);
            Gauge.builder("sales_status_total", ref, fromBigDecimalToDoubleValueFunction())
                .tag("status", status)
                .register(this.meterRegistry);
            return ref;
        };
    }

    private static ToDoubleFunction<AtomicReference<BigDecimal>> fromBigDecimalToDoubleValueFunction() {
        return bigDecimalAtomicReference -> bigDecimalAtomicReference.get().doubleValue();
    }

}
