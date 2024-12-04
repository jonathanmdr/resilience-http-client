package com.resilience.orderapi.authorization.integration;

import com.resilience.orderapi.JacksonIntegrationTest;
import com.resiliente.orderapi.autorization.integration.AuthorizationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonIntegrationTest
class AuthorizationResponseIntegrationTest {

    @Autowired
    private JacksonTester<AuthorizationResponse> jacksonTester;

    @Test
    void testMarshall() throws IOException {
        final AuthorizationResponse response = AuthorizationResponse.with("APPROVED");
        final JsonContent<AuthorizationResponse> jsonContent = this.jacksonTester.write(response);

        assertThat(jsonContent)
            .hasJsonPathStringValue("$.status", response.status());
    }

    @Test
    void testUnmarshall() throws IOException {
        final AuthorizationResponse response = AuthorizationResponse.with("APPROVED");
        final String json = """
            {
              "status": "APPROVED"
            }
            """;
        final AuthorizationResponse actual = this.jacksonTester.parseObject(json);

        assertThat(actual)
            .isNotNull()
            .isEqualTo(response);
    }

}
