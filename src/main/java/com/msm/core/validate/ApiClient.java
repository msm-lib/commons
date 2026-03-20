package com.msm.core.validate;

import java.util.Map;

public interface ApiClient {
    <T> T callApi(String baseUrl, String path, String method, Object requestBody, Class<T> responseType, Map<String, String> headers);
    <R> R get(String fullUrl, String method, Class<R> responseType);
}
