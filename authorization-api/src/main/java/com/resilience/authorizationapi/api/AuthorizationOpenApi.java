package com.resilience.authorizationapi.api;

import com.resilience.authorizationapi.api.GlobalExceptionHandler.ApiError;
import com.resilience.authorizationapi.authorization.models.AuthorizationRequest;
import com.resilience.authorizationapi.authorization.models.AuthorizationResponse;
import com.resilience.authorizationapi.authorization.models.GetAuthorizationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;

@RouterOperations(
    value = {
        @RouterOperation(
            method = RequestMethod.POST,
            path = "/v1/authorizations",
            operation = @Operation(
                operationId = "authorizeOrder",
                description = "Authorize order",
                summary = "Authorize order",
                tags = {
                    "Authorization"
                },
                requestBody = @RequestBody(
                    description = "Authorization request",
                    required = true,
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(
                            implementation = AuthorizationRequest.class
                        )
                    )
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Authorization processed previously successfully",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                implementation = AuthorizationResponse.class
                            )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "201",
                        description = "Authorization processed successfully",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                implementation = AuthorizationResponse.class
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
        ),
        @RouterOperation(
            method = RequestMethod.GET,
            path = "/v1/authorizations/{authorization_id}",
            operation = @Operation(
                operationId = "getAuthorization",
                description = "Get authorization",
                summary = "Get authorization",
                tags = {
                    "Authorization"
                },
                parameters = {
                    @Parameter(
                        name = "authorization_id",
                        description = "Authorization ID",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(
                            type = "string",
                            implementation = UUID.class
                        )
                    )
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Resource retrieved successfully",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                implementation = GetAuthorizationResponse.class
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
                        responseCode = "404",
                        description = "Resource not found",
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
        )
    }
)
@Target(
    value = {
        ElementType.METHOD,
        ElementType.TYPE
    }
)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizationOpenApi {

}
