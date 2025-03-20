package com.resilience.orderworker.authorization.job;

import com.resilience.orderworker.IntegrationTest;
import com.resilience.orderworker.authorization.AuthorizationDatabaseGateway;
import com.resilience.orderworker.configuration.SchedulingConfiguration;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.logging.LogLevel.INFO;

@IntegrationTest(
    properties = {
        "application.schedulers.enabled=true"
    },
    classes = {
        CleanupAuthorizationsProcessedDltJob.class,
        SchedulingConfiguration.class
    }
)
@ExtendWith(OutputCaptureExtension.class)
class CleanupAuthorizationsProcessedDltJobSchedulerIntegrationTest {

    @MockitoSpyBean
    private CleanupAuthorizationsProcessedDltJob subject;

    @MockitoBean
    private AuthorizationDatabaseGateway authorizationDatabaseGateway;

    @Test
    void givenAScheduledJob_whenJobRun_thenExecuteCleanup(final CapturedOutput output) {
        Awaitility.await()
            .atMost(Duration.ofSeconds(2))
            .untilAsserted(() -> {
                verify(this.subject, atLeastOnce()).run();
                verify(this.authorizationDatabaseGateway).cleanup(10);
                assertThat(output.getOut())
                    .contains(INFO.name(), "Running cleanup authorizations processed DLT job")
                    .contains(INFO.name(), "Cleanup authorizations processed DLT job finished");
            });
    }

}
