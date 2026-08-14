package com.msm.core.commons;

public final class Utils {
    public static final StringUtils STR = new StringUtils();
    public static final RandomUtils R = new RandomUtils();
    public static final NumberUtils N = new NumberUtils();
    public static final DateUtils DATES = new DateUtils();
    public static final CollectionUtils CL = new CollectionUtils();
    public static final ArrayUtils ARRAYS = new ArrayUtils();
    public static final ObjectUtils O = new ObjectUtils();
    public static final RequestUtils REQ = new RequestUtils();
    public static final CompositeKeys KEYS = new CompositeKeys();

    public static Integer getStartIndex(int page, int size) {
        return (page - 1) * size + 1;
    }

    public static int getEndIndex(int page, int size) {

        return getStartIndex(page, size) + size - 1;
    }

    private Utils() {
    }
}
