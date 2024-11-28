package com.resilience.domain.authorization;

import com.resilience.domain.StubId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationIdTest {

    @Test
    void shouldCreateAuthorizationId() {
        final AuthorizationId authorizationId = AuthorizationId.unique();
        assertThat(authorizationId)
            .isNotNull()
            .satisfies(generatedAuthorizationId -> assertThat(generatedAuthorizationId)
                .extracting(AuthorizationId::value)
                .isExactlyInstanceOf(String.class)
                .isNotNull()
            );
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
        assertThat(authorizationId).isNotEqualTo(stubId);
    }

}
