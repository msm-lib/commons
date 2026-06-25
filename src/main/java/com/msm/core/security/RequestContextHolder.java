package com.msm.core.security;

import com.msm.core.security.context.AuthorizationContext;
import com.msm.core.security.context.DataScopeContext;
import com.msm.core.security.context.RequestContext;
import com.msm.core.security.provider.RequestContextProvider;
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

        @Override
        public boolean isSupperAdmin() {
            return false;
        }

        @Override
        public AuthorizationContext getAuthorization() {
            return AuthorizationContext.DEFAULT_AUTHORIZATION_CONTEXT;
        }

        @Override
        public DataScopeContext getDataScopeContext() {
            return DataScopeContext.DEFAULT_DATA_SCOPE_CONTEXT;
        }
    };

    public static RequestContext getRequestContext() {
        return requestContextProvider.getRequestContext();
    }
}
