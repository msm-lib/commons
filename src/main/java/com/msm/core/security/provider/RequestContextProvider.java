package com.msm.core.security.provider;

import com.msm.core.security.context.RequestContext;

public interface RequestContextProvider {
    RequestContext getRequestContext();
}
