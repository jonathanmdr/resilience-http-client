package com.resilience.authorizationapi.api;

import com.resilience.authorizationapi.api.GlobalExceptionHandler.ApiError;
import com.resilience.authorizationapi.featuretoggle.Feature;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import static com.resilience.authorizationapi.api.FeatureToggleOpenApi.*;

@RouterOperations(
    value = {
        @RouterOperation(
            method = RequestMethod.GET,
            path = "/v1/features",
            operation = @Operation(
                operationId = "listFeatureToggles",
                description = "List feature toggles",
                summary = "List feature toggles",
                tags = {
                    "Feature Toggle"
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Resource retrieved successfully",
                        content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                implementation = ListFeatureResponseOpenApi.class
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
        ),
        @RouterOperation(
            method = RequestMethod.PATCH,
            path = "/v1/features/{feature}/enable",
            operation = @Operation(
                operationId = "enableFeatureToggle",
                description = "Enable feature toggle",
                summary = "Enable feature toggle",
                tags = {
                    "Feature Toggle"
                },
                parameters = {
                    @Parameter(
                        name = "feature",
                        description = "Feature identifier",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(
                            type = "string",
                            implementation = Feature.class
                        )
                    )
                },
                responses = {
                    @ApiResponse(
                        responseCode = "204",
                        description = "Feature toggle enabled successfully"
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
        ),
        @RouterOperation(
            method = RequestMethod.PATCH,
            path = "/v1/features/{feature}/disable",
            operation = @Operation(
                operationId = "disableFeatureToggle",
                description = "Disable feature toggle",
                summary = "Disable feature toggle",
                tags = {
                    "Feature Toggle"
                },
                parameters = {
                    @Parameter(
                        name = "feature",
                        description = "Feature identifier",
                        required = true,
                        in = ParameterIn.PATH,
                        schema = @Schema(
                            type = "string",
                            implementation = Feature.class
                        )
                    )
                },
                responses = {
                    @ApiResponse(
                        responseCode = "204",
                        description = "Feature toggle disabled successfully"
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
public @interface FeatureToggleOpenApi {

    abstract class ListFeatureResponseOpenApi implements Map<Feature, Boolean> { }

}
