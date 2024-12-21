package com.resilience.orderapi.order.models;

import com.resilience.orderapi.JacksonIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonIntegrationTest
class CreateOrderRequestIntegrationTest {

    @Autowired
    private JacksonTester<CreateOrderRequest> jacksonTester;

    @Test
    void testMarshall() throws IOException {
        final CreateOrderRequest createOrderRequest = new CreateOrderRequest(UUID.randomUUID().toString(), BigDecimal.valueOf(11.99));
        final JsonContent<CreateOrderRequest> jsonContent = this.jacksonTester.write(createOrderRequest);

        assertThat(jsonContent)
            .hasJsonPathStringValue("$.customer_id", createOrderRequest.customerId())
            .hasJsonPathNumberValue("$.amount", createOrderRequest.amount());
    }

    @Test
    void testUnmarshall() throws IOException {
        final CreateOrderRequest createOrderRequest = new CreateOrderRequest(UUID.randomUUID().toString(), BigDecimal.valueOf(11.99));
        final String json = """
            {
              "customer_id": "%s",
              "amount": 11.99
            }
            """.formatted(createOrderRequest.customerId());
        final CreateOrderRequest actual = this.jacksonTester.parseObject(json);

        assertThat(actual)
            .isNotNull()
            .isEqualTo(createOrderRequest);
    }

}
