package com.resilience.auditworker.configuration;

import com.resilience.auditworker.configuration.annotation.JobConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

@EnableScheduling
@JobConfiguration
public class SchedulingConfiguration {

    @Bean
    public TaskScheduler taskScheduler(
        @Value("${application.schedulers.configuration.enable-virtual-threads}") final boolean enableVirtualThreads,
        @Value("${application.schedulers.configuration.graceful-shutdown-enabled}") final boolean gracefulEnabled,
        @Value("${application.schedulers.configuration.graceful-await-termination-in-seconds}") final int awaitTerminationInSeconds
    ) {
        final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setVirtualThreads(enableVirtualThreads);
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(gracefulEnabled);
        threadPoolTaskScheduler.setAwaitTerminationSeconds(awaitTerminationInSeconds);
        threadPoolTaskScheduler.setErrorHandler(new SchedulerGlobalErrorHandler());
        return threadPoolTaskScheduler;
    }

    private static class SchedulerGlobalErrorHandler implements ErrorHandler {

        private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerGlobalErrorHandler.class);

        @SuppressWarnings("all")
        @Override
        public void handleError(final Throwable throwable) {
            LOGGER.error("An unexpected error occurred on scheduled job", throwable);
        }

    }

}
