package com.resilience.domain.authorization;

import com.resilience.domain.StubDomainEventPublisher;
import com.resilience.domain.events.DomainEvent;
import com.resilience.domain.validation.Error;
import com.resilience.domain.validation.ValidationHandler;
import com.resilience.domain.validation.handler.NotificationHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AuthorizationTest {

    @Test
    void shouldBeCreateNewAuthorization() {
        final var authorization = Authorization.create("1234", "4321", BigDecimal.TEN);
        assertSoftly(softly -> {
            softly.assertThat(authorization.id()).isNotNull();
            softly.assertThat(authorization.orderId()).isEqualTo("1234");
            softly.assertThat(authorization.customerId()).isEqualTo("4321");
            softly.assertThat(authorization.orderAmount()).isEqualTo(BigDecimal.TEN);
            softly.assertThat(authorization.status()).isEqualTo(AuthorizationStatus.PENDING);
            softly.assertThat(authorization.events()).isEmpty();
        });
    }

    @Test
    void shouldBeApproveAuthorization() {
        final var authorization = Authorization.create("1234", "4321", BigDecimal.TEN)
            .authorize(AuthorizationStatusTranslatorService.create("APPROVED"));
        assertSoftly(softly -> {
            softly.assertThat(authorization.id()).isNotNull();
            softly.assertThat(authorization.orderId()).isEqualTo("1234");
            softly.assertThat(authorization.customerId()).isEqualTo("4321");
            softly.assertThat(authorization.orderAmount()).isEqualTo(BigDecimal.TEN);
            softly.assertThat(authorization.status()).isEqualTo(AuthorizationStatus.APPROVED);
            softly.assertThat(authorization.events())
                .hasSize(1)
                .satisfies(events -> {
                    final var event = (AuthorizationProcessedEvent) events.getFirst();
                    softly.assertThat(event.authorizationId())
                        .isNotNull()
                        .isEqualTo(authorization.id().value());
                    softly.assertThat(event.orderId()).isEqualTo(authorization.orderId());
                    softly.assertThat(event.orderAmount()).isEqualTo(authorization.orderAmount());
                    softly.assertThat(event.status()).isEqualTo(AuthorizationStatus.APPROVED);
                });
        });
    }

    @Test
    void shouldBeRefuseAuthorization() {
        final var authorization = Authorization.create("1234", "4321", BigDecimal.TEN)
            .authorize(AuthorizationStatusTranslatorService.create("REFUSED"));
        assertSoftly(softly -> {
            softly.assertThat(authorization.id()).isNotNull();
            softly.assertThat(authorization.orderId()).isEqualTo("1234");
            softly.assertThat(authorization.customerId()).isEqualTo("4321");
            softly.assertThat(authorization.orderAmount()).isEqualTo(BigDecimal.TEN);
            softly.assertThat(authorization.status()).isEqualTo(AuthorizationStatus.REFUSED);
            softly.assertThat(authorization.events())
                .hasSize(1)
                .satisfies(events -> {
                    final var event = (AuthorizationProcessedEvent) events.getFirst();
                    softly.assertThat(event.authorizationId())
                        .isNotNull()
                        .isEqualTo(authorization.id().value());
                    softly.assertThat(event.orderId()).isEqualTo(authorization.orderId());
                    softly.assertThat(event.orderAmount()).isEqualTo(authorization.orderAmount());
                    softly.assertThat(event.status()).isEqualTo(AuthorizationStatus.REFUSED);
                });
        });
    }

    @Test
    void shouldBeHasEmptyEventsWhenAddNullableEvent() {
        final var authorization = Authorization.create("1234", "4321", BigDecimal.TEN);
        authorization.addEvent(null);
        assertThat(authorization.events()).isEmpty();
    }

    @Test
    void shouldBePublishDomainEvents() {
        final var authorization = Authorization.create("1234", "4321", BigDecimal.TEN);
        final var eventOne = AuthorizationProcessedEvent.with("1234", "4321", BigDecimal.TEN, AuthorizationStatus.APPROVED, Instant.now());
        final var eventTwo = AuthorizationProcessedEvent.with("1234", "4321", BigDecimal.TEN, AuthorizationStatus.APPROVED, Instant.now());
        authorization.addEvent(eventOne);
        authorization.addEvent(eventTwo);

        assertThat(authorization.events()).hasSize(2);

        final var publisher = spy(StubDomainEventPublisher.class);
        authorization.dispatch(publisher);

        assertThat(authorization.events()).isEmpty();

        final ArgumentCaptor<DomainEvent> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(publisher, times(2)).publish(eventCaptor.capture());

        assertThat(eventCaptor.getAllValues()).containsExactly(eventOne, eventTwo);
    }

    @Test
    void shouldBeIgnorePublishDomainEventsWithNullableDispatcher() {
        final var authorization = Authorization.create("1234", "4321", BigDecimal.TEN);
        final var eventOne = AuthorizationProcessedEvent.with("1234", "4321", BigDecimal.TEN, AuthorizationStatus.APPROVED, Instant.now());
        final var eventTwo = AuthorizationProcessedEvent.with("1234", "4321", BigDecimal.TEN, AuthorizationStatus.APPROVED, Instant.now());
        authorization.addEvent(eventOne);
        authorization.addEvent(eventTwo);

        assertThat(authorization.events()).hasSize(2);
        authorization.dispatch(null);
        assertThat(authorization.events()).hasSize(2);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAuthorizationToValidate")
    void shouldBeValidateAuthorization(final Authorization authorization, final Error error) {
        final ValidationHandler validationHandler = NotificationHandler.create();
        authorization.validate(validationHandler);
        assertSoftly(softly -> {
            softly.assertThat(validationHandler.hasErrors()).isTrue();
            softly.assertThat(validationHandler.errors()).isEqualTo(List.of(error));
            softly.assertThat(authorization.events()).isEmpty();
        });
    }

    private static Stream<Arguments> provideInvalidAuthorizationToValidate() {
        return Stream.of(
            Arguments.of(Authorization.create("1234", null, BigDecimal.TEN), Error.of("Customer id must not be null or blank")),
            Arguments.of(Authorization.create("1234", " ", BigDecimal.TEN), Error.of("Customer id must not be null or blank")),
            Arguments.of(Authorization.create("1234", "4321", null), Error.of("Order amount must be greater than zero")),
            Arguments.of(Authorization.create("1234", "4321", BigDecimal.ZERO), Error.of("Order amount must be greater than zero")),
            Arguments.of(Authorization.create("1234", "4321", BigDecimal.valueOf(-1)), Error.of("Order amount must be greater than zero"))
        );
    }

}
