package com.resilience.orderworker.authorization.job;

import com.resilience.orderworker.authorization.AuthorizationDatabaseGateway;
import com.resilience.orderworker.configuration.annotation.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;

@Job
public class CleanupAuthorizationsProcessedDltJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanupAuthorizationsProcessedDltJob.class);
    private static final int DEFAULT_BATCH_SIZE = 100;

    private final AuthorizationDatabaseGateway authorizationDatabaseGateway;
    private final Environment environment;

    public CleanupAuthorizationsProcessedDltJob(final AuthorizationDatabaseGateway authorizationDatabaseGateway, final Environment environment) {
        this.authorizationDatabaseGateway = authorizationDatabaseGateway;
        this.environment = environment;
    }

    @Scheduled(
        cron = "${application.schedulers.jobs.cleanup-authorizations-processed-dlt.cron}",
        zone = "${application.schedulers.jobs.cleanup-authorizations-processed-dlt.zone}"
    )
    public void run() {
        LOGGER.info("Running cleanup authorizations processed DLT job");

        final int batchSize = this.environment.getProperty("application.schedulers.jobs.cleanup-authorizations-processed-dlt.batch-size", Integer.class, DEFAULT_BATCH_SIZE);
        this.authorizationDatabaseGateway.cleanup(batchSize);

        LOGGER.info("Cleanup authorizations processed DLT job finished");
    }

}
