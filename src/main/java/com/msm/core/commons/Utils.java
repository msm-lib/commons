package com.msm.core.commons;

import java.util.concurrent.ThreadLocalRandom;

public final class Utils {
    public static final int DEFAULT_LENGTH_CODE = 7;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final StringUtils STR = new StringUtils();
    public static final NumberUtils N = new NumberUtils();
    public static final DateUtils DATES = new DateUtils();
    public static final CollectionUtils CL = new CollectionUtils();
    public static final ArrayUtils ARRAYS = new ArrayUtils();
    public static final ObjectUtils O = new ObjectUtils();
    public static final RequestUtils REQ = new RequestUtils();
    public static final CompositeKeys KEYS = new CompositeKeys();

    public static String toCodeGenerator(String prefix, int length) {
        if (length <= 0) {
            length = DEFAULT_LENGTH_CODE;
        }
        StringBuilder stringBuilderCode = new StringBuilder(prefix);
        for (int i = 0; i < length; i++) {
            stringBuilderCode.append(ALPHA_NUMERIC_STRING.charAt(ThreadLocalRandom.current().nextInt(ALPHA_NUMERIC_STRING.length())));
        }
        return stringBuilderCode.toString();
    }

    public static Integer getStartIndex(int page, int size) {
        return (page - 1) * size + 1;
    }

    public static int getEndIndex(int page, int size) {

        return getStartIndex(page, size) + size - 1;
    }

    private Utils() {
    }
}
