package com.resilience.authorizationapi.featuretoggle;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureToggleTest {

    @Test
    void validateAllFeaturesStartedDisabled() {
        assertThat(FeatureToggle.features())
            .hasSize(3)
            .containsEntry(Feature.LATENCY, false)
            .containsEntry(Feature.UNAVAILABLE, false)
            .containsEntry(Feature.TIMEOUT, false);
    }

    @Test
    void validateEnableFeature() {
        FeatureToggle.enable(Feature.LATENCY);
        assertThat(FeatureToggle.isEnabled(Feature.LATENCY)).isTrue();
    }

    @Test
    void validateDisableFeature() {
        FeatureToggle.disable(Feature.LATENCY);
        assertThat(FeatureToggle.isEnabled(Feature.LATENCY)).isFalse();
    }

    @Test
    void validateIsEnabledFeatureToUnknownFeature() {
        final Feature feature = Mockito.mock(Feature.class);
        assertThat(FeatureToggle.isEnabled(feature)).isFalse();
    }

}
