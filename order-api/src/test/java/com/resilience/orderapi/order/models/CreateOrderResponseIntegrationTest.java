package com.resilience.orderapi.order.models;

import com.resilience.orderapi.JacksonIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonIntegrationTest
class CreateOrderResponseIntegrationTest {

    @Autowired
    private JacksonTester<CreateOrderResponse> jacksonTester;

    @Test
    void testMarshall() throws IOException {
        final CreateOrderResponse createOrderResponse = new CreateOrderResponse(UUID.randomUUID().toString());
        final JsonContent<CreateOrderResponse> jsonContent = this.jacksonTester.write(createOrderResponse);

        assertThat(jsonContent)
            .hasJsonPathStringValue("$.order_id", createOrderResponse.orderId());
    }

    @Test
    void testUnmarshall() throws IOException {
        final CreateOrderResponse createOrderResponse = new CreateOrderResponse(UUID.randomUUID().toString());
        final String json = """
            {
              "order_id": "%s"
            }
            """.formatted(createOrderResponse.orderId());
        final CreateOrderResponse actual = this.jacksonTester.parseObject(json);

        assertThat(actual)
            .isNotNull()
            .isEqualTo(createOrderResponse);
    }

}
