package com.resilience.authorizationapi.authorization.handlers;

import com.resilience.authorizationapi.authorization.models.AuthorizationRequest;
import com.resilience.authorizationapi.authorization.models.AuthorizationResponse;
import com.resilience.authorizationapi.authorization.persistence.AuthorizationJpaEntity;
import com.resilience.authorizationapi.authorization.persistence.AuthorizationRepository;
import com.resilience.authorizationapi.common.ValidationException;
import com.resilience.authorizationapi.featuretoggle.Feature;
import com.resilience.authorizationapi.featuretoggle.FeatureToggle;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;

@Component
public class AuthorizeOrderHandler implements HandlerFunction<ServerResponse> {

    private static final BigDecimal AUTHORIZATION_LIMIT_VALUE = BigDecimal.valueOf(500);

    private final AuthorizationRepository authorizationRepository;
    private final SmartValidator smartValidator;

    public AuthorizeOrderHandler(final AuthorizationRepository authorizationRepository, final SmartValidator smartValidator) {
        this.authorizationRepository = authorizationRepository;
        this.smartValidator = smartValidator;
    }

    @SuppressWarnings("all")
    @Override
    public ServerResponse handle(final ServerRequest serverRequest) throws Exception {
        if (FeatureToggle.isEnabled(Feature.LATENCY)) {
            Thread.currentThread().sleep(Feature.LATENCY.value());
        }

        if (FeatureToggle.isEnabled(Feature.UNAVAILABLE)) {
            return ServerResponse.status(Feature.UNAVAILABLE.value()).build();
        }

        if (FeatureToggle.isEnabled(Feature.TIMEOUT)) {
            return ServerResponse.status(Feature.TIMEOUT.value()).build();
        }

        final AuthorizationRequest request = serverRequest.body(AuthorizationRequest.class);
        final BindingResult bindingResult = new BeanPropertyBindingResult(this, request.getClass().getSimpleName());
        this.smartValidator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        final Optional<AuthorizationJpaEntity> entityExists = this.authorizationRepository.findById(request.authorizationId());

        if (entityExists.isPresent()) {
            return ServerResponse.ok()
                .body(entityExists.get().toResponse());
        }

        final boolean shouldBeApproved = AUTHORIZATION_LIMIT_VALUE.compareTo(request.orderAmount()) >= 0;
        AuthorizationJpaEntity entity = AuthorizationJpaEntity.refuse(request);
        if (shouldBeApproved) {
            entity = AuthorizationJpaEntity.approve(request);
        }

        final URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(request.authorizationId())
            .toUri();
        final AuthorizationResponse response = this.authorizationRepository.save(entity).toResponse();

        return ServerResponse.created(location)
            .body(response);
    }

}
