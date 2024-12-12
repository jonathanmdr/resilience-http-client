package com.resilience.domain.authorization;

import com.resilience.domain.StubId;
import com.resilience.domain.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorizationIdTest {

    @Test
    void shouldCreateAuthorizationId() {
        final AuthorizationId authorizationId = AuthorizationId.unique(UUID.randomUUID().toString());
        assertThat(authorizationId)
            .isNotNull()
            .satisfies(generatedAuthorizationId -> assertThat(generatedAuthorizationId)
                .extracting(AuthorizationId::value)
                .isExactlyInstanceOf(String.class)
                .isNotNull()
            );
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldBeThrowAnDomainErrorWhenTryCreateAuthorizationIdWithBlankValue(final String value) {
        assertThatThrownBy(() -> AuthorizationId.unique(value))
            .isExactlyInstanceOf(DomainException.class)
            .hasMessage("Reference cannot be null or empty");
    }

    @Test
    void shouldCreateAuthorizationIdFrom() {
        final AuthorizationId authorizationId = AuthorizationId.from("authorization-id");
        assertThat(authorizationId)
            .isNotNull()
            .satisfies(generatedAuthorizationId -> assertThat(generatedAuthorizationId)
                .extracting(AuthorizationId::value)
                .isExactlyInstanceOf(String.class)
                .isEqualTo("authorization-id")
            );
    }

    @Test
    void shouldReturnAuthorizationIdValue() {
        final AuthorizationId authorizationId = AuthorizationId.from("authorization-id");
        assertThat(authorizationId.value())
            .isExactlyInstanceOf(String.class)
            .isEqualTo("authorization-id");
    }

    @Test
    void shouldBeTheSameAuthorizationId() {
        final AuthorizationId authorizationId = AuthorizationId.from("authorization-id");
        final AuthorizationId authorizationId2 = AuthorizationId.from("authorization-id");
        assertThat(authorizationId)
            .isEqualTo(authorizationId2)
            .hasSameHashCodeAs(authorizationId2);
    }

    @Test
    void shouldNotBeTheSameAuthorizationId() {
        final AuthorizationId authorizationId = AuthorizationId.from("authorization-id");
        final AuthorizationId authorizationId2 = AuthorizationId.from("authorization-id-2");
        assertThat(authorizationId)
            .isNotEqualTo(authorizationId2)
            .doesNotHaveSameHashCodeAs(authorizationId2);
    }

    @Test
    void shouldNotBeTheSameAuthorizationIdByInstances() {
        final AuthorizationId authorizationId = AuthorizationId.from("authorization-id");
        final StubId stubId = StubId.from("stub-id");
        assertThat(authorizationId).isNotEqualTo(stubId); // NOSONAR
    }

}
