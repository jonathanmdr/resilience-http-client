package com.resilience.orderworker.authorization;

import com.resilience.orderworker.DatabaseRepositoryIntegrationTest;
import com.resilience.orderworker.authorization.persistence.AuthorizationProcessedDltJpaEntity;
import com.resilience.orderworker.authorization.persistence.AuthorizationProcessedDltRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Limit;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.logging.LogLevel.INFO;

@DatabaseRepositoryIntegrationTest(
    includeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
                AuthorizationDatabaseGateway.class
            }
        )
    }
)
@ExtendWith(OutputCaptureExtension.class)
class AuthorizationDatabaseGatewayIntegrationTest {

    @Autowired
    private AuthorizationDatabaseGateway subject;

    @MockitoSpyBean
    @Autowired
    private AuthorizationProcessedDltRepository authorizationProcessedDltRepository;

    @MockitoSpyBean
    private TransactionTemplate transactionTemplate;

    @Test
    void givenEventsOnDlt_whenCallsCleanup_thenDeleteEvents(final CapturedOutput output) {
        final List<AuthorizationProcessedDltJpaEntity> entities = IntStream.range(1, 16)
            .mapToObj(index -> {
                final AuthorizationProcessedDltJpaEntity entity = new AuthorizationProcessedDltJpaEntity();
                entity.setId(String.valueOf(index));
                entity.setPayload("{}");
                entity.setError("any error");
                return entity;
            }).toList();
        this.authorizationProcessedDltRepository.saveAllAndFlush(entities);

        assertThat(this.authorizationProcessedDltRepository.count()).isEqualTo(15);

        this.subject.cleanup(10);

        assertThat(this.authorizationProcessedDltRepository.count()).isZero();

        verify(this.transactionTemplate, times(3)).execute(any());
        verify(this.authorizationProcessedDltRepository, times(3)).findAllBy(Limit.of(10));

        @SuppressWarnings("all")
        final ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(this.authorizationProcessedDltRepository, times(2)).deleteAllByIdInBatch(captor.capture());
        final List<String> firstBatch = captor.getAllValues().getFirst();
        final List<String> lastBatch = captor.getAllValues().getLast();

        assertThat(firstBatch)
            .hasSize(10)
            .isEqualTo(getRangeIdsRemovedBy(0, 10, entities));
        assertThat(lastBatch)
            .hasSize(5)
            .isEqualTo(getRangeIdsRemovedBy(10, 15, entities));
        assertThat(output.getOut())
            .contains(INFO.name(), "Deleted 10 authorizations processed DLT")
            .contains(INFO.name(), "Deleted 5 authorizations processed DLT");
    }

    @Test
    void givenEventsOnDltIsEmpty_whenCallsCleanup_thenDoNothing() {
        assertThat(this.authorizationProcessedDltRepository.count()).isZero();

        this.subject.cleanup(10);

        assertThat(this.authorizationProcessedDltRepository.count()).isZero();

        verify(this.transactionTemplate).execute(any());
        verify(this.authorizationProcessedDltRepository).findAllBy(Limit.of(10));
        verify(this.authorizationProcessedDltRepository, never()).deleteAllByIdInBatch(any());
    }

    private static List<String> getRangeIdsRemovedBy(final int startInclusive, final int endExclusive, final List<AuthorizationProcessedDltJpaEntity> entities) {
        return entities.subList(startInclusive, endExclusive)
            .stream()
            .map(AuthorizationProcessedDltJpaEntity::getId)
            .toList();
    }

}
