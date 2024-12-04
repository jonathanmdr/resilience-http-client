package com.resiliente.orderapi.api.controllers;

import com.resilience.application.authorization.authorize.AuthorizeOrderInput;
import com.resilience.application.authorization.authorize.AuthorizeOrderOutput;
import com.resilience.application.authorization.authorize.AuthorizeOrderUseCase;
import com.resilience.application.order.create.CreateOrderInput;
import com.resilience.application.order.create.CreateOrderOutput;
import com.resilience.application.order.create.CreateOrderUseCase;
import com.resilience.application.order.get.GetOrderByIdInput;
import com.resilience.application.order.get.GetOrderByIdOutput;
import com.resilience.application.order.get.GetOrderByIdUseCase;
import com.resilience.domain.common.Result;
import com.resilience.domain.exception.DomainException;
import com.resilience.domain.validation.ValidationHandler;
import com.resiliente.orderapi.api.OrderOpenApi;
import com.resiliente.orderapi.autorization.models.AuthorizeOrderResponse;
import com.resiliente.orderapi.autorization.presenter.AuthorizationPresenter;
import com.resiliente.orderapi.order.models.CreateOrderRequest;
import com.resiliente.orderapi.order.models.CreateOrderResponse;
import com.resiliente.orderapi.order.models.GetOrderByIdResponse;
import com.resiliente.orderapi.order.presenter.OrderPresenter;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class OrderController implements OrderOpenApi {

    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final CreateOrderUseCase createOrderUseCase;
    private final AuthorizeOrderUseCase authorizeOrderUseCase;

    public OrderController(final GetOrderByIdUseCase getOrderByIdUseCase, final CreateOrderUseCase createOrderUseCase, final AuthorizeOrderUseCase authorizeOrderUseCase) {
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.createOrderUseCase = createOrderUseCase;
        this.authorizeOrderUseCase = authorizeOrderUseCase;
    }

    @RateLimiter(name = "getOrderByIdRateLimiter", fallbackMethod = "rateLimiterFallback")
    @Override
    public ResponseEntity<GetOrderByIdResponse> getOrderById(final String orderId) {
        final GetOrderByIdInput input = GetOrderByIdInput.with(orderId);
        final Result<GetOrderByIdOutput, ValidationHandler> result = this.getOrderByIdUseCase.execute(input);

        if (result.hasError()) {
            final ValidationHandler validationHandler = result.error();
            throw DomainException.with("Command to retrieval resource cannot be processed", validationHandler.errors());
        }

        final GetOrderByIdOutput output = result.success();

        return ResponseEntity.ok(OrderPresenter.from(output));
    }

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder(final CreateOrderRequest request) {
        final CreateOrderInput input = CreateOrderInput.with(request.customerId(), request.amount());
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

    @Override
    public ResponseEntity<AuthorizeOrderResponse> authorizeOrder(final String orderId) {
        final AuthorizeOrderInput input = AuthorizeOrderInput.with(orderId);
        final Result<AuthorizeOrderOutput, ValidationHandler> result = this.authorizeOrderUseCase.execute(input);

        if (result.hasError()) {
            final ValidationHandler validationHandler = result.error();
            throw DomainException.with("Order cannot be authorized", validationHandler.errors());
        }

        final AuthorizeOrderOutput output = result.success();

        return ResponseEntity.ok(AuthorizationPresenter.from(output));
    }

}
