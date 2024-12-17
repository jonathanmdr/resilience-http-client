package com.resilience.orderapi.api.controllers;

import com.resilience.application.authorization.authorize.AuthorizeOrderInput;
import com.resilience.application.authorization.authorize.AuthorizeOrderOutput;
import com.resilience.application.authorization.authorize.DefaultAuthorizeOrderUseCase;
import com.resilience.application.order.create.CreateOrderInput;
import com.resilience.application.order.create.CreateOrderOutput;
import com.resilience.application.order.create.DefaultCreateOrderUseCase;
import com.resilience.application.order.get.DefaultGetOrderByIdUseCase;
import com.resilience.application.order.get.GetOrderByIdInput;
import com.resilience.application.order.get.GetOrderByIdOutput;
import com.resilience.domain.authorization.AuthorizationStatus;
import com.resilience.domain.common.Result;
import com.resilience.domain.order.OrderStatus;
import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.handler.NotificationHandler;
import com.resilience.orderapi.ApiIntegrationTest;
import com.resilience.orderapi.MockSupportTest;
import com.resiliente.orderapi.api.controllers.OrderController;
import com.resiliente.orderapi.configuration.Json;
import com.resiliente.orderapi.order.models.CreateOrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiIntegrationTest(controllers = OrderController.class)
class OrderControllerIntegrationTest extends MockSupportTest {

    @MockitoBean
    private DefaultGetOrderByIdUseCase getOrderByIdUseCase;

    @MockitoBean
    private DefaultCreateOrderUseCase createOrderUseCase;

    @MockitoBean
    private DefaultAuthorizeOrderUseCase authorizeOrderUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Override
    protected List<Object> mocks() {
        return List.of(this.getOrderByIdUseCase, this.createOrderUseCase, this.authorizeOrderUseCase);
    }

    @Test
    void givenAValidIdWhenCallsGetOrderByIdThenReturnOrder() throws Exception {
        final String orderId = UUID.randomUUID().toString();
        final String customerId = UUID.randomUUID().toString();
        final GetOrderByIdInput input = GetOrderByIdInput.with(orderId);
        final GetOrderByIdOutput output = GetOrderByIdOutput.with(orderId, customerId, BigDecimal.TEN, OrderStatus.CREATED.name());
        final Result<GetOrderByIdOutput, ValidationHandler> result = Result.success(output);

        when(this.getOrderByIdUseCase.execute(input)).thenReturn(result);

        final var request = get("/v1/orders/{orderId}", orderId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.order_id").value(output.orderId()))
            .andExpect(jsonPath("$.customer_id").value(output.customerId()))
            .andExpect(jsonPath("$.amount").value(output.amount()))
            .andExpect(jsonPath("$.status").value(output.status()));

        verify(this.getOrderByIdUseCase).execute(input);
    }

    @Test
    void givenAnInvalidIdWhenCallsGetOrderByIdThenReturnError() throws Exception {
        final String orderId = UUID.randomUUID().toString();
        final GetOrderByIdInput input = GetOrderByIdInput.with(orderId);
        final Error error = new Error("Order '%s' not found".formatted(orderId));
        final ValidationHandler handler = NotificationHandler.create(error);
        final Result<GetOrderByIdOutput, ValidationHandler> result = Result.error(handler);

        when(this.getOrderByIdUseCase.execute(input)).thenReturn(result);

        final var request = get("/v1/orders/{orderId}", orderId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.detail").value("Command to retrieval resource cannot be processed"))
            .andExpect(jsonPath("$.errors[0]").value(error.message()));

        verify(this.getOrderByIdUseCase).execute(input);
    }

    @Test
    void givenAValidDataWhenCallsCreateOrderThenReturnOrderId() throws Exception {
        final String orderId = UUID.randomUUID().toString();
        final String customerId = UUID.randomUUID().toString();
        final CreateOrderInput input = CreateOrderInput.with(customerId, BigDecimal.TEN);
        final CreateOrderRequest createOrderRequest = new CreateOrderRequest(input.customerId(), input.amount());
        final CreateOrderOutput output = CreateOrderOutput.with(orderId);
        final Result<CreateOrderOutput, ValidationHandler> result = Result.success(output);

        when(this.createOrderUseCase.execute(input)).thenReturn(result);

        final var request = post("/v1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(Json.writeValueAsString(createOrderRequest));

        this.mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/v1/orders/%s".formatted(output.orderId())))
            .andExpect(jsonPath("$.order_id").value(output.orderId()));

        verify(this.createOrderUseCase).execute(input);
    }

    @Test
    void givenAnInvalidDataWhenCallsCreateOrderThenReturnError() throws Exception {
        final String customerId = UUID.randomUUID().toString();
        final CreateOrderInput input = CreateOrderInput.with(customerId, null);
        final CreateOrderRequest createOrderRequest = new CreateOrderRequest(input.customerId(), input.amount());
        final Error error = new Error("Order amount cannot be null or negative");
        final ValidationHandler handler = NotificationHandler.create(error);
        final Result<CreateOrderOutput, ValidationHandler> result = Result.error(handler);

        when(this.createOrderUseCase.execute(input)).thenReturn(result);

        final var request = post("/v1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(Json.writeValueAsString(createOrderRequest));

        this.mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.detail").value("Order cannot be processed"))
            .andExpect(jsonPath("$.errors[0]").value(error.message()));

        verify(this.createOrderUseCase).execute(input);
    }

    @Test
    void givenAValidDataWhenCallsAuthorizeOrderThenReturnAuthorization() throws Exception {
        final String orderId = UUID.randomUUID().toString();
        final String authorizationId = UUID.randomUUID().toString();
        final AuthorizeOrderInput input = AuthorizeOrderInput.with(orderId);
        final AuthorizeOrderOutput output = AuthorizeOrderOutput.with(authorizationId, AuthorizationStatus.APPROVED.name());
        final Result<AuthorizeOrderOutput, ValidationHandler> result = Result.success(output);

        when(this.authorizeOrderUseCase.execute(input)).thenReturn(result);

        final var request = post("/v1/orders/{order_id}/authorize", orderId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.authorization_id").value(output.authorizationId()))
            .andExpect(jsonPath("$.status").value(output.status()));

        verify(this.authorizeOrderUseCase).execute(input);
    }

    @Test
    void givenAnInvalidDataWhenCallsAuthorizeOrderThenReturnError() throws Exception {
        final String orderId = UUID.randomUUID().toString();
        final AuthorizeOrderInput input = AuthorizeOrderInput.with(orderId);
        final Error error = new Error("Order has ben processed previously");
        final ValidationHandler handler = NotificationHandler.create(error);
        final Result<AuthorizeOrderOutput, ValidationHandler> result = Result.error(handler);

        when(this.authorizeOrderUseCase.execute(input)).thenReturn(result);

        final var request = post("/v1/orders/{order_id}/authorize", orderId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(request)
            .andDo(print())
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.detail").value("Order cannot be authorized"))
            .andExpect(jsonPath("$.errors[0]").value(error.message()));

        verify(this.authorizeOrderUseCase).execute(input);
    }

}
