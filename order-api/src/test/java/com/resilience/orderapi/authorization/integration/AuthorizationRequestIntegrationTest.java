package com.resilience.orderapi.authorization.integration;

import com.resilience.orderapi.JacksonIntegrationTest;
import com.resilience.orderapi.autorization.integration.AuthorizationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonIntegrationTest
class AuthorizationRequestIntegrationTest {

    @Autowired
    private JacksonTester<AuthorizationRequest> jacksonTester;

    @Test
    void testMarshall() throws IOException {
        final AuthorizationRequest request = AuthorizationRequest.with("1234", "4321", "5678", BigDecimal.valueOf(11.99));
        final JsonContent<AuthorizationRequest> jsonContent = this.jacksonTester.write(request);

        assertThat(jsonContent)
            .hasJsonPathStringValue("$.authorization_id", request.authorizationId())
            .hasJsonPathStringValue("$.order_id", request.orderId())
            .hasJsonPathStringValue("$.customer_id", request.customerId())
            .hasJsonPathNumberValue("$.order_amount", request.orderAmount());
    }

    @Test
    void testUnmarshall() throws IOException {
        final AuthorizationRequest request = AuthorizationRequest.with("1234", "4321", "5678", BigDecimal.valueOf(11.99));
        final String json = """
            {
              "authorization_id": "1234",
              "order_id": "4321",
              "customer_id": "5678",
              "order_amount": 11.99
            }
            """;
        final AuthorizationRequest actual = this.jacksonTester.parseObject(json);

        assertThat(actual)
            .isNotNull()
            .isEqualTo(request);
    }

}
