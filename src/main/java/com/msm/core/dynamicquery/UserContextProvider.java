package com.msm.core.dynamicquery;

import java.util.UUID;

public interface UserContextProvider {
    UUID getUserId();
    String getUsername();
}