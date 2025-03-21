package com.resilience.orderapi.api;

import com.resilience.domain.exception.NoStacktraceException;
import com.resilience.orderapi.api.GlobalExceptionHandler.ApiError;
import com.resilience.orderapi.autorization.models.AuthorizeOrderResponse;
import com.resilience.orderapi.order.models.CreateOrderRequest;
import com.resilience.orderapi.order.models.CreateOrderResponse;
import com.resilience.orderapi.order.models.GetOrderByIdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "Orders")
@RequestMapping("/v1/orders")
public interface OrderOpenApi {

    @Operation(summary = "Get order by id")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Resource retrieved successfully",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        implementation = GetOrderByIdResponse.class
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Requested invalid",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        implementation = ApiError.class
                    )
                )
            ),
            @ApiResponse(
                responseCode = "422",
                description = "Request body or params is invalid",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        implementation = ApiError.class
                    )
                )
            ),
            @ApiResponse(
                responseCode = "429",
                description = "Rate limit exceeded",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        implementation = ApiError.class
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "An unexpected internal server error",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        implementation = ApiError.class
                    )
                )
            )
        }
    )
    @GetMapping(value = "/{order_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GetOrderByIdResponse> getOrderById(
        @Parameter(
            name = "order_id",
            description = "The order id to retrieve",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "string", implementation = UUID.class),
            example = "123e4567-e89b-12d3-a456-426614174000"
        )
        @PathVariable("order_id") @NotBlank final String orderId
    );

    @Operation(summary = "Create a new order")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201",
                description = "Resource created successfully",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        implementation = CreateOrderResponse.class
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Requested invalid",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        implementation = ApiError.class
                    )
                )
            ),
            @ApiResponse(
                responseCode = "422",
                description = "Request body or params is invalid",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        implementation = ApiError.class
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "An unexpected internal server error",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        implementation = ApiError.class
                    )
                )
            )
        }
    )
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<CreateOrderResponse> createOrder(@RequestBody @Valid @NotNull final CreateOrderRequest request);

    @Operation(summary = "Authorize an order")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Resource processed successfully",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        implementation = AuthorizeOrderResponse.class
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Requested invalid",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        implementation = ApiError.class
                    )
                )
            ),
            @ApiResponse(
                responseCode = "422",
                description = "Request body or params is invalid",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        implementation = ApiError.class
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "An unexpected internal server error",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                        implementation = ApiError.class
                    )
                )
            )
        }
    )
    @PostMapping(value = "/{order_id}/authorize", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AuthorizeOrderResponse> authorizeOrder(
        @Parameter(
            name = "order_id",
            description = "The order id to retrieve",
            required = true,
            in = ParameterIn.PATH,
            schema = @Schema(type = "string", implementation = UUID.class),
            example = "123e4567-e89b-12d3-a456-426614174000"
        )
        @PathVariable("order_id") @NotBlank final String orderId
    );

    @SuppressWarnings("all")
    default ResponseEntity<ApiError> rateLimiterFallback(final Exception exception) throws Exception {
        if (exception instanceof NoStacktraceException) {
            throw exception;
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .body(ApiError.from(exception));
    }

}
