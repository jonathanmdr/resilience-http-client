package com.resiliente.orderapi.api;

import com.resiliente.orderapi.api.GlobalExceptionHandler.ApiError;
import com.resiliente.orderapi.order.models.CreateOrderRequest;
import com.resiliente.orderapi.order.models.CreateOrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Orders")
@RequestMapping("/v1/orders")
public interface OrderOpenApi {

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

}
