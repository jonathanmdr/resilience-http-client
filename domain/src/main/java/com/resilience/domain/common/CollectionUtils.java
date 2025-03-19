package com.resilience.domain.common;

import java.util.Collection;

public final class CollectionUtils {

    private CollectionUtils() { }

    public static <T> boolean isNullOrEmpty(final Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean isNotNullOrEmpty(final Collection<T> collection) {
        return !isNullOrEmpty(collection);
    }

}
