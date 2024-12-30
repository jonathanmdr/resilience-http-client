package com.resilience.authorizationapi.featuretoggle;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public final class FeatureToggle {

    private static final Map<Feature, Boolean> features = new EnumMap<>(Feature.class);

    static {
        Arrays.asList(Feature.values())
            .forEach(FeatureToggle::disable);
    }

    private FeatureToggle() { }

    public static void enable(final Feature feature) {
        features.put(feature, true);
    }

    public static void disable(final Feature feature) {
        features.put(feature, false);
    }

    public static boolean isEnabled(final Feature feature) {
        return features.getOrDefault(feature, false);
    }

    public static Map<Feature, Boolean> features() {
        return features;
    }

}
