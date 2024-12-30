package com.resilience.authorizationapi.featuretoggle;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureTest {

    @ParameterizedTest
    @CsvSource(
        value = {
            "LATENCY, 4000",
            "UNAVAILABLE, 503",
            "TIMEOUT, 504"
        }
    )
    void validateAllValues(final Feature feature, final int expected) {
        assertThat(feature.value())
            .isNotNegative()
            .isEqualTo(expected);
    }

}
