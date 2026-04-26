package com.msm.core.dynamicquery;

import java.util.UUID;

public class UserQuerySecurityContext {
    private static UserContextProvider provider;
    public static void setProvider(UserContextProvider p) {
        provider = p;
    }

    public static UUID getUserId() {
        return provider != null ? provider.getUserId() : null;
    }

    public static String getUsername() {
        return provider != null ? provider.getUsername() : null;
    }
}
