package com.msm.core.dynamicquery.context;

import lombok.Setter;

import java.util.UUID;

public class RequestContextHolder {

    @Setter
    private static RequestContextProvider requestContextProvider = () ->  new RequestContext() {

        @Override
        public UUID getUserId() {
            return UUID.fromString("00000000-0000-0000-0000-000000000000");
        }

        @Override
        public String getUsername() {
            return "SYSTEM";
        }
    };

    public static RequestContext getRequestContext() {
        return requestContextProvider.getRequestContext();
    }
}
