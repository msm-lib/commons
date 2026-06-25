package com.msm.core.metadata.ref;

import com.msm.core.commons.object.PropertyPathParser;
import com.msm.core.commons.object.PropertyToken;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PropertyPathCache {

    private PropertyPathCache() {
    }

    private static final Map<String, List<PropertyToken>> CACHE =
            new ConcurrentHashMap<>();

    public static List<PropertyToken> parse(String path) {

        return CACHE.computeIfAbsent(
                path,
                PropertyPathParser::parse
        );
    }
}
