package com.msm.core.dynamicquery.context;

import java.util.UUID;

public interface RequestContext {
    UUID getUserId();
    String getUsername();
}
