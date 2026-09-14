package com.msm.core.metadata.ref;

import java.util.List;
import java.util.Map;

public interface RefDataDefinition {
    List<String> fields();
    default Class<?> targetType() {
        return Map.class;
    }
}
