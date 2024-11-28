package com.resilience.domain.authorization;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationStatusTest {

    @Test
    void whenPendingHasApproveThenReturnApproved() {
        final AuthorizationStatus status = AuthorizationStatus.PENDING;
        final AuthorizationStatus actual = status.approve();
        assertThat(actual).isEqualTo(AuthorizationStatus.APPROVED);
    }

    @Test
    void whenPendingHasRefuseThenReturnRefused() {
        final AuthorizationStatus status = AuthorizationStatus.PENDING;
        final AuthorizationStatus actual = status.refuse();
        assertThat(actual).isEqualTo(AuthorizationStatus.REFUSED);
    }

    @Test
    void whenApprovedHasApproveThenReturnApproved() {
        final AuthorizationStatus status = AuthorizationStatus.APPROVED;
        final AuthorizationStatus actual = status.approve();
        assertThat(actual).isEqualTo(AuthorizationStatus.APPROVED);
    }

    @Test
    void whenApprovedHasRefuseThenReturnApproved() {
        final AuthorizationStatus status = AuthorizationStatus.APPROVED;
        final AuthorizationStatus actual = status.refuse();
        assertThat(actual).isEqualTo(AuthorizationStatus.APPROVED);
    }

    @Test
    void whenRefusedHasApproveThenReturnRefused() {
        final AuthorizationStatus status = AuthorizationStatus.REFUSED;
        final AuthorizationStatus actual = status.approve();
        assertThat(actual).isEqualTo(AuthorizationStatus.REFUSED);
    }

    @Test
    void whenRefusedHasRefuseThenReturnRefused() {
        final AuthorizationStatus status = AuthorizationStatus.REFUSED;
        final AuthorizationStatus actual = status.refuse();
        assertThat(actual).isEqualTo(AuthorizationStatus.REFUSED);
    }

}
