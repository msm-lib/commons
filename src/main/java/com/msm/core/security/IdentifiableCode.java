package com.msm.core.security;

public interface IdentifiableCode {
    String getCode();

    static <E extends Enum<E> & IdentifiableCode> E fromCode(Class<E> enumType, String code) {
        for (E value : enumType.getEnumConstants()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }

        throw new IllegalArgumentException("Unknown code: " + code);
    }
}