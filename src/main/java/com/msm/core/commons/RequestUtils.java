package com.msm.core.commons;

import java.util.Map;

public final class RequestUtils {
    RequestUtils() {}

    public String resolvePathApi(String api, Map<String, String> params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            api = api.replace(entry.getKey(), entry.getValue());
        }
        return api;
    }
}
