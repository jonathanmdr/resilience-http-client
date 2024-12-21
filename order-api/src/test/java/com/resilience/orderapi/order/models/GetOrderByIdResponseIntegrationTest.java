package com.resilience.orderapi.order.models;

import com.resilience.domain.order.OrderStatus;
import com.resilience.orderapi.JacksonIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@JacksonIntegrationTest
class GetOrderByIdResponseIntegrationTest {

    @Autowired
    private JacksonTester<GetOrderByIdResponse> jacksonTester;

    @Test
    void testMarshall() throws IOException {
        final GetOrderByIdResponse getOrderByIdResponse = new GetOrderByIdResponse("1234", "4321", BigDecimal.valueOf(11.99), OrderStatus.CREATED.name());
        final JsonContent<GetOrderByIdResponse> jsonContent = this.jacksonTester.write(getOrderByIdResponse);

        assertThat(jsonContent)
            .hasJsonPathStringValue("$.order_id", getOrderByIdResponse.orderId())
            .hasJsonPathStringValue("$.customer_id", getOrderByIdResponse.customerId())
            .hasJsonPathNumberValue("$.amount", getOrderByIdResponse.amount())
            .hasJsonPathStringValue("$.status", getOrderByIdResponse.status());
    }

    @Test
    void testUnmarshall() throws IOException {
        final GetOrderByIdResponse getOrderByIdResponse = new GetOrderByIdResponse("1234", "4321", BigDecimal.valueOf(11.99), OrderStatus.CREATED.name());
        final String json = """
            {
              "order_id": "%s",
              "customer_id": "%s",
              "amount": 11.99,
              "status": "CREATED"
            }
            """.formatted(getOrderByIdResponse.orderId(), getOrderByIdResponse.customerId());
        final GetOrderByIdResponse actual = this.jacksonTester.parseObject(json);

        assertThat(actual)
            .isNotNull()
            .isEqualTo(getOrderByIdResponse);
    }

}
