package com.msm.core.filter.domain;

import java.util.Base64;

public class CursorUtils {

    public static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes());
    }

    public static String decode(String cursor) {
        return new String(Base64.getDecoder().decode(cursor));
    }

}