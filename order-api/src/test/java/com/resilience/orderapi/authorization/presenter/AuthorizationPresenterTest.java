package com.resilience.orderapi.authorization.presenter;

import com.resilience.application.authorization.authorize.AuthorizeOrderOutput;
import com.resiliente.orderapi.autorization.models.AuthorizeOrderResponse;
import com.resiliente.orderapi.autorization.presenter.AuthorizationPresenter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class AuthorizationPresenterTest {

    @Test
    void givenAnAuthorizeOrderOutputWhenFromIsCalledThenReturnAuthorizeOrderResponse() {
        final AuthorizeOrderOutput output = new AuthorizeOrderOutput("1234", "APPROVED");
        final AuthorizeOrderResponse response = AuthorizationPresenter.from(output);

        assertSoftly(softly -> {
            softly.assertThat(response.authorizationId()).isEqualTo(output.authorizationId());
            softly.assertThat(response.status()).isEqualTo(output.status());
        });
    }

}
