package com.resiliente.orderapi.api.controllers;

import com.resilience.application.order.create.CreateOrderInput;
import com.resilience.application.order.create.CreateOrderOutput;
import com.resilience.application.order.create.CreateOrderUseCase;
import com.resilience.domain.common.Result;
import com.resilience.domain.exception.DomainException;
import com.resilience.domain.validation.ValidationHandler;
import com.resiliente.orderapi.api.OrderOpenApi;
import com.resiliente.orderapi.order.models.CreateOrderRequest;
import com.resiliente.orderapi.order.models.CreateOrderResponse;
import com.resiliente.orderapi.order.presenter.OrderPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class OrderController implements OrderOpenApi {

    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(final CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder(final CreateOrderRequest request) {
        final CreateOrderInput input = CreateOrderInput.with(request.customerId().toString(), request.amount());
        final Result<CreateOrderOutput, ValidationHandler> result = this.createOrderUseCase.execute(input);

        if (result.hasError()) {
            final ValidationHandler validationHandler = result.error();
            throw DomainException.with("Order cannot be processed", validationHandler.errors());
        }

        final CreateOrderOutput output = result.success();
        final URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(output.orderId())
            .toUri();

        return ResponseEntity.created(location)
            .body(OrderPresenter.from(output));
    }

}