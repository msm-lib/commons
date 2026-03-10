package com.msm.core.filter.domain.pageable;

public enum SortDirection {
    ASC, DESC;
    public static SortDirection from(String value) {
        if (value == null) return ASC;
        return value.equalsIgnoreCase("desc") ? DESC : ASC;
    }
}
