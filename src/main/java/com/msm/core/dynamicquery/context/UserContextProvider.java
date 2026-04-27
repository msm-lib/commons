package com.msm.core.dynamicquery.context;

import java.util.UUID;

public interface UserContextProvider {
    UUID getUserId();
    String getUsername();
}