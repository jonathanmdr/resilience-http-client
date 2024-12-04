package com.resilience.orderapi.authorization.models;

import com.resilience.orderapi.JacksonIntegrationTest;
import com.resiliente.orderapi.autorization.models.AuthorizeOrderResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonIntegrationTest
class AuthorizeOrderResponseIntegrationTest {

    @Autowired
    private JacksonTester<AuthorizeOrderResponse> jacksonTester;

    @Test
    void testMarshall() throws IOException {
        final AuthorizeOrderResponse response = AuthorizeOrderResponse.with("1234", "APPROVED");
        final JsonContent<AuthorizeOrderResponse> jsonContent = this.jacksonTester.write(response);

        assertThat(jsonContent)
            .hasJsonPathStringValue("$.authorization_id", response.authorizationId())
            .hasJsonPathStringValue("$.status", response.status());
    }

    @Test
    void testUnmarshall() throws IOException {
        final AuthorizeOrderResponse response = AuthorizeOrderResponse.with("1234", "APPROVED");
        final String json = """
            {
              "authorization_id": "1234",
              "status": "APPROVED"
            }
            """;
        final AuthorizeOrderResponse actual = this.jacksonTester.parseObject(json);

        assertThat(actual)
            .isNotNull()
            .isEqualTo(response);
    }

}
