package com.msm.core.commons;


import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CompositeKeys {
    private final String DEFAULT_DELIMITER = ":";

    public String getKey(Collection<?> objectIds, String delimiter) {
        if (objectIds == null || objectIds.isEmpty()) {
            return "";
        }
        String delim = (delimiter == null) ? DEFAULT_DELIMITER : delimiter;
        return objectIds.stream()
                .filter(Objects::nonNull)
                .map(Objects::toString)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(delim));
    }

    public String getKey(Collection<?> objectIds) {
        return getKey(objectIds, DEFAULT_DELIMITER);
    }

    public String getKeyWithDelimiter(String delimiter, Object... objectIds) {
        if (objectIds == null || objectIds.length == 0) {
            return "";
        }
        return getKey(Arrays.asList(objectIds), delimiter);
    }

    public String getKey(Object... objectIds) {
        return getKeyWithDelimiter(DEFAULT_DELIMITER, objectIds);
    }


    CompositeKeys() {}
}
